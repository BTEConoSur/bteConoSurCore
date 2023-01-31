package pizzaaxx.bteconosur.Cities.Actions;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.SQLException;

public class SetDisplayNameCityAction {

    private final BTEConoSur plugin;
    private final String name;
    private final String displayName;


    public SetDisplayNameCityAction(BTEConoSur plugin, String name, String displayName) {
        this.plugin = plugin;
        this.name = name;
        this.displayName = displayName;
    }

    public void execute() throws CityActionException, SQLException {
        if (!plugin.getCityManager().exists(name)) {
            throw new CityActionException();
        }

        plugin.getSqlManager().update(
                "cities",
                new SQLValuesSet(
                        new SQLValue(
                                "display_name",
                                this.displayName
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "name", "=", this.name
                        )
                )
        ).execute();

        plugin.getCityManager().reloadCity(name);
    }
}
