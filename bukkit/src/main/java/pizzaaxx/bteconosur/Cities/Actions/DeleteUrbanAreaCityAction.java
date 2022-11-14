package pizzaaxx.bteconosur.Cities.Actions;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.SQLException;
import java.util.List;

public class DeleteUrbanAreaCityAction {

    private final BTEConoSur plugin;
    private final String name;


    public DeleteUrbanAreaCityAction(BTEConoSur plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public void execute() throws CityActionException, SQLException {
        if (!plugin.getCityManager().exists(name)) {
            throw new CityActionException();
        }

        if (plugin.getRegionManager().hasRegion("city_" + name + "_urban")) {

            plugin.getRegionManager().removeRegion("city_" + name + "_urban");

            plugin.getSqlManager().update(
                    "cities",
                    new SQLValuesSet(
                            new SQLValue(
                                    "urban_area",
                                    false
                            )
                    ),
                    new SQLConditionSet(
                            new SQLOperatorCondition(
                                    "name", "=", name
                            )
                    )
            ).execute();

            plugin.getCityManager().reloadCity(name);
        }
    }
}
