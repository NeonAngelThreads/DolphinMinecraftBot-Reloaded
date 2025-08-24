package org.angellock.impl;


import org.angellock.impl.commands.*;
import org.angellock.impl.events.IConnectListener;
import org.angellock.impl.events.IDisconnectListener;
import org.angellock.impl.events.handlers.SystemChatHandler;
import org.angellock.impl.events.packets.AddEntityPacket;
import org.angellock.impl.events.packets.PlayerPositionPacket;
import org.angellock.impl.events.packets.TeleportEntityPacket;
import org.angellock.impl.ingame.IPlayer;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.providers.Plugin;
import org.angellock.impl.providers.PluginManager;
import org.angellock.impl.providers.SessionProvider;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.PlainTextSerializer;
import org.angellock.impl.util.math.Position;
import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRobot implements ISendable, SessionProvider, IOptionalProcedures, IPlayer {
    protected Session serverSession;
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&aDolphinBot"));
    private final ScheduledExecutorService reconnectScheduler = Executors.newScheduledThreadPool(1);
    private final Map<Integer, Player> onlinePlayers = new HashMap<>();
    protected final Random randomizer = new Random();
    protected final PluginManager pluginManager;
    protected final int ReconnectionDelay;
    protected final int TIME_OUT;
    protected MinecraftProtocol minecraftProtocol;
    protected ConfigManager config;
    protected long connectDuration = 0;
    protected boolean isByPassedVerification = true;
    protected GameMode serverGamemode = GameMode.ADVENTURE;
    private ChatMessageManager messageManager;
    private BotManager botManager;
    protected Position loginPos = new Position(0d,0d,0d);
    protected String server;
    protected short port;
    protected String name;
    private String profileName;
    protected String password;
    private final CommandSpec commands = new CommandSpec();

    public AbstractRobot(ConfigManager configManager, PluginManager pluginManager){
        this.config = configManager;
        String playerName = (String) this.config.getConfigValue("username");
        String serverAddress = (String) this.config.getConfigValue("server");
        short serverPort = Short.parseShort((String) this.config.getConfigValue("port"));
        this.password = (String) this.config.getConfigValue("password");

        this.pluginManager = pluginManager;

        this.server = serverAddress;
        this.name = playerName;
        this.port = serverPort;
        this.TIME_OUT = Integer.parseInt((String) this.config.getConfigValue("connect-timing-out"));
        this.ReconnectionDelay = Integer.parseInt((String) this.config.getConfigValue("reconnect-delay"));

        this.commands.register(new CommandBuilder().withName("reload").allowedUsers(config.getConfigValue("owner")).build((act)->{
            this.pluginManager.reloadPlugin(this, act.getCommandList()[1].toLowerCase());
        }));

    }

    public AbstractRobot withName(String userName){
        this.name = userName;
        return this;
    }

    public AbstractRobot withPassword(String password){
        this.password = password;
        return this;
    }
    public AbstractRobot withDefaultPlugins(List<Plugin> plugins){
        this.pluginManager.getDefaultPlugins().addAll(plugins);
        return this;
    }
    public AbstractRobot withBotManager(BotManager botManager){
        this.botManager = botManager;
        return this;
    }
    public AbstractRobot buildProtocol(){
        this.minecraftProtocol = new MinecraftProtocol(this.name);
        return this;
    }

    public BotManager getBotManager() {
        return botManager;
    }

    public String getPassword(){
        return this.password;
    }

    public ChatMessageManager getMessageManager() {
        return messageManager;
    }

    public void connect(){
        onPreLogin();
        this.serverSession = new TcpClientSession(this.server, this.port, minecraftProtocol);

        this.messageManager = new ChatMessageManager(this.serverSession);

        this.serverSession.addListener((IConnectListener) event -> onJoin());

        this.serverSession.addListener((IDisconnectListener) event -> {
            onQuit(event.getReason().toString());
        });

        this.serverSession.addListener(new SystemChatHandler().addExtraAction((chatPacket -> {
            long timeElapse = System.currentTimeMillis();
            CommandSerializer serializer = new CommandSerializer();
            PlainTextSerializer componentSerializer = new PlainTextSerializer();
            String commandMsg = componentSerializer.serialize(chatPacket.getContent());
            CommandResponse meta = serializer.serialize(commandMsg);
            if (meta != null) {
                log.info("CommandList: {}, sender: {}", Arrays.toString(meta.getCommandList()), meta.getSender());
                Command cmd = this.commands.getCommand(meta.getCommandList()[0]);
                if(cmd != null){
                    boolean success = cmd.activate(meta);
                    if (!success){
                        this.messageManager.putMessage("[ERR]未能执行该命令。发送者未在owners白名单.请在命令行中配置。");
                    } else {
                        long time = (System.currentTimeMillis() - timeElapse);
                        if (time < 128000L) {
                            this.messageManager.putMessage("[INFO]操作已成功完成。耗时" + time + "ms");
                        }
                    }
                }
            }
        })));

        this.serverSession.addListener(new AddEntityPacket().addExtraAction((entityPacket -> {
            if (this.onlinePlayers.get(entityPacket.getEntityId()) == null) {
                this.onlinePlayers.put(entityPacket.getEntityId(), new Player(entityPacket.getEntityId(), new Position(entityPacket.getX(), entityPacket.getY(), entityPacket.getZ())));
            }
        })));
        this.serverSession.addListener(new TeleportEntityPacket().addExtraAction((teleportEntityPacket -> {
            this.onlinePlayers.get(teleportEntityPacket.getId()).setPosition(teleportEntityPacket.getPosition().getX(), teleportEntityPacket.getPosition().getX(), teleportEntityPacket.getPosition().getZ());
        })));
        this.serverSession.addListener(new PlayerPositionPacket().addExtraAction((packet->{
            log.info(ConsoleTokens.colorizeText("&7Login At Position &b{}"), packet.getPosition());
            this.loginPos.from(packet.getPosition());
        })));

        this.serverSession.connect(true);
        this.connectDuration = System.currentTimeMillis();
        if (this.isByPassedVerification) {
            this.pluginManager.loadAllPlugins(this);
        }
        try {
            while (true) {
                try {
                    Thread.sleep(100L);
                    if (!this.serverSession.isConnected()){
                        this.connectDuration = System.currentTimeMillis();
                        break;
                    }
                    this.messageManager.pollMessage();
                }
                catch (InterruptedException e){
                    this.serverSession.disconnect("Interrupted");
                    throw new RuntimeException(e);
                }
            }
        } finally {
            scheduleReconnect();
        }
    }

    public abstract boolean canSendMessages();

    public void scheduleReconnect() {
        try {
            Thread.sleep(Long.parseLong((String) this.config.getConfigValue("reconnect-delay")));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.reconnectScheduler.schedule(() -> this.connect(), 5, TimeUnit.SECONDS);
    }

    public void setBypassed(boolean bypassed) {
        this.isByPassedVerification = bypassed;
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        this.serverSession.send(packet);
    }

    @Override
    public Session getSession(){
        return this.serverSession;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public Random getRandomizer() {
        return randomizer;
    }
    public long getConnectTime() {
        return connectDuration;
    }

    public boolean isByPassedVerification() {
        return isByPassedVerification;
    }

    public String getProfileName() {
        return (this.profileName != null) ? this.profileName: this.name;
    }

    public AbstractRobot withProfileName(String name) {
        this.profileName = name;
        return this;
    }

    public GameMode getServerGamemode() {
        return serverGamemode;
    }

    public void setServerGamemode(GameMode serverGamemode) {
        this.serverGamemode = serverGamemode;
    }

    public Map<Integer, Player> getOnlinePlayers(){
        return this.onlinePlayers;
    }

    public CommandSpec getRegisteredCommands() {
        return this.commands;
    }
}
