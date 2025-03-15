package com.bteconosur.core;

import com.bteconosur.core.utils.PluginRegistry;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;


public final class BteConoSurCore extends JavaPlugin {
    private static BteConoSurCore instance;

    @Override
    public void onEnable() {
        // Save the instance of the plugin
        instance = this;

        // Command registration
        CommandMap commandMap = PluginRegistry.getCommandMap();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BteConoSurCore getInstance() {
        return instance;
    }
}
