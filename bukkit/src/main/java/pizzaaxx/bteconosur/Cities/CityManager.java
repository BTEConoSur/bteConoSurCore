package pizzaaxx.bteconosur.Cities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.Actions.CreateCityAction;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CityManager {

    private final Map<String, City> citiesCache = new HashMap<>();
    private final Map<String, Long> deletionCache = new HashMap<>();
    private final Set<String> names = new HashSet<>();
    public final Map<String, String> displayNames = new HashMap<>();

    private final BTEConoSur plugin;

    public CityManager(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void init() throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "cities",
                new SQLColumnSet(
                        "name",
                        "display_name"
                ),
                new SQLANDConditionSet()
        ).retrieve();

        while (set.next()) {
            names.add(set.getString("name"));
            displayNames.put(set.getString("name"), set.getString("display_name"));
        }
    }


    public boolean exists(String name) {
        return names.contains(name);
    }

    public boolean isLoaded(String name) {
        return citiesCache.containsKey(name);
    }

    public void load(String name) throws SQLException, JsonProcessingException {
        citiesCache.put(name, new City(plugin, name));
        this.scheduleDeletion(name);
    }

    public void unload(String name) {
        citiesCache.remove(name);
        deletionCache.remove(name);
    }

    public City get(String name) {
        if (!this.isLoaded(name)) {
            try {
                this.load(name);
            } catch (SQLException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        this.scheduleDeletion(name);
        return citiesCache.get(name);
    }

    public City getCityAt(Location loc) {
        for (ProtectedRegion region : plugin.getRegionManager().getApplicableRegions(loc)) {
            if (region.getId().startsWith("city_")) {
                String name = region.getId().replace("city_", "").replace("_urban", "");
                return this.get(name);
            }
        }
        return null;
    }

    public Collection<String> getNames() {
        return names;
    }

    public String getDisplayName(String name) {
        return displayNames.get(name);
    }

    public void registerName(String name, String displayName) {
        names.add(name);
        displayNames.put(name, displayName);
    }

    public void unregisterName(String name) {
        names.remove(name);
        deletionCache.remove(name);
        citiesCache.remove(name);
    }

    public void reloadCity(String name) {
        if (this.isLoaded(name)) {
            unload(name);
            try {
                load(name);
            } catch (SQLException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public CreateCityAction createCity(String name, String displayName, Country country, List<BlockVector2D> points) {
        return new CreateCityAction(
                plugin,
                name,
                displayName,
                country,
                points
        );
    }

    public void scheduleDeletion(String name) {
        if (this.isLoaded(name)) {
            deletionCache.put(name, System.currentTimeMillis());
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (deletionCache.containsKey(name) && System.currentTimeMillis() - deletionCache.get(name) > 550000) {
                        unload(name);
                    }
                }
            };
            runnable.runTaskLaterAsynchronously(plugin, 12000);
        }
    }

    public List<String> getCloseMatches(@NotNull String input, int limit) {

        List<Map.Entry<String, Integer>> entries = new ArrayList<>();

        String i = input.toLowerCase();
        for (String name : displayNames.keySet()) {

            int distance  = plugin.getFuzzyMatcher().getDistance(i, displayNames.get(name).toLowerCase());

            if (distance <= limit) {
                entries.add(
                        new AbstractMap.SimpleEntry<>(
                                name,
                                distance
                        )
                );
            }

        }

        entries.sort(Map.Entry.comparingByValue());

        return entries.stream().map(Map.Entry::getKey).collect(Collectors.toList());

    }
}
