package org.angellock.impl.managers.utils;

import org.angellock.impl.Start;

public class Manager {
    public String getBaseConfigRoot(){
        return System.getProperty("user.dir") + "\\";
    }
}
