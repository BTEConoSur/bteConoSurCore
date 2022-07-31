package pizzaaxx.bteconosur.country.cities;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
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
    private final Plugin plugin;

    public CityRegistry(@NotNull Country country, Plugin plugin) {

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
        return registry.get(name);
    }

    // TODO ADD PROJECT LOADING CHECK
    public void scheduleDeletion(String name) {

        deletionRegistry.put(name, System.currentTimeMillis());

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (deletionRegistry.containsKey(name)) {
                    if (System.currentTimeMillis() - deletionRegistry.get(name) > 590000) {
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
}
