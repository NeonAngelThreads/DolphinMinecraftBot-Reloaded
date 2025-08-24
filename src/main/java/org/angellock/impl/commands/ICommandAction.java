package org.angellock.impl.commands;
@FunctionalInterface
public interface ICommandAction {
    public void onCommand(CommandResponse responseEntity);
}
