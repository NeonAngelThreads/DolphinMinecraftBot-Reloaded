package org.angellock.impl;

import org.angellock.impl.ingame.IPlayer;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.providers.PluginManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.math.Position;
import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;

public class RobotPlayer extends AbstractRobot {
    private long connectTime;

    public RobotPlayer(ConfigManager configManager, PluginManager pluginManager) {
        super(configManager, pluginManager);
    }

    @Override
    public boolean canSendMessages() {
        return (this.serverGamemode == GameMode.SURVIVAL);
    }

    @Override
    public void onJoin() {
        this.connectTime = System.currentTimeMillis();
        log.info(ConsoleTokens.colorizeText("&7[{}] &2Connection was established!"), this.getProfileName());
    }

    @Override
    public void onQuit(String reason) {
        long millis = System.currentTimeMillis() - this.connectTime;
        log.info(ConsoleTokens.colorizeText("[{}] &7Session Duration: &f{}ms"), this.getProfileName(), millis);
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.DARK_RED + "Disconnected from the server!"));
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.GOLD + "Reason: " + ConsoleTokens.LIGHT_PURPLE + reason));
        if (this.config.getConfigValue("auto-reconnecting").equals("true")){
            log.info(ConsoleTokens.standardizeText(ConsoleTokens.DARK_BLUE + "Trying to reconnect to the server..."));

            if (reason.contains("验证")){
                log.info("bypassing");
                this.isByPassedVerification = false;
            }
            this.getPluginManager().disableAllPlugins(this);
            log.info(ConsoleTokens.colorizeText("&aTiming completed."));
            this.getSession().getChannel().close();
            this.getSession().getChannel().closeFuture();
            this.scheduleReconnect();
        }
    }

    @Override
    public void onKicked() {
        return;
    }

    @Override
    public void onPreLogin() {
        log.info(ConsoleTokens.colorizeText("&l&bAttempt to join to the server &3"+ this.server+':'+this.port +". &bWaiting for server establishing the connection..."));
    }

    @Override
    public double getDistanceFromOthers(IPlayer player) {
        return this.getPosition().getDistance(player.getPosition());
    }

    @Override
    public Position getPosition() {
        return this.loginPos;
    }
}
