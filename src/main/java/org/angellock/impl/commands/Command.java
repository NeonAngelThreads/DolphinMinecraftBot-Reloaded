package org.angellock.impl.commands;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private final String name;
    private final List<String> users = new ArrayList<>();
    private ICommandAction action;

    public Command(String name, ICommandAction action, List<String> users) {
        this.name = name;
        this.users.addAll(users);
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

    public boolean activate(CommandResponse entity){
        if (users.contains(entity.getSender()) || users.isEmpty()) {
            this.action.onCommand(entity);
            return true;
        }
        return false;
    }
}
