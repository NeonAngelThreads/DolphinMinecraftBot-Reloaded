package org.angellock.impl.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command {
    private final String name;
    private final List<String> users = new ArrayList<>();
    private ICommandAction action;

    public Command(String name, ICommandAction action, String...strings) {
        this.name = name;
        this.users.addAll(Arrays.asList(strings));
        this.action = action;
    }

    public List<String> getUsers(){
        return this.users;
    }

    public String getName() {
        return name;
    }

    public void setAction(ICommandAction action){
        this.action = action;
    }

    public void activate(CommandResponse entity){
        this.action.onCommand(entity);
    }
}
