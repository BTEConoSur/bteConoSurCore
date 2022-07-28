package pizzaaxx.bteconosur.country;

import org.bukkit.plugin.Plugin;
import pizzaaxx.bteconosur.cities.CityRegistry;
import pizzaaxx.bteconosur.configuration.Configuration;

import java.io.File;

public class Country {

    private final CityRegistry registry;

    private final File folder;

    private final String guildID;

    private final String projectsLogsChannelID;

    private final String projectRequestsChannelID;

    private final String showcaseChannelID;
    private final String name;

    public Country(Configuration config, Plugin plugin, String name) {

        this.name = name;

        registry = new CityRegistry(this, plugin);

        this.folder = new File(plugin.getDataFolder(), "countries/" + name);

        this.guildID = config.getString("guildID");
        this.projectsLogsChannelID = config.getString("projectsLogsChannelID");
        this.projectRequestsChannelID = config.getString("projectRequestsChannelID");
        this.showcaseChannelID = config.getString("showcaseChannelID");

    }

    public CityRegistry getRegistry() {
        return registry;
    }

    public File getFolder() {
        return folder;
    }

    public String getGuildID() {
        return guildID;
    }

    public String getProjectsLogsChannelID() {
        return projectsLogsChannelID;
    }

    public String getProjectRequestsChannelID() {
        return projectRequestsChannelID;
    }

    public String getShowcaseChannelID() {
        return showcaseChannelID;
    }


    public String getName() {
        return name;
    }

}
