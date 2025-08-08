package org.angellock.impl;


import net.kyori.adventure.key.Key;
import org.angellock.impl.events.IConnectListener;
import org.angellock.impl.events.IDisconnectListener;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.providers.PluginManager;
import org.angellock.impl.providers.SessionProvider;
import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundCustomPayloadPacket;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundKeepAlivePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;
import java.util.Random;

public abstract class AbstractRobot implements ISendable, SessionProvider, IOptionalProcedures {
    protected Session serverSession;
    protected MinecraftProtocol minecraftProtocol;
    protected ConfigManager config;
    protected String server;
    protected short port;
    protected String name;
    protected String password;
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&aDolphinBot"));
    protected final int TIME_OUT;
    protected final int ReconnectionDelay;
    protected Thread connectingThread;
    private final PluginManager pluginManager;
    protected final Random randomizer = new Random();
    protected boolean isByPassedVerification = true;
    protected int verifyTimes = 0;
    protected long connectTime = 0;

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

    public AbstractRobot buildProtocol(){
        this.minecraftProtocol = new MinecraftProtocol(this.name);
        return this;
    }

    public String getPassword(){
        return this.password;
    }
    public void resetVerify(){
        verifyTimes = 0;
        isByPassedVerification = false;
    }

    public boolean isVerified(){
        return this.isByPassedVerification;
    }
    public void connect(){
        onPreLogin();
        this.serverSession = new TcpClientSession(this.server, this.port, minecraftProtocol);
        if (this.isByPassedVerification) {
            this.pluginManager.loadAllPlugins(this);
        }

        this.serverSession.addListener((IConnectListener) event -> onJoin());

        this.serverSession.addListener((IDisconnectListener) event -> {
            Thread.currentThread().interrupt();
            onQuit(event.getReason().toString());
        });
        this.serverSession.connect();
        this.connectTime = System.currentTimeMillis();

        int var = 0;
        try {

            while (true) {
                int i = 0;
                while (Math.abs(var - i) < 1){
                    i = randomizer.nextInt(1000)%8;
                }
                var = i;
                Thread.sleep(1000L*(1+var));

                if (!serverSession.isConnected()){
                    this.connectTime = System.currentTimeMillis();
                }
                else {
                    if (!this.isByPassedVerification) {
                        if(this.verifyTimes < 3){
                            this.verifyTimes++;
                            this.serverSession.disconnect("");
                        }
                        log.info(ConsoleTokens.colorizeText("&7正在进行人机验证..."));
                        if (System.currentTimeMillis() - this.connectTime > 10700L) {
                            log.info(ConsoleTokens.colorizeText("&a机器人验证已完毕."));
                            this.pluginManager.loadAllPlugins(this);
                            this.isByPassedVerification = true;
                            if(this.isVerified()){
                                this.sendPacket(new ServerboundChatCommandPacket("reg " + this.getPassword() +" "+ this.getPassword()));
                                Thread.sleep(3000L);
                                this.serverSession.disconnect("");
                            }
                        }
                    }
                }

                Thread.onSpinWait();
            }
        }catch (InterruptedException e){
            this.serverSession.disconnect("Interrupted By Client");
            throw new RuntimeException();
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
