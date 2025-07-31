package org.angellock.impl.providers;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.extensions.BaseDefaultPlugin;
import org.angellock.impl.managers.utils.Manager;
import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager extends Manager implements IPluginInjectable{
    private static final Logger log = LoggerFactory.getLogger("Plugin-Manager");
    private final FilenameFilter pluginFilePattern = (d,name)->name.endsWith(".jar");
    private final Map<String, Plugin> registeredPlugins = new HashMap<>();
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
            log.error(ConsoleTokens.standardizeText(ConsoleTokens.DARK_RED + "The plugin folder was invalid or not existed: "+ ConsoleTokens.YELLOW + pluginDir));
            log.warn(ConsoleTokens.standardizeText(ConsoleTokens.GOLD + "Trying to locate the fallback directory"));
            this.pluginFolder = new File(getBaseConfigRoot(), "plugins");

        }
        else {
            this.pluginFolder = pluginDir;
        }
        this.loader = new PluginLoader();
    }

    private void registerPlugin(Plugin plugin){
        plugin.onLoad();
        this.registeredPlugins.putIfAbsent(plugin.getName(), plugin);
    }

    public void loadAllPlugins(AbstractRobot botInstance){
        Plugin basePlugin = new BaseDefaultPlugin();
        enable(basePlugin, botInstance);

        File[] plugins = this.pluginFolder.listFiles(this.pluginFilePattern);
        if(plugins == null){
            log.error("The plugin folder was invalid or not found by removed, plugins will not be loaded.");
            return;
        }
        for (File plugin: plugins){
            this.loadPlugin(botInstance, plugin);
        }
    }
    public void disableAllPlugins(AbstractRobot botInstance){
        botInstance.getSession().getListeners().clear();
        for (Plugin plugin : this.registeredPlugins.values()){
            plugin.onDisable();
        }
        this.registeredPlugins.clear();
    }
    @Override
    public void disable(AbstractRobot botInstance, String pluginName){
        Plugin target = this.registeredPlugins.get(pluginName);
        List<SessionListener> pluginListeners = target.getListeners();

        for (SessionListener listener : pluginListeners) {
            botInstance.getSession().removeListener(listener);
        }
        target.onDisable();
        this.registeredPlugins.remove(pluginName);
    }
    @Override
    public void enable(Plugin plugin, AbstractRobot provider){
        plugin.onLoad();
        if (!plugin.isEnabled()){
            plugin.setEnabled(true);
            this.registerPlugin(plugin);
            plugin.onEnable(provider);

            List<SessionListener> listeners = plugin.getListeners();
            for (SessionListener listener : listeners) {
                provider.getSession().addListener(listener);
            }
        }
    }

    public void loadPlugin(AbstractRobot botInstance, File target) {
        Plugin plugin = this.loader.loadPluginClass(target);
        if (plugin != null) {
            log.info(ConsoleTokens.standardizeText(ConsoleTokens.DARK_GREEN + "Registering plugin: " + ConsoleTokens.AQUA + plugin.getName()));
            enable(plugin, botInstance);
        }else {
            log.error(ConsoleTokens.standardizeText("Failed to register the plugin " + ConsoleTokens.DARK_RED + target));
        }
    }

}
