package pizzaaxx.bteconosur.country.cities;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.Country;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityRegistry {

    private final Country country;
    private final Map<String, City> registry = new HashMap<>();
    private final Map<String, Long> deletionRegistry = new HashMap<>();
    private final List<String> names = new ArrayList<>();
    private final BteConoSur plugin;

    public CityRegistry(@NotNull Country country, BteConoSur plugin) {

        this.country = country;
        this.plugin = plugin;

        File folder = new File(country.getFolder(), "countries/" + country.getName() + "/cities");
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                names.add(file.getName().replace(".yml", ""));
            }
        }

    }

    public boolean exists(String name) {
        return names.contains(name);
    }

    public boolean isRegistered(String name) {
        return registry.containsKey(name);
    }

    public void register(@NotNull City city) {
        if (exists(city.getName())) {
            registry.put(city.getName(), city);
        }
    }

    public void unregister(String name) {
        registry.remove(name);
        deletionRegistry.remove(name);
    }

    public City get(String name) {
        if (exists(name)) {
            if (!isRegistered(name)) {
                register(new City(country, name, plugin));
            }
            scheduleDeletion(name);
            return registry.get(name);
        }
        return null;
    }

    public boolean isInsideCity(@NotNull Location loc) {
        if (country.isInside(loc)){
            for (ProtectedRegion region : plugin.getWorldGuard().getRegionManager(plugin.getWorld()).getApplicableRegions(loc)) {

                if (region.getId().startsWith("city_")) {

                    String cityName = region.getId().replace("city_", "");
                    if (exists(cityName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInsideCity(@NotNull BlockVector2D vector) {
        return this.isInsideCity(new Location(plugin.getWorld(), vector.getX(), 100, vector.getZ()));
    }

    public City get(@NotNull Location loc) {
        if (country.isInside(loc)){
            for (ProtectedRegion region : plugin.getWorldGuard().getRegionManager(plugin.getWorld()).getApplicableRegions(loc)) {

                if (region.getId().startsWith("city_")) {

                    String cityName = region.getId().replace("city_", "");
                    if (exists(cityName)) {
                        return get(cityName);
                    }
                }
            }
        }
        return null;
    }

    public City get(@NotNull BlockVector2D vector) {
        return this.get(new Location(plugin.getWorld(), vector.getX(), 100, vector.getZ()));
    }

    // TODO ADD PROJECT LOADING CHECK
    public void scheduleDeletion(String name) {

        deletionRegistry.put(name, System.currentTimeMillis());

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (deletionRegistry.containsKey(name)) {
                    if (registry.get(name).getProjectsRegistry().hasLoaded()) {
                        scheduleDeletion(name);
                    } else if (System.currentTimeMillis() - deletionRegistry.get(name) > 590000) {
                        unregister(name);
                    }
                }
            }
        };

        runnable.runTaskLaterAsynchronously(plugin, 12000);

    }

    public Country getCountry() {
        return country;
    }

    public List<String> getNames() {
        return names;
    }
}
