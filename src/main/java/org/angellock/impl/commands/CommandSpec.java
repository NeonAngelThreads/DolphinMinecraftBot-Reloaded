package org.angellock.impl.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandSpec {
    private final Map<String, Command> registeredCommands = new HashMap<>();

    public void register(Command command){
        this.registeredCommands.put(command.getName(), command);
    }

    public Command getCommand(String commandName){
        return this.registeredCommands.get(commandName);
    }
}
