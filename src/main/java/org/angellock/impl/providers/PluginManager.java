package org.angellock.impl.providers;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.extensions.BaseDefaultPlugin;
import org.angellock.impl.extensions.PlayerVerificationPlugin;
import org.angellock.impl.extensions.QuestionAnswererPlugin;
import org.angellock.impl.managers.utils.Manager;
import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class PluginManager extends Manager implements IPluginInjectable{
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&9PluginManager"));
    private final FilenameFilter pluginFilePattern = (d,name)->name.endsWith(".jar");
    private final Map<String, AbstractPlugin> registeredPlugins = new HashMap<>();
    private final Map<String, File> loadedExternalPlugin = new HashMap<>();
    private final Collection<Plugin> enabled_base_plugin = new ArrayList<>();
    private final File pluginFolder;
    private final PluginLoader loader;

    public PluginManager(){
        this((File) null);
    }

    public PluginManager(@Nullable String pluginDir) {
        this(new File((pluginDir == null)? "" : pluginDir));
    }

    public PluginManager(@Nullable File pluginDir) {
        if (pluginDir == null || !pluginDir.exists() || !pluginDir.isDirectory()){
            log.warn(ConsoleTokens.colorizeText("&4The plugin folder was invalid or not existed: &c"+ pluginDir
                     + "&eTrying to locate the fallback directory"));
            this.pluginFolder = new File(getBaseConfigRoot(), "plugins");

        }
        else {
            this.pluginFolder = pluginDir;
        }
        this.loader = new PluginLoader();
    }

    private void registerPlugin(Plugin plugin){
        this.registeredPlugins.putIfAbsent(plugin.getName(), (AbstractPlugin) plugin);
    }

    public void keepScheduleThreadsAlive(){
        for (AbstractPlugin plugin: this.registeredPlugins.values()){
            if (!plugin.schedulerThread.isAlive()){
                plugin.schedulerThread.start();
            }
        }
    }

    public void loadAllPlugins(AbstractRobot botInstance){
        if(!this.registeredPlugins.isEmpty()){
            for (Plugin plugin: this.registeredPlugins.values()){
                enable(plugin, botInstance);
            }
            return;
        }
        for (Plugin aDefault : this.enabled_base_plugin) {
            enable(aDefault, botInstance);
        }

        File[] plugins = this.pluginFolder.listFiles(this.pluginFilePattern);
        File subDir = new File(this.pluginFolder, botInstance.getProfileName());
        if(!this.pluginFolder.exists()){
            boolean successful = this.pluginFolder.mkdir();
            if (!successful){
                log.error(ConsoleTokens.colorizeText("&4Failed to create the plugin folder."));
            }else {
                log.info(ConsoleTokens.colorizeText("&7Successfully created new plugin folder."));
            }
        }
        if (!subDir.exists()){
            boolean successful2 = subDir.mkdir();
            if(successful2){
                log.info(ConsoleTokens.colorizeText("&7Created individual bot plugin folder."));
            }
        }
        if(plugins == null){
            log.error(ConsoleTokens.colorizeText("&6The plugin folder was invalid or not found by removed, plugins will not be loaded."));
            return;
        }
        for (File plugin: plugins){
            this.loadPlugin(botInstance, plugin);
        }

        File[] individualPlugins = subDir.listFiles(this.pluginFilePattern);
        if(individualPlugins == null){
            log.error(ConsoleTokens.colorizeText("&6The plugin folder was invalid or not found by removed, plugins will not be loaded."));
            return;
        }
        for (File InnerPlugin: individualPlugins){
            this.loadPlugin(botInstance, InnerPlugin);
        }

    }
    public void disableAllPlugins(AbstractRobot botInstance){
        for (String plugin : this.registeredPlugins.keySet()){
            this.disable(botInstance, plugin);
        }
    }
    @Override
    public void disable(AbstractRobot botInstance, String pluginName){
        Plugin target = this.registeredPlugins.get(pluginName);
        List<SessionListener> pluginListeners = target.getListeners();

        for (SessionListener listener : pluginListeners) {
            log.info(ConsoleTokens.colorizeText("&7[&bEventBus&7] &7Removing Action Object &l{}"), listener.toString());
            botInstance.getSession().removeListener(listener);
        }
        target.onDisable();
        target.setEnabled(false);
    }

    public void enable(Plugin plugin, AbstractRobot provider){
        plugin.onLoad();
        if (!plugin.isEnabled()){
            plugin.setEnabled(true);
            this.registerPlugin(plugin);
            plugin.onEnables(provider);

            List<SessionListener> listeners = plugin.getListeners();
            log.info(ConsoleTokens.colorizeText("&7[&bEventBus&7] &eRegistering Listener From Plugin &6{}"), plugin.getName());

            for (SessionListener listener : listeners) {
                if (!provider.getSession().getListeners().contains(listener)) {
                    log.info(ConsoleTokens.colorizeText("&7[&bEventBus&7] &6Injecting Action Object &7&l{}"), listener.toString());
                    provider.getSession().addListener(listener);
                }

            }
        }
        log.info(ConsoleTokens.colorizeText("&aSuccessfully registered plugin &2{}, &dversion: &5{}, &bdescription: &3{}"), plugin.getName(), plugin.getVersion(), plugin.getDescription());
    }

    public void loadPlugin(AbstractRobot botInstance, File target) {
        Plugin plugin = this.loader.loadPluginClass(target);
        if (plugin != null) {
            log.info(ConsoleTokens.colorizeText("&2Registering plugin: &b" + plugin.getName()));
            enable(plugin, botInstance);
            this.loadedExternalPlugin.put(plugin.getName().toLowerCase(), target);
        }else {
            log.error(ConsoleTokens.colorizeText("Failed to register the plugin &4" + target));
        }
    }

    public void reloadPlugin(AbstractRobot botInstance, String pluginName){
        File pluginFile = this.loadedExternalPlugin.get(pluginName);
        if (pluginFile.exists()){
            this.loadPlugin(botInstance, pluginFile);
        }
    }

    public Collection<Plugin> getDefaultPlugins() {
        return enabled_base_plugin;
    }
}
