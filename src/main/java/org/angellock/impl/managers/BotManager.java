package org.angellock.impl.managers;

import com.google.gson.JsonElement;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.providers.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BotManager extends ResourceHelper {
    private static final Logger log = LoggerFactory.getLogger("BotManager");
    private final Map<String, RobotPlayer> bots = new HashMap<>();
    private final ArrayList<Thread> botSessions = new ArrayList<>();
    private final ConfigManager botConfigHelper;
    private PluginManager pluginManager;
    public BotManager(@Nullable String defaultPath, String fileType, ConfigManager botConfigHelper) {
        super(defaultPath, fileType);
        this.botConfigHelper = botConfigHelper;
    }

    public BotManager globalPluginManager(String pluginDir){
        this.pluginManager = new PluginManager(pluginDir);
        return this;
    }

    public BotManager loadProfiles(String profileString){
        String commandLinePlayerName = (String)this.botConfigHelper.getConfigValue("username");
        String commandLinePWD = (String)this.botConfigHelper.getConfigValue("password");
        String commandLineOwner = (String)this.botConfigHelper.getConfigValue("owner");
        if(commandLinePlayerName != null && commandLineOwner != null && commandLinePWD != null){
            this.registerBot(commandLinePlayerName, commandLinePWD, commandLineOwner);
            return this;
        }

        String[] profileKeys = new String[0];
        if (profileString != null) {
            profileKeys = profileString.replaceAll("\"","").split(";");
        }
        Map<String, JsonElement> jsonObject = this.readJSONContent();
        Map<String, JsonElement> profiles = jsonObject.get("profiles").getAsJsonObject().asMap();

        if (profileKeys.length == 0) {
            for(String profileName: profiles.keySet()){
                registerBot(profiles, profileName);
            }
        }else {
            for (String name: profileKeys){
                registerBot(profiles, name);
            }
        }
        return this;
    }

    private void registerBot(Map<String, JsonElement> profiles, String name) {
        Map<String, JsonElement> profile = profiles.get(name).getAsJsonObject().asMap();
        String botName = profile.get("name").getAsString();
        String password = profile.get("password").getAsString();
        AbstractRobot botInst = new RobotPlayer(this.botConfigHelper, pluginManager).withName(botName).withPassword(password).buildProtocol();
        this.bots.put(name, (RobotPlayer) botInst);
    }

    private void registerBot(String username, String password, String owner){
        AbstractRobot botInst = new RobotPlayer(this.botConfigHelper, pluginManager).withName(username).withPassword(password).buildProtocol();
        this.bots.put(username, (RobotPlayer) botInst);
    }

    public void startAll(){
        for (RobotPlayer bot: this.bots.values()){
            Thread botThread = new Thread(bot::connect);
            botSessions.add(botThread);
        }

        for (Thread thread: this.botSessions){
            thread.start();
        }
    }
    @Override
    public String getFileName() {
        return "bot.profiles";
    }
}
