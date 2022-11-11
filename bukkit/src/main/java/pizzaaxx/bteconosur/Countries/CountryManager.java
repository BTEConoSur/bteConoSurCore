package pizzaaxx.bteconosur.Countries;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CountryManager {

    private final BTEConoSur plugin;
    private final Map<String, Country> countries = new HashMap<>();
    private final Map<String, String> abbreviations = new HashMap<>();

    public CountryManager(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void init() throws SQLException, JsonProcessingException {
        ResultSet set = plugin.getSqlManager().select(
                "countries",
                new SQLColumnSet(
                        "*"
                ),
                new SQLConditionSet()
        ).retrieve();

        while (set.next()) {
            countries.put(set.getString("name"), new Country(plugin, set));
        }
    }

    public boolean exists(@NotNull String nameOrAbbreviation) {
        if (nameOrAbbreviation.length() == 2) {
            return countries.containsKey(abbreviations.get(nameOrAbbreviation));
        } else {
            return countries.containsKey(nameOrAbbreviation);
        }
    }

    public Country get(@NotNull String nameOrAbbreviation) {
        if (nameOrAbbreviation.length() == 2) {
            return countries.get(abbreviations.get(nameOrAbbreviation));
        } else {
            return countries.get(nameOrAbbreviation);
        }
    }

    public Collection<Country> getAllCountries() {
        return countries.values();
    }
}
