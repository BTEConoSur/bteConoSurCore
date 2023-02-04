package pizzaaxx.bteconosur.Countries;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.Managers.ProjectManager;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.JSONParsable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Country implements JSONParsable {

    private final BTEConoSur plugin;
    private final String name;
    private final String displayName;
    private final String abbreviation;
    private final String guildID;
    private final String showcaseID;
    private final String globalChatID;
    private final String countryChatID;
    private final String logsID;
    private final String requestsID;
    private final String iconURL;
    private final Location spawnPoint;
    public final Set<String> cities;
    public final LinkedHashMap<String, ProjectType> projectTypes;

    public Country(@NotNull BTEConoSur plugin, @NotNull ResultSet set) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        this.name = set.getString("name");
        this.displayName = set.getString("display_name");
        this.abbreviation = set.getString("abbreviation");
        this.guildID = set.getString("guild_id");
        this.showcaseID = set.getString("showcase_id");
        this.globalChatID = set.getString("global_chat_id");
        this.countryChatID = set.getString("country_chat_id");
        this.logsID = set.getString("logs_id");
        this.requestsID = set.getString("requests_id");
        this.iconURL = set.getString("icon_url");
        Map<String, Integer> spawnJSON = plugin.getJSONMapper().readValue(set.getString("spawn_point"), Map.class);
        this.spawnPoint = new Location(plugin.getWorld(), spawnJSON.get("x"), spawnJSON.get("y"), spawnJSON.get("z"));

        this.cities = new HashSet<>();
        ResultSet citiesSet = plugin.getSqlManager().select(
                "cities",
                new SQLColumnSet(
                        "name"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "country", "=", this.name
                        )
                )
        ).retrieve();

        while (citiesSet.next()) {
            this.cities.add(citiesSet.getString("name"));
        }

        this.projectTypes = new LinkedHashMap<>();
        List<String> projectTypeIDs = plugin.getJSONMapper().readValue(set.getString("project_types"), ArrayList.class);
        for (String projectTypeID : projectTypeIDs) {
            this.projectTypes.put(projectTypeID, new ProjectType(plugin, this, projectTypeID));
        }
    }

    public BTEConoSur getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Guild getGuild() {
        return plugin.getBot().getGuildById(guildID);
    }

    public String getGuildID() {
        return guildID;
    }

    public TextChannel getShowcaseChannel() {
        return plugin.getBot().getTextChannelById(showcaseID);
    }

    public TextChannel getGlobalChatChannel() {
        return plugin.getBot().getTextChannelById(globalChatID);
    }

    public TextChannel getCountryChatChannel() {
        return plugin.getBot().getTextChannelById(countryChatID);
    }

    public TextChannel getLogsChannel() {
        return plugin.getBot().getTextChannelById(logsID);
    }

    public TextChannel getRequestsChannel() {
        return plugin.getBot().getTextChannelById(requestsID);
    }

    public String getIconURL() {
        return iconURL;
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public Set<String> getCities() {
        return cities;
    }

    public boolean isType(String type) {
        return projectTypes.containsKey(type);
    }

    public ProjectType getProjectType(String type) {
        return projectTypes.get(type);
    }

    public List<ProjectType> getProjectTypes() {
        return new ArrayList<>(projectTypes.values());
    }

    public List<ProjectType> getUnlockedProjectTypes(ProjectManager manager) {
        List<ProjectType> result = new ArrayList<>();
        for (ProjectType type : projectTypes.values()) {
            if (type.isUnlocked(manager)) {
                result.add(type);
            }
        }
        return result;
    }

    public Set<String> getPendingProjectsIDs() {
        Set<String> result = new HashSet<>();
        try {
            ResultSet set = plugin.getSqlManager().select(
                    "projects",
                    new SQLColumnSet(
                            "id"
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "pending", "=", true
                            ),
                            new SQLOperatorCondition(
                                    "country", "=", this.name
                            )
                    )
            ).retrieve();

            while (set.next()) {
                result.add(set.getString("id"));
            }
        } catch (SQLException ignored) {}
        return result;
    }

    @Override
    public String getJSON(boolean insideJSON) {
        return (insideJSON?"\"":"'") + this.name + (insideJSON?"\"":"'");
    }
}
