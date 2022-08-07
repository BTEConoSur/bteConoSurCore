package pizzaaxx.bteconosur.country.cities.projects;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.helper.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GlobalProjectsManager {

    private final Map<String, Pair<Country, String>> registry = new HashMap<>();

    private final BteConoSur plugin;

    public GlobalProjectsManager(BteConoSur plugin) {
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

                        final File projectsFolder = new File(citiesFolder, "/projects");
                        final File[] projects = projectsFolder.listFiles();

                        if (projects != null) {
                            for (File project : projects){

                                String id = project.getName().replace(".yml", "");
                                String cityName = city.getName();
                                Country countryObject = plugin.getCountryManager().get(country.getName());

                                registry.put(id, new Pair<>(countryObject, cityName));

                            }
                        }

                    }
                }

            }
        }

    }

    public void add(@NotNull Project project) {
        registry.put(project.getId(), new Pair<>(project.getCountry(), project.getCity().getName()));
    }

    public void remove(@NotNull String id) {
        registry.remove(id);
    }

    public Set<String> getIDs() {
        return registry.keySet();
    }

    public boolean exists(@NotNull String id) {
        return registry.containsKey(id);
    }

    public Project getFromId(String id) {

        Pair<Country, String> path = registry.get(id);

        return path.getKey().getCityRegistry().get(path.getValue()).getProjectsRegistry().get(id);

    }

}
