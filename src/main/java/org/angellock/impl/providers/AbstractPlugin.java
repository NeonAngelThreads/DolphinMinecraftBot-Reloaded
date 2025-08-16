package org.angellock.impl.providers;

import net.kyori.adventure.text.TextComponent;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.Command;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.CommandSerializer;
import org.angellock.impl.commands.CommandSpec;
import org.angellock.impl.events.packets.SystemChatHandler;
import org.angellock.impl.managers.utils.Manager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.PlainTextSerializer;
import org.angellock.impl.util.TextComponentSerializer;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractPlugin extends Manager implements Plugin {
    private Path dataPath;
    private String simpleName;
    private Manifest manifest;
    private boolean enabled = false;
    private Manifest pluginManifest;
    private final List<SessionListener> listeners = new ArrayList<>();
    private final CommandSpec commands = new CommandSpec();
    private static final Logger log = LoggerFactory.getLogger(AbstractPlugin.class);
    protected Thread schedulerThread;

    public AbstractPlugin(@Nullable String defaultDataPath){
        this();
        if (defaultDataPath != null) {
            Path path = Path.of(defaultDataPath);
            if (Files.exists(path)) {
                this.dataPath = path;
            }
        }
    }

    public AbstractPlugin(){
        String path = getBaseConfigRoot();
        this.dataPath = Path.of(path);
        this.simpleName = this.getClass().getSimpleName();
    }

    public String getSimpleName() {
        return simpleName;
    }

    public Manifest getManifest(){
        return this.pluginManifest;
    }

    public static Logger getLogger(){
        return log;
    }

    public CommandSpec getCommands(){
        return this.commands;
    }

    public void enable(AbstractRobot robot){

        this.listeners.add(new SystemChatHandler().addExtraAction((chatPacket -> {
            CommandSerializer serializer = new CommandSerializer();
            PlainTextSerializer componentSerializer = new PlainTextSerializer();
            String commandMsg = componentSerializer.serialize(chatPacket.getContent());
            CommandResponse meta = serializer.serialize(commandMsg);
            if (meta != null) {
                log.info("CommandList: {}, sender: {}", Arrays.toString(meta.getCommandList()), meta.getSender());
                Command cmd = this.commands.getCommand(meta.getCommandList()[0]);
                if(cmd != null){
                    cmd.activate(meta);
                }
            }
        })));

        onEnable(robot);
    }
    public abstract void onEnable(final AbstractRobot entityBot);

    public abstract String getPluginName();
    @Override
    public String getName(){
        if(!this.getPluginName().isEmpty() && this.getPluginName() != null){
            return this.getPluginName();
        }
        else {
            String pluginName = getManifest().getPluginName();
            if(!pluginName.isEmpty()){
                return pluginName;
            }
        }
        return getSimpleName();
    }
    @Override
    public @Nullable Path getDataFolder(){
        return this.dataPath;
    }

    @Override
    public boolean isEnabled(){
        return this.enabled;
    }

    @Override
    public List<SessionListener> getListeners(){
        return this.listeners;
    }
    @Override
    public void setManifest(Manifest manifest){
        this.manifest = manifest;
    }
    @Override
    public void setEnabled(boolean state){
        this.enabled = state;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else {
            return obj instanceof AbstractPlugin &&
                    this.getName().equals(((AbstractPlugin) obj).getName()) &&
                    this.simpleName.equals(((AbstractPlugin) obj).getSimpleName()) ;
        }
    }
}
