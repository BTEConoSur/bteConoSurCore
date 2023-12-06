package pizzaaxx.bteconosur.countries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.SQLResult;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.terra.TerraCoords;
import pizzaaxx.bteconosur.utils.registry.BaseRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CountriesRegistry extends BaseRegistry<Country, String> {

    private final BTEConoSurPlugin plugin;

    public CountriesRegistry(BTEConoSurPlugin plugin) {
        super(
                plugin,
                () -> {
                    List<String> names = new ArrayList<>();
                    try (SQLResult result = plugin.getSqlManager().select(
                            "countries",
                            new SQLColumnSet("name"),
                            new SQLANDConditionSet()
                    ).retrieve()) {
                        ResultSet set = result.getResultSet();
                        while (set.next()) {
                            names.add(set.getString("name"));
                        }
                    } catch (SQLException e) {
                        plugin.error("Could not load countries registry.");
                    }
                    return names;
                },
                name -> {
                    try {
                        return new Country(plugin, name);
                    } catch (SQLException | JsonProcessingException e) {
                        e.printStackTrace();
                        plugin.error("Error loading country instance. (Name: " + name + ")");
                    }
                    return null;
                },
                false
        );
        this.plugin = plugin;
    }

    public List<Country> getCountries() {
        List<Country> countries = new ArrayList<>(super.cacheMap.values());
        countries.sort(Comparator.comparing(Country::getName));
        return countries;
    }

    @Nullable
    public Country getCountryByAbbreviation(String abbreviation) {
        for (Country country : super.cacheMap.values()) {
            if (country.getAbbreviation().equals(abbreviation)) {
                return country;
            }
        }
        return null;
    }

    @Nullable
    public Country getCountryByGuildId(String guildId) {
        for (Country country : super.cacheMap.values()) {
            if (country.getGuildID().equals(guildId)) {
                return country;
            }
        }
        return null;
    }

    @Nullable
    public Country getCountryAt(Location location) {
        for (Country country : super.cacheMap.values()) {
            for (ProtectedPolygonalRegion region : country.getRegions()) {
                if (region.contains(
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ()
                )) {
                    return country;
                }
            }
        }
        return null;
    }

    @Nullable
    public Country getCountryAt(TerraCoords coords) {
        for (Country country : super.cacheMap.values()) {
            for (ProtectedPolygonalRegion region : country.getRegions()) {
                if (region.contains(
                        BlockVector2.at(
                                coords.getX(),
                                coords.getZ()
                        )
                )) {
                    return country;
                }
            }
        }
        return null;
    }

}
