package org.angellock.impl.extensions;

import org.angellock.impl.providers.Plugin;

public enum Plugins {
    QUEUE_PLUGIN("QuestionAnswerer", new QuestionAnswererPlugin()),
    BASE_PLUGIN("MessageDisplay", new BaseDefaultPlugin()),
    VERIFY_PLUGIN("HumanVerify", new PlayerVerificationPlugin());

    private final String pluginName;
    private final Plugin pluginInstance;

    Plugins(String pluginName, Plugin pluginInstance) {
        this.pluginName = pluginName;
        this.pluginInstance = pluginInstance;
    }

    public Plugin getPlugin(){
        return this.pluginInstance;
    }

    public static Plugin getPluginFromString(String pluginName){
        for (Plugins plugins: Plugins.values()){
            if(plugins.pluginName.equalsIgnoreCase(pluginName)){
                return plugins.getPlugin();
            }
        }
        return null;
    }
}
