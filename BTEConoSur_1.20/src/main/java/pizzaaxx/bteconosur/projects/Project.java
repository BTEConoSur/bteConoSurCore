package pizzaaxx.bteconosur.projects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import net.kyori.adventure.text.Component;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplay;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplayProvider;
import pizzaaxx.bteconosur.projects.ProjectType;
import pizzaaxx.bteconosur.utils.SQLUtils;
import pizzaaxx.bteconosur.utils.registry.RegistrableEntity;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Project implements RegistrableEntity<String>, ScoreboardDisplay {

    private final BTEConoSurPlugin plugin;
    private final String id;
    private String name;
    private final Country country;
    private final int city;
    private long pending;
    private ProjectType type;
    private int points;
    private Set<UUID> members;
    private UUID owner;
    private Polygon polygon;

    public Project(BTEConoSurPlugin plugin, String id) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        this.id = id;
        try (SQLResult result = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet("*, ST_AsGeoJSON(region) AS region_json"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("id", "=", id)
                )
        ).retrieve()) {
            ResultSet set = result.getResultSet();
            if (!set.next()) {
                throw new SQLException("Project not found.");
            }
            this.name = set.getString("name");
            this.country = plugin.getCountriesRegistry().get(set.getString("country"));
            this.city = set.getInt("city");
            this.pending = set.getLong("pending");
            this.type = country.getProjectType(set.getString("type"));
            this.points = set.getInt("points");

            JsonNode membersNode = plugin.getJsonMapper().readTree(set.getString("members"));
            members = new HashSet<>();
            for (JsonNode memberNode : membersNode) {
                members.add(UUID.fromString(memberNode.asText()));
            }

            this.owner = SQLUtils.uuidFromBytes(set.getBytes("owner"));

            JsonNode regionNode = plugin.getJsonMapper().readTree(set.getString("region_json"));
            this.polygon = new Polygon();
            JsonNode coordinatesNode = regionNode.path("coordinates").get(0);
            int nodeSize = coordinatesNode.size();
            int counter = 0;
            for (JsonNode coordinateNode : coordinatesNode) {
                if (counter == nodeSize - 1) {
                    break;
                }
                int x = coordinateNode.get(0).asInt();
                int z = coordinateNode.get(1).asInt();
                polygon.addPoint(x, z);
                counter++;
            }
        }
    }

    public String getDisplayName() {
        if (this.name != null) {
            return name;
        }
        return id.toUpperCase();
    }

    public ProjectType getType() {
        return type;
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isClaimed() {
        return owner != null;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public void disconnected() {

    }

    @Override
    public Component getTitle() {
        return null;
    }

    @Override
    public List<Component> getLines() {
        return null;
    }

    @Override
    public ScoreboardDisplayProvider getProvider() {
        return plugin.getProjectsRegistry();
    }

    @Override
    public boolean isSavable() {
        return true;
    }
}
