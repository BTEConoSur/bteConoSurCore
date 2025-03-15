package com.bteconosur.core.config;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigHandler {
    private static ConfigHandler instance;
    private final ConfigFile config = new ConfigFile("config.yml");
    private final ConfigFile lang = new ConfigFile("lang.yml");

    public ConfigHandler() {
        registerConfig();
    }

    private void registerConfig() {
        config.register();
        lang.register();
    }

    public YamlConfiguration getConfig() {
        return config.getFileConfiguration();
    }

    public YamlConfiguration getLang() {
        return lang.getFileConfiguration();
    }

    public void save() {
        config.save();
        lang.save();
    }

    public void reload() {
        config.reload();
        lang.reload();
    }

    /**
     * Get the instance of the ConfigHandler
     * @return ConfigHandler instance
     */
    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }
}
