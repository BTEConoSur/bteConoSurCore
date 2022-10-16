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
import java.util.*;

public class CityRegistry {

    private final Country country;
    private final Map<String, City> registry = new HashMap<>();
    private final Map<String, Long> deletionRegistry = new HashMap<>();
    private final Map<String, ProtectedRegion> regions = new HashMap<>();
    private final BteConoSur plugin;

    public CityRegistry(@NotNull Country country, BteConoSur plugin) {

        this.country = country;
        this.plugin = plugin;

        File folder = new File(country.getFolder(), "countries/" + country.getName() + "/cities");
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                regions.put(name, plugin.getRegionsManager().getRegion("city_" + country.getName() + "_" + name));
            }
        }

    }

    public boolean exists(String name) {
        return regions.containsKey(name);
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

                    String cityName = region.getId().split("_")[2];
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

                    String cityName = region.getId().split("_")[2];
                    if (exists(cityName)) {
                        return get(cityName);
                    }
                }
            }
        }
        return null;
    }

    public Collection<ProtectedRegion> getRegions() {
        Map<String, ProtectedRegion> map = new HashMap<>(regions);
        map.remove("default");
        return map.values();
    }

    /**
     * Get the city this belongs region to. Alphabetical order is used if there's more than one city.
     * @param region The region.
     * @return
     */
    public City get(@NotNull ProtectedRegion region) {
        List<ProtectedRegion> intersecting = region.getIntersectingRegions(getRegions());

        if (intersecting.isEmpty()) {
            return get("default");
        }

        List<String> names = new ArrayList<>();
        for (ProtectedRegion r : intersecting) {
            names.add(r.getId().split("_")[2]);
        }

        Collections.sort(names);

        return get(names.get(0));
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
        List<String> names = new ArrayList<>(regions.keySet());
        names.remove("default");
        return names;
    }
}
