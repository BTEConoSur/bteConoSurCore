package pizzaaxx.bteconosur.Cities.Actions;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.SQLException;

public class DeleteUrbanAreaCityAction {

    private final BTEConoSur plugin;
    private final String name;


    public DeleteUrbanAreaCityAction(BTEConoSur plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public void execute() {
        if (plugin.getRegionManager().hasRegion("city_" + name + "_urban")) {
            plugin.getRegionManager().removeRegion("city_" + name + "_urban");
            plugin.getCityManager().reloadCity(name);
        }
    }
}
