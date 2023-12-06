package pizzaaxx.bteconosur.projects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectType {

    private final BTEConoSurPlugin plugin;
    private final String name;
    private final String displayName;
    private final int maxMembers;
    private final List<Integer> pointOptions;
    private final Map<String, Integer> unlockRequirements;
    private final Color color;
    private final String description;

    public ProjectType(@NotNull BTEConoSurPlugin plugin, String name) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        this.name = name;
        try (SQLResult result = plugin.getSqlManager().select(
                "project_types",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("name", "=", name)
                )
        ).retrieve()) {
            ResultSet set = result.getResultSet();
            if (!set.next()) {
                throw new SQLException("Project type not found.");
            }

            this.displayName = set.getString("display_name");
            this.maxMembers = set.getInt("max_members");

            this.pointOptions = new ArrayList<>();
            JsonNode pointOptionsNode = plugin.getJsonMapper().readTree(set.getString("points_options"));
            for (JsonNode point_option : pointOptionsNode) {
                this.pointOptions.add(point_option.asInt());
            }

            this.unlockRequirements = new HashMap<>();
            JsonNode unlockRequirementsNode = plugin.getJsonMapper().readTree(set.getString("unlock_requirements"));
            Iterable<Map.Entry<String, JsonNode>> unlockRequirementsFields = unlockRequirementsNode::fields;
            for (Map.Entry<String, JsonNode> field : unlockRequirementsFields) {
                this.unlockRequirements.put(field.getKey(), field.getValue().asInt());
            }


            // get color from hex
            this.color = Color.decode("#" + set.getString("color"));
            this.description = set.getString("description");
        }
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

    public List<Integer> getPointOptions() {
        return pointOptions;
    }

    public Map<String, Integer> getUnlockRequirements() {
        return unlockRequirements;
    }

    public Color getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    // TODO
    public boolean isUnlocked() {
        return true;
    }
}
