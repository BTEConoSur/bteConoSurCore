package pizzaaxx.bteconosur.country.cities.projects;

import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.City;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectsRegistry {

    private final City city;
    private final BteConoSur plugin;
    private final Map<String, Project> registry = new HashMap<>();

    private final Map<String, Long> deletionRegistry = new HashMap<>();

    private final Set<String> ids = new HashSet<>();

    public ProjectsRegistry(@NotNull City city, @NotNull BteConoSur plugin) {

        this.city = city;
        this.plugin = plugin;

        File folder = new File(city.getFolder(), "/projects");
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                ids.add(file.getName().replace(".yml", ""));
            }
        }

    }

    public boolean exists(String id) {
        return ids.contains(id);
    }

    public boolean hasLoaded() {
        return !registry.isEmpty();
    }

    public boolean isRegistered(String id) {
        return registry.containsKey(id);
    }

    public void register(Project project) {
        registry.put(project.getId(), project);
    }

    public void unregister(String id) {
        registry.get(id).saveToDisk();
        registry.remove(id);
        deletionRegistry.remove(id);
    }

    public Project get(String id) {
        if (exists(id)) {
            if (!isRegistered(id)) {
                register(new Project(city, id, plugin));
            }
            scheduleDeletion(id);
            return registry.get(id);
        }
        return null;
    }

    public City getCity() {
        return city;
    }

    public BteConoSur getPlugin() {
        return plugin;
    }

    private void scheduleDeletion(String id) {
        deletionRegistry.put(id, System.currentTimeMillis());
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (deletionRegistry.containsKey(id) && System.currentTimeMillis() - deletionRegistry.get(id) > 590000) {
                    unregister(id);
                }
            }
        };
        runnable.runTaskLaterAsynchronously(plugin, 12000);
    }
}
