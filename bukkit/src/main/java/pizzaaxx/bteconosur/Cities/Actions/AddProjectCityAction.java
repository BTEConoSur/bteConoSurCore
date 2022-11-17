package pizzaaxx.bteconosur.Cities.Actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class AddProjectCityAction {

    private final BTEConoSur plugin;
    private final String name;
    private final String id;

    public AddProjectCityAction(BTEConoSur plugin, String name, String id) {
        this.plugin = plugin;
        this.name = name;
        this.id = id;
    }

    public void execute() throws SQLException, JsonProcessingException, CityActionException {
        if (!plugin.getCityManager().exists(name)) {
            throw new CityActionException();
        }

        ResultSet set = plugin.getSqlManager().select(
                "cities",
                new SQLColumnSet(
                        "projects"
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "name", "=", name
                        )
                )
        ).retrieve();

        if (set.next()) {
            Set<String> ids = plugin.getJSONMapper().readValue(set.getString("projects"), HashSet.class);
            ids.add(id);
            plugin.getSqlManager().update(
                    "cities",
                    new SQLValuesSet(
                            new SQLValue(
                                    "projects",
                                    ids
                            )
                    ),
                    new SQLConditionSet(
                            new SQLOperatorCondition(
                                    "name", "=", name
                            )
                    )
            ).execute();
        } else {
            throw new SQLException();
        }

        plugin.getCityManager().reloadCity(name);
    }

}
