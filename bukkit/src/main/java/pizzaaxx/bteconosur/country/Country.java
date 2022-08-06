package pizzaaxx.bteconosur.country;

import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.chats.Chat;
import pizzaaxx.bteconosur.country.cities.CityRegistry;
import pizzaaxx.bteconosur.configuration.Configuration;

import java.io.File;

public class Country {

    private final CityRegistry registry;
    private final File folder;
    private final String guildID;
    private final String projectsLogsChannelID;
    private final String projectsRequestsChannelID;
    private final String showcaseChannelID;
    private final String name;
    private final JDA bot;
    private final Configuration tags;
    private final Configuration pending;
    private final ProtectedPolygonalRegion region;
    private final Configuration config;
    private final Chat chat;

    public Country(BteConoSur plugin, String name, JDA bot) {

        this.name = name;
        this.bot = bot;
        this.config = new Configuration(plugin, "countries/" + name + "/config");

        registry = new CityRegistry(this, plugin);

        this.folder = new File(plugin.getDataFolder(), "countries/" + name);

        this.tags = new Configuration(plugin, "countries/" + name + "/tags");
        this.pending = new Configuration(plugin, "countries/" + name + "/pending");

        this.guildID = config.getString("guildID");
        this.projectsLogsChannelID = config.getString("projectsLogsChannelID");
        this.projectsRequestsChannelID = config.getString("projectRequestsChannelID");
        this.showcaseChannelID = config.getString("showcaseChannelID");

        region = (ProtectedPolygonalRegion) plugin.getRegionsManager().getRegion(name);

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

    public Guild getGuild() {
        return bot.getGuildById(guildID);
    }

    public String getProjectsLogsChannelID() {
        return projectsLogsChannelID;
    }

    public TextChannel getProjectsLogsChannel() {
        return bot.getTextChannelById(projectsLogsChannelID);
    }

    public String getProjectsRequestsChannelID() {
        return projectsRequestsChannelID;
    }

    public TextChannel getProjectsRequestsChannel() {
        return bot.getTextChannelById(projectsRequestsChannelID);
    }

    public String getShowcaseChannelID() {
        return showcaseChannelID;
    }

    public TextChannel getShowcaseChannel() {
        return bot.getTextChannelById(showcaseChannelID);
    }

    public String getName() {
        return name;
    }

    public Configuration getTags() {
        return tags;
    }

    public Configuration getPending() {
        return pending;
    }

    public ProtectedPolygonalRegion getRegion() {
        return region;
    }

    public Chat getChat() {
        return chat;
    }
}
