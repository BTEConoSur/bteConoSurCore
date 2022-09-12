package pizzaaxx.bteconosur.country.cities.projects;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.IProjectSelector;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.NoProjectsFoundException;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.NotInsideProjectException;
import pizzaaxx.bteconosur.helper.Pair;

import java.io.File;
import java.util.*;

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

    public Set<Project> getProjectsAt(Location location) {

        Set<Project> projects = new HashSet<>();
        for (ProtectedRegion region : plugin.getRegionsManager().getApplicableRegions(location).getRegions()) {

            if (region.getId().startsWith("project_")) {

                projects.add(this.getFromId(region.getId().replace("project_", "")));

            }

        }

        return projects;

    }

    public Set<Project> getProjectsAt(@NotNull BlockVector2D vector) {
        Location loc = new Location(plugin.getWorld(), vector.getX(), 100, vector.getZ());
        return this.getProjectsAt(loc);
    }

    public Set<Project> getProjectsAt(@NotNull Coords2D coords) {
        Location loc = new Location(plugin.getWorld(), coords.getX(), 100, coords.getZ());
        return this.getProjectsAt(loc);
    }

    public Project getProjectAt(Location location, @NotNull IProjectSelector selector) throws NoProjectsFoundException, NotInsideProjectException {
        return selector.select(getProjectsAt(location));
    }

    public Pair<Country, String> getPathFromId(@NotNull String id) {
        return registry.get(id);
    }

    // TODO MOST PROBABLY DOESN'T WORK
    public Set<ProtectedRegion> getProjectsIntersecting(ProtectedRegion region) {

        Set<ProtectedRegion> projects = new HashSet<>();

        for (Country country : plugin.getCountryManager().getAllCountries()) {

            if (!region.getIntersectingRegions(country.getRegions()).isEmpty()) { // <- Is inside country

                Set<ProtectedRegion> cityRegions = new HashSet<>();

                for (String name : country.getCityRegistry().getNames()) {
                    cityRegions.add(plugin.getRegionsManager().getRegion("city_" + country.getName() + "_" + name));
                }

                for (ProtectedRegion cityRegion : region.getIntersectingRegions(cityRegions)) { // <- Cities the regions is in

                    File projectsFolder = new File(country.getFolder(), "cities/" + cityRegion.getId().split("_")[2] + "/projects");
                    File[] projectsFiles = projectsFolder.listFiles();

                    if (projectsFiles != null) {

                        Set<ProtectedRegion> projectRegions = new HashSet<>();

                        for (File project : projectsFiles) {
                            String id = project.getName().replace(".yml", "");
                            projectRegions.add(plugin.getRegionsManager().getRegion("project_" + id));
                        }

                        projects.addAll(region.getIntersectingRegions(projectRegions));
                    }

                }

            }

        }

        return projects;

    }

    public boolean isProjectAt(Location loc) {

        for (ProtectedRegion region : plugin.getWorldGuard().getRegionManager(plugin.getWorld()).getApplicableRegions(loc)) {
            if (region.getId().startsWith("project_")) {
                if (exists(region.getId().replace("project_", ""))) {
                    return true;
                }
            }
        }

        return false;
    }

}
