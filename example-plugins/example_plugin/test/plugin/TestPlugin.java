package test.plugin;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.events.packets.PlayerLogInfo;
import org.angellock.impl.events.packets.SystemChatHandler;
import org.angellock.impl.providers.AbstractPlugin;

public class TestPlugin extends AbstractPlugin {
    @Override
    public String getPluginName() {
        return "TestPlugin";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "TestPlugin";
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable(AbstractRobot abstractRobot) {
        getListeners().add(new SystemChatHandler().addExtraAction((clientboundSystemChatPacket) -> {
            System.out.println("Hello world!");
        }));
    }
}
