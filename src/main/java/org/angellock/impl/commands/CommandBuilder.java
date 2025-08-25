package org.angellock.impl.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandBuilder {
    private String commandName = "";
    private List<String> users = new ArrayList<>();

    public CommandBuilder withName(String cmdName){
        this.commandName = cmdName;
        return this;
    }

    public CommandBuilder allowedUsers(List<String> users) {
        this.users = users;
        return this;
    }

    public Command build(ICommandAction action) {
        return new Command(this.commandName, action, this.users);
    }
}
