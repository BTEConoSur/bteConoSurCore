package pizzaaxx.bteconosur.cities;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.worldguard.WorldGuardProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class City {

    private final String name;

    private final OldCountry country;

    private final ProtectedPolygonalRegion region;

    private ProtectedPolygonalRegion urbanArea;

    private String displayName;

    private final List<String> showcaseIDs = new ArrayList<>();

    private final List<List<BlockVector2D>> finishedRegions = new ArrayList<>();

    public City(@NotNull CityRegistry registry, String name) {

        this.country = registry.getCountry();
        this.name = name;

        if (registry.isLoaded(name)) {

            City origin = registry.get(name);

            this.region = origin.region;

            if (origin.urbanArea != null) {
                this.urbanArea = origin.urbanArea;
            }

            if (origin.displayName != null) {
                this.displayName = origin.displayName;
            }

            this.showcaseIDs.addAll(origin.showcaseIDs);

            this.finishedRegions.addAll(origin.finishedRegions);

        } else {

            Configuration config = new Configuration(registry.getPlugin(), "cities/" + country.getName() + "/" + name);

            RegionManager manager = WorldGuardProvider.getWorldGuard().getRegionManager(mainWorld);

            this.region = (ProtectedPolygonalRegion) manager.getRegion("city_" + this.country.getName() + "_" + name);

            if (config.contains("urbanArea")) {

                List<Map<String, Integer>> pointsList = (List<Map<String, Integer>>) config.getList("urbanArea");

                List<BlockVector2D> points = new ArrayList<>();

                for (Map<String, Integer> point : pointsList) {

                    int x = point.get("x");
                    int z = point.get("z");

                    points.add(new BlockVector2D(x, z));

                }

                this.urbanArea = new ProtectedPolygonalRegion("urban", points, -100, 8000);
            }

            if (config.contains("displayName")) {
                this.displayName = config.getString("displayName");
            }

            if (config.contains("showcaseIDs")) {
                this.showcaseIDs.addAll(config.getStringList("showcaseIDs"));
            }

            if (config.contains("finishedRegions")) {

                ConfigurationSection section = config.getConfigurationSection("finishedRegions");

                for (String key : section.getKeys(false)) {

                    List<BlockVector2D> points = new ArrayList<>();

                    List<Map<String, Integer>> pointsList = (List<Map<String, Integer>>) section.getList(key);

                    for (Map<String, Integer> point : pointsList) {

                        int x = point.get("x");
                        int z = point.get("z");

                        points.add(new BlockVector2D(x, z));

                    }

                    finishedRegions.add(points);

                }

            }

            registry.register(this);

        }

    }

    public String getName() {
        return name;
    }
}
