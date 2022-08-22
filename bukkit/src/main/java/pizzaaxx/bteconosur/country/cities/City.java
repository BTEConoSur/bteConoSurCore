package pizzaaxx.bteconosur.country.cities;

import com.sk89q.worldedit.BlockVector2D;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.cities.projects.ProjectsRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class City {

    private final Country country;
    private final Configuration config;
    private final BteConoSur plugin;
    private final ProjectsRegistry registry;
    private final String name;
    private final File folder;
    private String displayName;
    private List<BlockVector2D> urbanArea;
    private final List<String> showcaseIDs = new ArrayList<>();
    private final List<List<BlockVector2D>> finishedRegions = new ArrayList<>();

    public City(@NotNull Country country, @NotNull String name, @NotNull BteConoSur plugin) {

        this.country = country;
        this.config = new Configuration(plugin, "countries/" + country.getName() + "/cities/" + name + "/config");
        this.plugin = plugin;

        this.registry = new ProjectsRegistry(this, plugin);
        this.name = name;
        this.folder = new File(plugin.getDataFolder(), "countries/" + country.getName() + "/cities/" + name);

        if (config.contains("displayName")) {
            this.displayName = config.getString("displayName");
        }

        if (config.contains("urbanArea")) {

            List<Map<String, Integer>> points = (List<Map<String, Integer>>) config.getList("urbanArea");
            List<BlockVector2D> regionPoints = new ArrayList<>();

            for (Map<String, Integer> point : points) {

                regionPoints.add(new BlockVector2D(point.get("x"), point.get("y")));

            }

            urbanArea =  regionPoints;

        }

        if (config.contains("showcaseIDs")) {
            showcaseIDs.addAll(config.getStringList("showcaseIDs"));
        }

        if (config.contains("finishedRegions")) {

            ConfigurationSection section = config.getConfigurationSection("finishedRegions");

            for (String key : section.getKeys(false)) {

                List<Map<String, Integer>> points = (List<Map<String, Integer>>) section.getList(key);
                List<BlockVector2D> regionPoints = new ArrayList<>();

                for (Map<String, Integer> point : points) {

                    regionPoints.add(new BlockVector2D(point.get("x"), point.get("y")));

                }

                finishedRegions.add(regionPoints);

            }

        }

    }

    public String getName() {
        return name;
    }

    public File getFolder() {
        return folder;
    }

    public Country getCountry() {
        return country;
    }

    public ProjectsRegistry getProjectsRegistry() {
        return registry;
    }

    public Configuration getConfig() {
        return config;
    }

    public BteConoSur getPlugin() {
        return plugin;
    }

    public List<BlockVector2D> getUrbanArea() {
        return urbanArea;
    }

    public List<List<BlockVector2D>> getFinishedRegions() {
        return finishedRegions;
    }

    public List<String> getShowcaseIDs() {
        return showcaseIDs;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setUrbanArea(List<BlockVector2D> urbanArea) {
        this.urbanArea = urbanArea;
    }

    public void addShowcaseID(String id) {
        showcaseIDs.add(id);
    }

    public void addFinishedRegion(List<BlockVector2D> points) {
        finishedRegions.add(points);
    }

    public int getProjectAmount() {
        return registry.getIds().size();
    }

    public int getFinishedProjectsAmount() {
        return finishedRegions.size();
    }

    public boolean isDefault() {
        return name.equals("default");
    }

}
