package pizzaaxx.bteconosur.country;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.Chat.CountryChat;
import pizzaaxx.bteconosur.Points.PointsContainer;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.cities.CityRegistry;

import java.io.File;
import java.util.*;

public class Country implements PointsContainer {

    private final BteConoSur plugin;
    private final CityRegistry registry;
    private final File folder;
    private final String guildID;
    private final String projectsLogsChannelID;
    private final String projectsRequestsChannelID;
    private final String showcaseChannelID;
    private final String name;
    private final String abbreviation;
    private final JDA bot;
    private final Configuration tags;
    private final Configuration pending;
    private final Configuration maxPoints;
    private final Set<ProtectedRegion> regions = new HashSet<>();
    private final Configuration config;
    private final CountryChat chat;
    private final boolean allowsProjects;

    public Country(BteConoSur plugin, @NotNull String name, String abbreviation, boolean allowsProjects, JDA bot) {

        this.allowsProjects = allowsProjects;

        this.abbreviation = abbreviation;
        this.plugin = plugin;

        this.name = name;
        this.bot = bot;
        this.config = new Configuration(plugin, "countries/" + name + "/config");

        registry = new CityRegistry(this, plugin);

        this.folder = new File(plugin.getDataFolder(), "countries/" + name);

        this.tags = new Configuration(plugin, "countries/" + name + "/tags");
        this.pending = new Configuration(plugin, "countries/" + name + "/pending");
        this.maxPoints = new Configuration(plugin, "countries/" + name + "/maxPoints");

        this.guildID = config.getString("guildID");
        this.projectsLogsChannelID = config.getString("projectsLogsChannelID");
        this.projectsRequestsChannelID = config.getString("projectRequestsChannelID");
        this.showcaseChannelID = config.getString("showcaseChannelID");

        if (!name.equals("chile")) {
            regions.add(plugin.getRegionsManager().getRegion(name));
        } else {
            regions.add(plugin.getRegionsManager().getRegion("chile_cont"));
            regions.add(plugin.getRegionsManager().getRegion("chile_idp"));
        }

        this.chat = plugin.getChatManager().getChat(this);

    }

    public CityRegistry getCityRegistry() {
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

    public Set<ProtectedRegion> getRegions() {
        return regions;
    }

    public CountryChat getChat() {
        return chat;
    }

    public BteConoSur getPlugin() {
        return plugin;
    }

    public String getDiscordEmoji() {
        return ":flag_" + abbreviation + ":";
    }

    public boolean isInside(@NotNull Location loc) {
        BlockVector2D vector = new BlockVector2D(loc.getX(), loc.getZ());
        for (ProtectedRegion region : regions) {
            if (region.contains(vector)) {
                return true;
            }
        }
        return false;
    }

    public boolean allowsProjects() {
        return allowsProjects;
    }

    private class CountryPointsComparator implements Comparator<UUID> {

        private final Country country;

        public CountryPointsComparator(Country country) {
            this.country = country;
        }

        @Override
        public int compare(UUID u1, UUID u2) {
            int p1 = plugin.getPlayerRegistry().get(u1).getPointsManager().getPoints(country);
            int p2 = plugin.getPlayerRegistry().get(u2).getPointsManager().getPoints(country);

            return Integer.compare(p1, p2);
        }
    }

    @Override
    public void checkMaxPoints(UUID uuid) {

        if (allowsProjects) {
            List<UUID> uuids = new ArrayList<>();
            for (String key : maxPoints.getStringList("max")) {
                uuids.add(UUID.fromString(key));
            }
            if (!uuids.contains(uuid)) {
                uuids.add(uuid);
            }
            uuids.sort(new CountryPointsComparator(this));

            List<String> result = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                result.add(uuids.get(i).toString());
            }
            maxPoints.set("max", result);
            maxPoints.save();
        }
    }

    @Override
    public @NotNull List<UUID> getMaxPoints() {
        List<UUID> max = new ArrayList<>();
        for (String key : maxPoints.getStringList("max")) {
            max.add(UUID.fromString(key));
        }
        return max;
    }

    @Override
    public String toString() {
        return name;
    }
}
