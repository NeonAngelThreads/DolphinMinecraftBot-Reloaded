package org.angellock.impl;


import org.angellock.impl.events.IConnectListener;
import org.angellock.impl.events.IDisconnectListener;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.providers.Plugin;
import org.angellock.impl.providers.PluginManager;
import org.angellock.impl.providers.SessionProvider;
import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerAction;
import org.geysermc.mcprotocollib.protocol.data.status.ServerStatusInfo;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundClientInformationPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundTickingStatePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundPlayerAbilitiesPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundPlayerActionPacket;
import org.geysermc.mcprotocollib.protocol.packet.login.serverbound.ServerboundKeyPacket;
import org.geysermc.mcprotocollib.protocol.packet.status.clientbound.ClientboundStatusResponsePacket;
import org.geysermc.mcprotocollib.protocol.packet.status.serverbound.ServerboundStatusRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRobot implements ISendable, SessionProvider, IOptionalProcedures {
    protected Session serverSession;
    protected MinecraftProtocol minecraftProtocol;
    protected ConfigManager config;
    protected String server;
    protected short port;
    protected String name;
    private String profileName;
    protected String password;
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&aDolphinBot"));
    protected final int TIME_OUT;
    protected final int ReconnectionDelay;
    private final PluginManager pluginManager;
    protected final Random randomizer = new Random();
    protected long connectDuration = 0;
    protected boolean isByPassedVerification = true;
    private ChatMessageManager messageManager;
    private BotManager botManager;
    private GameMode serverGamemode = GameMode.ADVENTURE;
    private ScheduledExecutorService reconnectScheduler = Executors.newScheduledThreadPool(1);
    public Collection<Plugin> enabled_base_plugin = new ArrayList<>();

    public AbstractRobot(ConfigManager configManager, PluginManager pluginManager){
        this.config = configManager;
        String playerName = (String) this.config.getConfigValue("username");
        String serverAddress = (String) this.config.getConfigValue("server");
        short serverPort = Short.parseShort((String) this.config.getConfigValue("port"));
        this.password = (String) this.config.getConfigValue("password");
        //String pluginDir = (String) this.config.getConfigValue("plugin-dir");

        this.pluginManager = pluginManager;

        this.server = serverAddress;
        this.name = playerName;
        this.port = serverPort;
        this.TIME_OUT = Integer.parseInt((String) this.config.getConfigValue("connect-timing-out"));
        this.ReconnectionDelay = Integer.parseInt((String) this.config.getConfigValue("reconnect-delay"));

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
        this.enabled_base_plugin = plugins;
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
        this.serverSession.connect(true);
        this.connectDuration = System.currentTimeMillis();

        if (this.isByPassedVerification) {
            //this.pluginManager.disableAllPlugins(this);
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

                    //Thread.onSpinWait();
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
}
