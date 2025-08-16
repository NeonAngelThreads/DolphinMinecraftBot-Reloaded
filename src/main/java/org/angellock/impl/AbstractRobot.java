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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    protected Thread connectingThread;
    private final PluginManager pluginManager;
    protected final Random randomizer = new Random();
    protected long connectDuration = 0;
    protected boolean isByPassedVerification = true;
    private ChatMessageManager messageManager;
    private BotManager botManager;
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

        this.connectingThread = new Thread(this::connect);

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
            try {
                Thread.sleep(Long.parseLong((String) this.config.getConfigValue("reconnect-delay")));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Thread.currentThread().interrupt();
            onQuit(event.getReason().toString());
        });
        this.serverSession.connect();
        this.connectDuration = System.currentTimeMillis();

        if (this.isByPassedVerification) {
            this.getPluginManager().loadAllPlugins(this);
        }

        while (true) {
            try {
                Thread.sleep(100L);
                if (!this.serverSession.isConnected()){
                    this.connectDuration = System.currentTimeMillis();
                }
                this.messageManager.pollMessage();

                Thread.onSpinWait();
            }
            catch (InterruptedException e){
                this.serverSession.disconnect("Interrupted");
                throw new RuntimeException();
            }
        }
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
}
