package org.angellock.impl.extensions;

import net.kyori.adventure.text.TextComponent;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.events.packets.SystemChatHandler;
import org.angellock.impl.managers.QuestionManager;
import org.angellock.impl.providers.AbstractPlugin;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.QuestionSerializer;
import org.angellock.impl.util.TextComponentSerializer;

public class QuestionAnswererPlugin extends AbstractPlugin {
    private final QuestionManager questionManager = new QuestionManager(".json").load();
    private long lastAnswerTime = System.currentTimeMillis();
    @Override
    public String getPluginName() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable(AbstractRobot entityBot) {

        getListeners().add(new SystemChatHandler().addExtraAction((packet) -> {
            String msg = ((TextComponent)packet.getContent()).content();

            if(msg.contains("接下来问一个问题")){
                this.lastAnswerTime = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - this.lastAnswerTime < 500L) {
                QuestionSerializer serializer = new QuestionSerializer(msg, questionManager);
                serializer.build();
                if (serializer.isValid()) {
                    getLogger().info(ConsoleTokens.colorizeText("&b{}"), serializer.getAnswer());
                    entityBot.getMessageManager().putMessage(serializer.getAnswer());
                }else {
                    getLogger().info(ConsoleTokens.colorizeText("&c The Question Is Not Found. \"{}\""), msg);
                }
            }
        }));
    }
}
