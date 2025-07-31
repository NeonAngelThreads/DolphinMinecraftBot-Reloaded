package org.angellock.impl.managers;

import com.google.gson.JsonElement;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.angellock.impl.Start;
import org.angellock.impl.managers.utils.Manager;
import org.angellock.impl.util.ConsoleTokens;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger("Config-Manager");
    private final Map<Object, Object> cache = new HashMap<>();
    private final RobotConfig configHelper;
    public ConfigManager(OptionSet optionList, @Nullable String defaultPath){
        for (OptionSpec<?> option: optionList.specs()){
            String stringOpt = option.options().get(0);
            Object valueObject = optionList.valueOf(option);
            this.cache.put(stringOpt, valueObject);
        }
        this.configHelper = new RobotConfig(defaultPath, ".json");
        this.loadConfig();
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.GRAY + "below argument options are enabled: "+ ConsoleTokens.DARK_AQUA + cache.toString()));
    }
    public ConfigManager(OptionSet optionList){
        this(optionList, null);
    }
    public Map<?, ?> getMCBotConfig(){
        if (this.cache.isEmpty()){
            loadConfig();
        }
        return this.cache;
    }

    public Object getConfigValue(Object key){
        return this.cache.get(key);
    }

    private void loadConfig(){
        Map<String, JsonElement> defaultConfig = this.configHelper.readJSONContent();
        if (defaultConfig == null){
            return;
        }
        for (String item: defaultConfig.keySet()){
            if (this.cache.get(item) == null){

                this.cache.put(item, defaultConfig.get(item).getAsString());
            }
        }
    }
    public void reloadConfig(){
        this.flushConfig();
        this.loadConfig();
    }

    private void flushConfig(){
        this.cache.clear();
    }
}
