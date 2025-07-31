package org.angellock.impl.providers;

import org.angellock.impl.managers.utils.Manager;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public abstract class AbstractPlugin extends Manager implements Plugin {
    private Path dataPath;
    private String simpleName;
    private Manifest manifest;
    private boolean enabled = false;
    private Manifest pluginManifest;
    private List<SessionListener> listeners = new ArrayList<>();

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
