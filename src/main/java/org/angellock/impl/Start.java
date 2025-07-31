package org.angellock.impl;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.util.ConsoleDecorations;
import org.angellock.impl.util.ConsoleTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Start {
    private static final Logger log = LoggerFactory.getLogger(Start.class);

    public static void main(String[] args) {
        OptionParser optionParser = new OptionParser();

        //optionParser.allowsUnrecognizedOptions();

        optionParser.accepts("owner").withRequiredArg().ofType(String.class);
        optionParser.accepts("username").withRequiredArg().ofType(String.class);
        optionParser.accepts("password").withRequiredArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> configFile = optionParser.accepts("config-file").withOptionalArg().ofType(String.class);
        NonOptionArgumentSpec<String> unrecognizedOptions = optionParser.nonOptions();
        OptionSet parsedOption = optionParser.parse(args);

        List<?> badOptions = parsedOption.valuesOf(unrecognizedOptions);
        if (!badOptions.isEmpty()){
            //System.out.println("Omitted arguments " + badOptions);
            log.warn(ConsoleTokens.standardizeText(ConsoleTokens.GOLD + "Omitted option arguments "+ badOptions));
        }

        String defaultConfigPath = parsedOption.valueOf(configFile);
        if (defaultConfigPath != null){
            if(Files.exists(Paths.get(defaultConfigPath))) {
                log.info(ConsoleTokens.standardizeText(ConsoleTokens.LIGHT_PURPLE + "The default config file path was specified: " + ConsoleDecorations.UNDERLINED + ConsoleTokens.DARK_PURPLE + defaultConfigPath));
            }
            else {
                log.error(ConsoleTokens.standardizeText(ConsoleTokens.DARK_RED + "The specified config file path is invalid: " + defaultConfigPath));
                defaultConfigPath = null;
            }
        }

        ConfigManager config = new ConfigManager(parsedOption, defaultConfigPath);
        RobotPlayer player = new RobotPlayer(config);
        player.connect();

    }
}