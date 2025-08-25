package org.angellock.impl;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.win32terminal.AnsiEscapes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Start {
    private static final Logger log = LoggerFactory.getLogger(Start.class);
    private static final String ARCHIVE_VERSION = Start.class.getPackage().getImplementationVersion();
    private static final boolean win32 = System.getProperty("os.name").toLowerCase().contains("windows");

    public static void main(String[] args) {
        OptionParser optionParser = new OptionParser();

        AnsiEscapes.enableAnsiSupport();
        optionParser.allowsUnrecognizedOptions();

        optionParser.accepts("owner").withRequiredArg().ofType(String.class);
        optionParser.accepts("username").withRequiredArg().ofType(String.class);
        optionParser.accepts("password").withRequiredArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> profilesArg = optionParser.accepts("profiles").withOptionalArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> pluginDir = optionParser.accepts("plugin-dir").withOptionalArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> configFile = optionParser.accepts("config-file").withOptionalArg().ofType(String.class);
        NonOptionArgumentSpec<String> unrecognizedOptions = optionParser.nonOptions();
        OptionSet parsedOption = optionParser.parse(args);

        List<?> badOptions = parsedOption.valuesOf(unrecognizedOptions);
        if (!badOptions.isEmpty()){
            log.warn(ConsoleTokens.colorizeText("&6Omitted option arguments " + badOptions));
        }

        String defaultConfigPath = parsedOption.valueOf(configFile);
        if (defaultConfigPath != null){
            if(Files.exists(Paths.get(defaultConfigPath))) {
                log.info(ConsoleTokens.colorizeText("&dThe default config file path was specified: &5&l" + defaultConfigPath));
            }
            else {
                log.error(ConsoleTokens.colorizeText("&4The specified config file path is invalid: " + defaultConfigPath));
                defaultConfigPath = null;
            }
        }
        @Nullable String profiles = (parsedOption.valueOf(profilesArg));

        ConfigManager config = new ConfigManager(parsedOption, defaultConfigPath);
        BotManager botManager = new BotManager(defaultConfigPath, ".json", config).globalPluginManager(parsedOption.valueOf(pluginDir)).loadProfiles(profiles);
        botManager.startAll();
    }

    public static boolean isWindows() {
        return win32;
    }
}