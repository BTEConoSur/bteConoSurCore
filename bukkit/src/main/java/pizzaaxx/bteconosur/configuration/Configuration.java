package pizzaaxx.bteconosur.configuration;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Configuration extends YamlConfiguration {

    private static final String DEFAULT_EXTENSION = ".yml";

    private final String fileName;
    private final Plugin plugin;
    private final File folder;

    public Configuration(Plugin plugin, String fileName, String fileExtension,
                         File folder) {
        this.folder = folder;
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.createFile();
    }

    public Configuration(Plugin plugin, String fileName) {
        this(plugin, fileName, DEFAULT_EXTENSION);
    }

    public Configuration(Plugin plugin, String fileName, String fileExtension) {
        this(plugin, fileName, fileExtension, plugin.getDataFolder());
    }

    @NotNull
    public String getString(String path) {

        String message = super.getString(path);

        if (message == null) {
            return "Invalid path " + path + " is incorrect";
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private void createFile() {

        try {
            File file = new File(folder, fileName);

            if (file.exists()) {
                load(file);
                save(file);
                return;
            }

            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            } else {
                save(file);
            }

            load(file);
        } catch (InvalidConfigurationException | IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Creation of Configuration '" + fileName + "' failed.", e);
        }
    }

    public void save() {
        File folder = plugin.getDataFolder();
        File file = new File(folder, fileName);
        try {
            save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Save of the file '" + fileName + "' failed.", e);
        }
    }

    public void reload() {
        File folder = plugin.getDataFolder();
        File file = new File(folder, fileName);
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "Reload of the file '" + fileName + "' failed.", e);
        }
    }

}