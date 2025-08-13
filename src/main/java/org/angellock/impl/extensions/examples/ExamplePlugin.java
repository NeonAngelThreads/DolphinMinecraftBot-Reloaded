package org.angellock.impl.extensions.examples;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.events.packets.LoginHandler;
import org.angellock.impl.providers.AbstractPlugin;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket;

public class ExamplePlugin extends AbstractPlugin {
    @Override
    public String getPluginName() {
        return "My First Plugin";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Hello DolphinBot";
    }

    @Override
    public void onDisable() {
        //Disable Message
    }

    @Override
    public void onLoad() {
        // Loading Plugin Message
    }

    @Override
    public void onEnable(AbstractRobot entityBot) {

        getListeners().add(
                new LoginHandler().addExtraAction((loginPacket) -> {
                    getLogger().info(loginPacket.getCommonPlayerSpawnInfo().getGameMode().name());
                })
        );
    }
}
