package org.angellock.impl.commands;

public class CommandBuilder {
    private String commandName = "";
    private String[] users = new String[]{};

    public CommandBuilder withName(String cmdName){
        this.commandName = cmdName;
        return this;
    }

    public CommandBuilder allowedUsers(String... users){
        this.users = users;
        return this;
    }

    public Command build(ICommandAction action) {
        return new Command(this.commandName, action, this.users);
    }
}
