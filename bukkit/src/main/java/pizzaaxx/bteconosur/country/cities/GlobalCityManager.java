package pizzaaxx.bteconosur.country.cities;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.Country;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlobalCityManager {

    private final Map<String, Set<Country>> registry = new HashMap<>();

    private final BteConoSur plugin;

    public GlobalCityManager(BteConoSur plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        final File countriesFolder = new File(plugin.getDataFolder(), "/countries");
        final File[] countries = countriesFolder.listFiles();

        if (countries != null) {
            for (File country : countries) {

                final File citiesFolder = new File(country, "/cities");
                final File[] cities = citiesFolder.listFiles();

                if (cities != null) {
                    for (File city : cities) {

                        String cityName = city.getName().replace(".yml", "");
                        Country countryObj = plugin.getCountryManager().get(country.getName().replace(".yml", ""));

                        Set<Country> c = registry.getOrDefault(cityName, new HashSet<>());
                        c.add(countryObj);
                        registry.put(cityName, c);

                    }
                }
            }
        }
    }

    public void add(@NotNull City city) {

        String cityName = city.getName();
        Country country = city.getCountry();

        Set<Country> countries = registry.getOrDefault(cityName, new HashSet<>());
        countries.add(country);
        registry.put(cityName, countries);

    }

    public void remove(@NotNull String name, @NotNull Country country) {
        if (exists(name)) {
            Set<Country> countries = registry.getOrDefault(name, new HashSet<>());
            if (countries.contains(country)) {
                if (countries.size() == 1) {
                    registry.remove(name);
                } else {
                    countries.remove(country);
                    registry.put(name, countries);
                }
            }
        }
    }

    public boolean exists(@NotNull String name) {
        return registry.containsKey(name);
    }

    /**
     *
     * @param name The name of the City, check if it exists first.
     * @return Whether the city is present in 2 more countries or not.
     */
    public boolean isMultiple(@NotNull String name) {

        if (exists(name)) {
            return (registry.get(name).size() > 1);
        }
        return false;

    }

    public Map<Country, City> getFromName(@NotNull String name) {

        Map<Country, City> map = new HashMap<>();
        if (exists(name)) {

            for (Country country : registry.get(name)) {

                map.put(country, country.getCityRegistry().get(name));

            }
            return map;

        }
        return new HashMap<>();

    }

    public boolean isInCity(Location loc) {
        for (ProtectedRegion region : plugin.getWorldGuard().getRegionManager(plugin.getWorld()).getApplicableRegions(loc)) {
            if (region.getId().startsWith("city_")) {
                return true;
            }
        }
        return false;
    }
}
