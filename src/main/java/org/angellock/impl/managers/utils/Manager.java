package org.angellock.impl.managers.utils;

import org.angellock.impl.Start;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Manager {
    public String getBaseConfigRoot(){
        URL d = Start.class.getProtectionDomain().getCodeSource().getLocation();
        String path = URLDecoder.decode(d.getPath().substring(1), StandardCharsets.UTF_8);
        if (path.endsWith(".jar")){
            path = path.substring(0, path.lastIndexOf('/'));
        }
        if(!path.endsWith("/")){
            path += "/";
        }
        return path;
    }
}
