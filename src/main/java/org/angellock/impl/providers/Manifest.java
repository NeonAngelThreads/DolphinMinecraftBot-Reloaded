package org.angellock.impl.providers;

public class Manifest {
    private String entry;
    private String name;
    private String version;

    public String getMainClass() {
        return entry;
    }

    public String getPluginName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
