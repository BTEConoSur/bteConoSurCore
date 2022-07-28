package pizzaaxx.bteconosur.cities;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.worldguard.WorldGuardProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class CityRegistry {

    private final Plugin plugin;

    private final OldCountry country;

    private final List<String> names = new ArrayList<>();
    private final Map<String, City> registry = new HashMap<>();
    private final Map<String, Long> deletionRegistry = new HashMap<>();


    public CityRegistry(@NotNull Plugin plugin, String countryName) {
        this.plugin = plugin;
        this.country = new OldCountry(countryName);

        File directory = new File(plugin.getDataFolder(), "cities/" + countryName);
        File[] files = directory.listFiles();

        if (files != null) {

            for (File file : files) {

                names.add(file.getName().replace(".yml", ""));

            }

        }

    }

    public void createCity(String name, List<BlockVector2D> points) {

        if (!exists(name)) {

            String path = "cities/" + country.getName() + "/" + name;

            File configFile = new File(plugin.getDataFolder(), path + ".yml");
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            RegionManager manager = WorldGuardProvider.getWorldGuard().getRegionManager(mainWorld);

            ProtectedPolygonalRegion polygonalRegion = new ProtectedPolygonalRegion("city_" + country.getName() + "_" + name, points, -100, 8000);

            manager.addRegion(polygonalRegion);

            names.add(name);

        }

    }

    public void deleteCity(String name) {

        if (exists(name)) {

            if (isLoaded(name)) {
                remove(name);
            }

            names.remove(name);
            String path = "cities/" + country.getName() + "/" + name;
            File configFile = new File(plugin.getDataFolder(), path + ".yml");
            configFile.delete();

        }

    }

    public Plugin getPlugin() {
        return plugin;
    }

    public OldCountry getCountry() {
        return country;
    }

    public void register(City city) {

        registry.put(city.getName(), city);
        scheduleDeletion(city.getName());

    }

    public City get(String name) {

        scheduleDeletion(name);
        return registry.get(name);

    }

    public boolean isLoaded(String name) {

        return registry.containsKey(name);

    }

    public boolean exists(String name) {

        return names.contains(name);

    }

    public void remove(String name) {

        registry.remove(name);
        deletionRegistry.remove(name);

    }

    public void scheduleDeletion(String name) {

        deletionRegistry.put(name, System.currentTimeMillis());

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {

                if (deletionRegistry.containsKey(name) && System.currentTimeMillis() - deletionRegistry.get(name) > 590000) {

                    registry.remove(name);
                    deletionRegistry.remove(name);

                }
            }
        };

        runnable.runTaskLaterAsynchronously(plugin, 12000);

    }

}
