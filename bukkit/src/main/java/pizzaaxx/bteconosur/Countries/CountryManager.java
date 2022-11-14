package pizzaaxx.bteconosur.Countries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
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
            abbreviations.put(set.getString("abbreviation"), set.getString("name"));
        }
    }

    public boolean exists(@NotNull String nameOrAbbreviation) {
        if (nameOrAbbreviation.length() == 2) {
            return abbreviations.containsKey(nameOrAbbreviation);
        } else {
            return countries.containsKey(nameOrAbbreviation);
        }
    }

    public Country get(@NotNull String nameOrAbbreviation) {
        if (nameOrAbbreviation.length() == 2) {
            if (abbreviations.containsKey(nameOrAbbreviation)) {
                return countries.get(abbreviations.get(nameOrAbbreviation));
            }
            return null;
        } else {
            return countries.get(nameOrAbbreviation);
        }
    }

    public boolean isInsideCountry(Location loc) {
        for (ProtectedRegion region : plugin.getRegionManager().getApplicableRegions(loc)) {
            if (region.getId().startsWith("country")) {
                return true;
            }
        }
        return false;
    }

    public Country getCountryAt(Location loc) {
        for (ProtectedRegion region : plugin.getRegionManager().getApplicableRegions(loc)) {
            if (region.getId().startsWith("country")) {
                // country_chile_1
                String name = region.getId().split("_")[1];
                return this.get(name);
            }
        }
        return null;
    }

    public Collection<Country> getAllCountries() {
        return countries.values();
    }
}
