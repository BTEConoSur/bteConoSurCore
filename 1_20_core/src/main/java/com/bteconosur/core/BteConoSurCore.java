package com.bteconosur.core;

import com.bteconosur.core.command.btecs.BTECSCommand;
import com.bteconosur.core.utils.PluginRegistry;
import org.bukkit.plugin.java.JavaPlugin;


public final class BteConoSurCore extends JavaPlugin {
    private static BteConoSurCore instance;

    @Override
    public void onEnable() {
        // Guardar instancia del plugin
        instance = this;

        // Registro de comandos
        PluginRegistry.registerCommand(new BTECSCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BteConoSurCore getInstance() {
        return instance;
    }
}
