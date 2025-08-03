package org.angellock.impl;

import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.providers.PluginManager;
import org.angellock.impl.util.ConsoleTokens;

public class RobotPlayer extends AbstractRobot {

    public RobotPlayer(ConfigManager configManager) {
        super(configManager);
    }

    @Override
    public void onJoin() {
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.DARK_GREEN + "Connection was established!"));
    }

    @Override
    public void onQuit(String reason) {
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.DARK_RED + "Disconnected from the server!"));
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.GOLD + "Reason: " + ConsoleTokens.LIGHT_PURPLE + reason));
        if (this.config.getConfigValue("auto-reconnecting").equals("true")){
            log.info(ConsoleTokens.standardizeText(ConsoleTokens.DARK_BLUE + "Trying to reconnect to the server..."));
            try {
                Thread.sleep(this.ReconnectionDelay);
            } catch (InterruptedException ignore) {}
            super.connect();
        }
    }

    @Override
    public void onKicked() {
        return;
    }

    @Override
    public void onPreLogin() {
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.DARK_AQUA + "Attempt to join to the server "+ this.server+':'+this.port +". Waiting for server establishing the connection..."));
    }
}
