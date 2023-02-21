package pizzaaxx.bteconosur.Cities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.Actions.*;
import pizzaaxx.bteconosur.Showcases.Showcase;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLJSONArrayCondition;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.JSONParsable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class City implements JSONParsable {

    private final BTEConoSur plugin;
    private final String name;
    private final String displayName;
    private final Country country;
    private List<Showcase> showcases = null;
    private final ProtectedRegion region;
    private ProtectedRegion urbanRegion;

    public City(@NotNull BTEConoSur plugin, String name) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        ResultSet set = plugin.getSqlManager().select(
                "cities",
                new SQLColumnSet(
                        "*"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "name", "=", name
                        )
                )
        ).retrieve();

        if (set.next()) {
            this.name = set.getString("name");
            this.displayName = set.getString("display_name");
            this.country = plugin.getCountryManager().get(set.getString("country"));
            if (plugin.getRegionManager().hasRegion("city_" + this.name + "_urban")) {
                this.urbanRegion = plugin.getRegionManager().getRegion("city_" + this.name + "_urban");
            }
            this.region = plugin.getRegionManager().getRegion("city_" + this.name);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public List<Showcase> getShowcases() throws SQLException {

        if (showcases == null) {
            ResultSet set = plugin.getSqlManager().select(
                    "showcases",
                    new SQLColumnSet(
                            "message_id",
                            "project_id"
                    ),
                    new SQLANDConditionSet(
                            new SQLJSONArrayCondition("cities", this.name)
                    )
            ).retrieve();

            showcases = new ArrayList<>();
            while (set.next()) {
                showcases.add(new Showcase(set.getString("message_id"), set.getString("project_id")));
            }
            return showcases;
        }
        return showcases;
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

    public Country getCountry() {
        return country;
    }

    public List<String> getProjects() throws SQLException {
        List<String> ids = new ArrayList<>();
        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "id"
                ),
                new SQLANDConditionSet(
                        new SQLJSONArrayCondition(
                                "cities", this.name
                        )
                )
        ).retrieve();

        while (set.next()) {
            ids.add(set.getString("id"));
        }

        return ids;
    }

    public boolean hasUrbanArea() {
        return urbanRegion != null;
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

    public RedefineRegionCityAction redefine(List<BlockVector2D> points) {
        return new RedefineRegionCityAction(
                plugin,
                name,
                points
        );
    }


    @Override
    public String getJSON(boolean insideJSON) {
        return (insideJSON?"\"":"'") + this.name + (insideJSON?"\"":"'");
    }

    @Override
    public boolean equals(Object obj) {

        if (getClass() != obj.getClass()) {
            return false;
        }

        City city = (City) obj;
        return this.name.equals(city.name);
    }
}
