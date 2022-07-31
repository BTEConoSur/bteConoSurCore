package pizzaaxx.bteconosur.country.cities.projects;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.country.cities.City;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectsRegistry {

    private final City city;
    private final Map<String, NewProject> registry = new HashMap<>();

    private final Map<String, Long> deletionRegistry = new HashMap<>();

    private final Set<String> ids = new HashSet<>();

    public ProjectsRegistry(@NotNull City city) {

        this.city = city;

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

    public boolean isRegistered(String id) {
        return registry.containsKey(id);
    }

    public void register(NewProject project) {

    }

    public void unregister(String id) {
        registry.remove(id);
        deletionRegistry.remove(id);
    }

    public NewProject get(String id) {

    }

    public City getCity() {
        return city;
    }
}
