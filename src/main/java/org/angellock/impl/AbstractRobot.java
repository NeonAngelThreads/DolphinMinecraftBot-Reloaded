package org.angellock.impl;

;
import org.angellock.impl.events.IConnectListener;
import org.angellock.impl.events.IDisconnectListener;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.providers.PluginManager;
import org.angellock.impl.providers.SessionProvider;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRobot implements ISendable, SessionProvider, IOptionalProcedures {
    protected Session serverSession;
    protected MinecraftProtocol minecraftProtocol;
    protected ConfigManager config;
    protected String server;
    protected short port;
    protected String name;
    protected static final Logger log = LoggerFactory.getLogger("BotEntity");
    protected final int TIME_OUT;
    protected final int ReconnectionDelay;
    protected Thread connectingThread;
    private final PluginManager pluginManager;

    public AbstractRobot(ConfigManager configManager){
        this.config = configManager;
        String playerName = (String) this.config.getConfigValue("username");
        String serverAddress = (String) this.config.getConfigValue("server");
        short serverPort = Short.parseShort((String) this.config.getConfigValue("port"));
        String pluginDir = (String) this.config.getConfigValue("plugin-dir");

        this.minecraftProtocol = new MinecraftProtocol(playerName);
        this.pluginManager = new PluginManager(pluginDir);

        this.server = serverAddress;
        this.name = playerName;
        this.port = serverPort;
        this.TIME_OUT = Integer.parseInt((String) this.config.getConfigValue("connect-timing-out"));
        this.ReconnectionDelay = Integer.parseInt((String) this.config.getConfigValue("reconnect-delay"));

        this.connectingThread = new Thread(this::connect);

    }

    public void connect(){
        onPreLogin();
        this.serverSession = new TcpClientSession(this.server, this.port, minecraftProtocol);
        this.pluginManager.loadAllPlugins(this);

        this.serverSession.addListener((IDisconnectListener) event -> onQuit(event.getReason().toString()));
        this.serverSession.addListener((IConnectListener) event -> onJoin());

        this.serverSession.connect();
        long start_time = System.currentTimeMillis();
        while (!this.serverSession.isConnected()){
            if (System.currentTimeMillis() - start_time > this.TIME_OUT) {
                this.serverSession.disconnect("Connection timing out.");
                break;
            }
        }

        try {
            while (true) {
                Thread.sleep(1L);
                Thread.onSpinWait();
            }
        }catch (InterruptedException e){
            return;
        }
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        this.serverSession.send(packet);
    }

    @Override
    public Session getSession(){
        return this.serverSession;
    }
}
