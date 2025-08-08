### **[<- Back to README.md](README.md)**
# Customized Plugin Developing Guide
In this section, you will learn:
- **1. Plugin Jar Structures**
- **2. Understand Plugin Lifecycles**  
- **3. To Create a Simple Plugin**  

## 1. Plugin Structure
  Plugin jar files are supposed to be contained the plugin manifest file `plugin.json` inside the root of jar file.  
  **Jar file structure:**  
  ````text 
    MyPlugin.jar
      ├─plugin.json
      └─org
        └─me
          └─plugin
            └─MyPlugin.java
   ````
  Manifest file `plugin.json` is aiming to specify the **entry class** of plugin, plugin name and version.  
  **An example for configuring the `plugin.json`:**
  ````json
   {
      "entry": "MyPluginClassName",
      "name": "MyPlugin",
      "version": "1.0.0"
   }
  ````
  1. **How Entry Class Being Located**:   
    When loading a plugin, DolphinBot will locate the target entry class defined in `plugin.json`, if `plugin.json` not existing, 
    it will scan all classes that extends `AbstractPlugin`, if class not found, it will skip and ignore this plugin.  

## 2. Build A Simple Plugin
  Every single plugin entry class needs to extend the `AbstractPlugin` class:

   ````java
    package org.angellock.impl.extensions;

    import org.angellock.impl.AbstractRobot;
    import org.angellock.impl.events.packets.LoginHandler;
    import org.angellock.impl.providers.AbstractPlugin;
    import org.angellock.impl.util.ConsoleTokens;
    
    public class ExamplePlugin extends AbstractPlugin {
        @Override
        public String getPluginName() {
            return "My First Plugin";
        }
    
        @Override
        public String getVersion() {
            return "1.0.0";
        }
    
        @Override
        public String getDescription() {
            return "Hello DolphinBot";
        }
    
        @Override
        public void onDisable() {
            getLogger().info("Disabling {} - {}", this.getName(), getVersion());
            //Disable Message
        }
    
        @Override
        public void onLoad() {
            getLogger().info("Loading {} - {}", this.getName(), getVersion());
            // Loading Plugin Message
        }
    
        @Override
        public void onEnable(AbstractRobot entityBot) {
            getListeners().add(
                    new LoginHandler().addExtraAction((loginPacket) -> {
                        getLogger().info(loginPacket.getCommonPlayerSpawnInfo().getGameMode().name());
                    })
            );
        }
    }

   ````
  **1. Deep Understand DolphinAPIs:**  
    In above plugin code, at `onEnable()` method, we used `getListeners()` method to get the collection of packet listeners in this plugin.  
    
   ```java
    @Override
    public void onEnable(AbstractRobot entityBot) {
        getListeners().add(
                new LoginHandler().addExtraAction((loginPacket) -> {
                    getLogger().info(loginPacket.getCommonPlayerSpawnInfo().getGameMode());
                })
        );
    }
   ```
   Above code implements an informant that will print current gamemode info once the bot join to or be redirected to a server.  
   **Code Explanation:**  
   `getListeners()` returns an iterable list of `AbstractEventProcessor`, allows you to use `add()` to register various packet handlers.  
   Packet handlers includes `LoginHandler`, `SystemChatHandler`, `PlayerLogInfo.UpdateHandler`, `PlayerLogInfo.RemoveHander`, `JoinedGameHandler` and so on.  
   - **Customize Handlers:**  
    Excepting implemented handlers, you can make custom packet handlers by **extending** `AbstractEventProcessor` class:  
    For example, if you want to create a custom handler that listens and handles `ClientboundPlayerChatPacket`, you should **extend**
    the `AbstractEventProcessor` class:  
   ````java
    package org.angellock.impl.extensions;
    
    import org.angellock.impl.events.AbstractEventProcessor;
    import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
    import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket;
    
    public class MyHandler extends AbstractEventProcessor<ClientboundPlayerChatPacket> { // specifying target packet type.
        @Override
        protected boolean isTargetPacket(MinecraftPacket packet) {
            return (packet instanceof ClientboundPlayerChatPacket); // filtering other packet type.
        }
    }

   ````
   After that, you can use your packet handler ``MyHandler`` class to register your custom listener at your custom plugin.
   By using `.addExtraAction()`, you can add your wanted action towards `MyHandler`. Finally, add it to the global listener.  
   ````java
    import org.angellock.impl.extensions.MyHandler;

    @Override
    public void onEnable(AbstractRobot entityBot) {
        getListeners().add(
                new MyHandler().addExtraAction((playerChatPacket) -> {
                    getLogger().info(playerChatPacket.getContent()); // Example Action.
                })
        );
    }
   ````
## 3. How a Plugin Works
  1. **Base Plugin Events**:  
   Every single plugin exists in form of a jar file managed by `PluginManager`. The `PluginManager` is used to enable
   and register plugins or disable them.  
   Each plugin has 3 base simple loading handlers:
     1. **The `onLoad()` Method.**
     2. **The `onRegister()` Method.**
     3. **The `onDisable()` Method.**  
     - The differences between method `onLoad()` and `onRegister()`:  
       `onLoad()` method is called only when plugin internal classes and resources is being loading.  
       `onRegister()` method is called only when plugin classes loads was completed and register its wrapped listeners.
  2. **Lifecycle of a Plugin** :  
     When bot initialized, it will scan for all plugins in the plugin folder, and then fetches **entry class**, at this phase, `onLoad()` method will be called.  
     Once a bot pre-enter to a server, `PluginLoader` will load all registered listeners of target plugins as `IActions` 
     listener, after that, 
     `PluginManager` will register them as global plugins, at this phase, `onEnable()` method will be called.