package pizzaaxx.bteconosur.Cities.Actions;

import com.sk89q.worldguard.protection.managers.RegionManager;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLJSONArrayCondition;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DeleteCityAction {

    private final BTEConoSur plugin;
    private final City city;

    public DeleteCityAction(BTEConoSur plugin, City city) {
        this.plugin = plugin;
        this.city = city;
    }

    public void execute() throws SQLException, IOException {

        RegionManager regionManager = plugin.getRegionManager();

        if (regionManager.hasRegion("city_" + city.getName())) {
            regionManager.removeRegion("city_" + city.getName());
        }

        if (city.hasUrbanArea()) {
            if (regionManager.hasRegion("city_" + city.getName() + "_urban")) {
                regionManager.removeRegion("city_" + city.getName() + "_urban");
            }
        }

        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet("cities", "id"),
                new SQLANDConditionSet(
                        new SQLJSONArrayCondition(
                                "cities", city.getName()
                        )
                )
        ).retrieve();

        while (set.next()) {

            Set<String> cities = plugin.getJSONMapper().readValue(set.getString("cities"), HashSet.class);
            cities.remove(city.getName());
            plugin.getSqlManager().update(
                    "projects",
                    new SQLValuesSet(
                            new SQLValue(
                                    "cities", cities
                            )
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "id", "=", set.getString("id")
                            )
                    )
            ).execute();

            Project project = plugin.getProjectRegistry().get(set.getString("id"));
            project.update();

        }

        plugin.getSqlManager().delete(
                    "cities",
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "name", "=", city.getName()
                            )
                    )
            ).execute();
    }

}
