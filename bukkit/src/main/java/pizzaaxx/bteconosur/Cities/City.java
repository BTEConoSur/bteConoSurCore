package pizzaaxx.bteconosur.Cities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.Actions.*;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class City {

    private final BTEConoSur plugin;
    private final String name;
    private final String displayName;
    private final Set<String> showcaseIDs;
    private final Set<String> projects;
    private final Country country;
    private final boolean hasUrbanArea;
    private final ProtectedRegion region;
    private ProtectedRegion urbanRegion;

    public City(@NotNull BTEConoSur plugin, String name) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        ResultSet set = plugin.getSqlManager().select(
                "cities",
                new SQLColumnSet(
                        "*"
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "name", "=", name
                        )
                )
        ).retrieve();

        if (set.next()) {
            this.name = set.getString("name");
            this.displayName = set.getString("display_name");
            this.showcaseIDs = plugin.getJSONMapper().readValue(set.getString("showcase_ids"), HashSet.class);
            this.projects = plugin.getJSONMapper().readValue(set.getString("projects"), HashSet.class);
            this.country = plugin.getCountryManager().get(set.getString("country"));
            this.hasUrbanArea = set.getBoolean("urban_area");
            if (this.hasUrbanArea) {
                this.urbanRegion = plugin.getRegionManager().getRegion("city_" + this.name + "_urban");
            }
            this.region = plugin.getRegionManager().getRegion("city_" + this.name);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public BTEConoSur getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<String> getShowcaseIDs() {
        return showcaseIDs;
    }

    public Set<String> getProjects() {
        return projects;
    }

    public Country getCountry() {
        return country;
    }

    public boolean hasUrbanArea() {
        return hasUrbanArea;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public ProtectedRegion getUrbanRegion() {
        return urbanRegion;
    }

    public SetDisplayNameCityAction setDisplayName(@NotNull String displayName) {
        return new SetDisplayNameCityAction(
                plugin,
                name,
                displayName
        );
    }

    public SetUrbanAreaCityAction setUrbanArea(List<BlockVector2D> points) {
        return new SetUrbanAreaCityAction(
                plugin,
                name,
                points
        );
    }

    public DeleteUrbanAreaCityAction deleteUrbanArea() {
        return new DeleteUrbanAreaCityAction(
                plugin,
                name
        );
    }

    public AddShowcaseIDCityAction addShowcaseID(String id) {
        return new AddShowcaseIDCityAction(
                plugin,
                name,
                id
        );
    }

    public RemoveShowcaseIDCityAction removeShowcaseID(String id) {
        return new RemoveShowcaseIDCityAction(
                plugin,
                name,
                id
        );
    }

    public AddProjectCityAction addProject(String id) {
        return new AddProjectCityAction(
                plugin,
                name,
                id
        );
    }

    public RemoveProjectCityAction removeProject(String id) {
        return new RemoveProjectCityAction(
                plugin,
                name,
                id
        );
    }

    public RedefineRegionCityAction redefine(List<BlockVector2D> points) {
        return new RedefineRegionCityAction(
                plugin,
                name,
                points
        );
    }

}
