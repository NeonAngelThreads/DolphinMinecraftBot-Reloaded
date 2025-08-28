package org.angellock.impl.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class PlainTextSerializer implements ComponentSerializer<Component, Component, String> {
    private final StringBuilder result = new StringBuilder();
    @Override
    public @NotNull Component deserialize(@NotNull String input) {
        return Component.newline();
    }

    public void serializePlain(@NotNull Component component) {
        if (component instanceof TextComponent){
            this.result.append(((TextComponent) component).content());
        }

        for (Component child : component.children()){
            this.serializePlain(child);
        }
    }

    @NotNull
    @Override
    public String serialize(@NotNull Component component) {
        this.serializePlain(component);
        return ConsoleTokens.fadeText(result.toString());
    }
}
