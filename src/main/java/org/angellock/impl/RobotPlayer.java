package org.angellock.impl;

import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.providers.PluginManager;
import org.angellock.impl.util.ConsoleTokens;

public class RobotPlayer extends AbstractRobot {
    private long connectTime;

    public RobotPlayer(ConfigManager configManager, PluginManager pluginManager) {
        super(configManager, pluginManager);
    }

    @Override
    public void onJoin() {
        this.connectTime = System.currentTimeMillis();
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.DARK_GREEN + "Connection was established!"));
    }

    @Override
    public void onQuit(String reason) {
        long millis = System.currentTimeMillis() - this.connectTime;
        log.info(ConsoleTokens.colorizeText("&7Session Duration: &f{}ms"), millis);
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.DARK_RED + "Disconnected from the server!"));
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.GOLD + "Reason: " + ConsoleTokens.LIGHT_PURPLE + reason));
        if (this.config.getConfigValue("auto-reconnecting").equals("true")){
            log.info(ConsoleTokens.standardizeText(ConsoleTokens.DARK_BLUE + "Trying to reconnect to the server..."));

            if (reason.contains("验证")){
                log.info("bypassing");
                this.isByPassedVerification = false;
            }

            log.info(ConsoleTokens.colorizeText("&aTiming completed."));
            if (!super.connectingThread.isAlive()) {
                super.connect();
            }
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
}
