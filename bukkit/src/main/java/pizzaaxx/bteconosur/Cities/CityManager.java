package pizzaaxx.bteconosur.Cities;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CityManager {

    private final Map<String, City> citiesCache = new HashMap<>();
    private final Map<String, Long> deletionCache = new HashMap<>();
    private final Map<String, String> namesToID = new HashMap<>();

    private final BTEConoSur plugin;

    public CityManager(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void init() throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "cities",
                new SQLColumnSet(
                        "id",
                        "name"
                ),
                new SQLConditionSet()
        ).retrieve();

        while (set.next()) {
            namesToID.put(set.getString("name"), set.getString("id"));
        }
    }

    public boolean existsID(String id) {
        return namesToID.containsValue(id);
    }

    public boolean existsName(String name) {
        return namesToID.containsKey(name);
    }



}
