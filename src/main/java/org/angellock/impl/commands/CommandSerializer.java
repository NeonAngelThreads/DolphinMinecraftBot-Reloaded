package org.angellock.impl.commands;

import org.angellock.impl.util.ConsoleTokens;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandSerializer implements Serializable {
    private final char chineseExclamation = '！';
    private final char exclamation = '!';
    private final Pattern senderPattern = Pattern.compile("<([^>]+)>");


    public @Nullable CommandResponse serialize(String stringCommand){
        int exclamationIndex = stringCommand.indexOf(exclamation);
        int chineseExclamationIndex = stringCommand.indexOf(chineseExclamation);
        if(exclamationIndex == -1){
            if(chineseExclamationIndex == -1){
                return null;
            }
            return extractCommandMeta(stringCommand, chineseExclamation);
        }
        return extractCommandMeta(stringCommand, exclamation);
    }

    public @Nullable CommandResponse serialize(String command, String commandSender) {
        int indexSum = command.indexOf(exclamation) + command.indexOf(chineseExclamation);
        if (indexSum == -1) {
            String[] commands = command.substring(1).strip().split(" ");
            for (int o = 0; o < commands.length; o++) {
                commands[o] = commands[o].trim();
            }
            return new CommandResponse(commands, commandSender);
        }
        return null;
    }

    private @Nullable CommandResponse extractCommandMeta(String msg, char target) {
        Matcher matcher = this.senderPattern.matcher(msg);
        String commandSender;
        if (matcher.find()) {
            commandSender = matcher.group(1);

            msg = matcher.replaceAll("").trim().strip();
            msg = ConsoleTokens.fadeText(msg);
            if (msg.indexOf(target) < 1) {
                msg = msg.substring(msg.indexOf(target) + 1);
                String[] commands = msg.split(" ");
                for (int o = 0; o < commands.length; o++) {
                    commands[o] = commands[o].trim();
                }
                return new CommandResponse(commands, commandSender);
            }
            return null;
        }
        return null;
    }

}
