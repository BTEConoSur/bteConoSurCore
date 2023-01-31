package pizzaaxx.bteconosur.Countries.Actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class AddCityProjectAction {

    private final BTEConoSur plugin;
    private final Country country;
    private final String name;

    public AddCityProjectAction(BTEConoSur plugin, Country country, String name) {
        this.plugin = plugin;
        this.country = country;
        this.name = name;
    }

    public void execute() throws SQLException, JsonProcessingException {
        ResultSet set = plugin.getSqlManager().select(
                "countries",
                new SQLColumnSet(
                        "cities"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "name", "=", country.getName()
                        )
                )
        ).retrieve();

        if (set.next()) {
            Set<String> cities = plugin.getJSONMapper().readValue(set.getString("cities"), HashSet.class);
            cities.add(name);
            plugin.getSqlManager().update(
                    "countries",
                    new SQLValuesSet(
                            new SQLValue(
                                    "cities",
                                    cities
                            )
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "name", "=", country.getName()
                            )
                    )
            ).execute();

            country.cities.add(name);
        } else {
            throw new SQLException();
        }
    }
}
