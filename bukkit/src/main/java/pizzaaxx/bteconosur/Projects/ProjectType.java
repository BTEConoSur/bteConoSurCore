package pizzaaxx.bteconosur.Projects;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectType {

    private final BTEConoSur plugin;
    private final Country country;

    private final String name;
    private final String displayName;
    private final int maxMembers;
    private final List<Integer> pointsOptions;
    private final Map<String, Integer> unlockProjects;

    public ProjectType(@NotNull BTEConoSur plugin, Country country, String name) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        this.country = country;
        this.name = name;

        ResultSet set = plugin.getSqlManager().select(
                "project_types",
                new SQLColumnSet("*"),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "name", "=", name
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.displayName = set.getString("display_name");
            this.maxMembers = set.getInt("max_members");
            this.pointsOptions = plugin.getJSONMapper().readValue(set.getString("points_options"), ArrayList.class);
            this.unlockProjects = plugin.getJSONMapper().readValue(set.getString("unlock_projects"), HashMap.class);

        } else {
            throw new IllegalArgumentException();
        }
    }

    public BTEConoSur getPlugin() {
        return plugin;
    }

    public Country getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public List<Integer> getPointsOptions() {
        return pointsOptions;
    }

    public Integer getRequiredProjects(String projectTypeName) {
        return unlockProjects.get(projectTypeName);
    }

    public Integer getRequiredProjects(@NotNull ProjectType type) {
        return unlockProjects.get(type.getName());
    }
}
