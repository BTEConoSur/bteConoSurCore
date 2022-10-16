package pizzaaxx.bteconosur.country.cities.projects;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.HelpMethods.SatMapHelper;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.ProjectTypes.ProjectType;
import pizzaaxx.bteconosur.country.cities.City;
import pizzaaxx.bteconosur.country.cities.projects.ChangeAction.UpdateScoreboardProjectAction;
import pizzaaxx.bteconosur.country.cities.projects.Exceptions.ProjectActionException;
import pizzaaxx.bteconosur.helper.Pair;
import pizzaaxx.bteconosur.methods.CodeGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ProjectsRegistry {

    private final City city;
    private final BteConoSur plugin;
    private final Map<String, Project> registry = new HashMap<>();

    private final Map<String, Long> deletionRegistry = new HashMap<>();

    private final Set<String> ids = new HashSet<>();
    private final File folder;
    private final File imagesFolder;

    public ProjectsRegistry(@NotNull City city, @NotNull BteConoSur plugin) {

        this.city = city;
        this.plugin = plugin;

        folder = new File(city.getFolder(), "/projects");
        imagesFolder = new File(city.getFolder(), "/projectsImages");
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
                register(new Project(this, id));
            }
            scheduleDeletion(id);
            return registry.get(id);
        }
        return null;
    }

    public File getFolder() {
        return folder;
    }

    public File getImagesFolder() {
        return imagesFolder;
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

    public Set<String> getIds() {
        return ids;
    }

    /**
     *
     * @param regionPoints The points of the region of the new project.
     * @return An instance of the new project.
     * @throws IOException If the server failed to create a configuration file.
     */
    public Project createProject(ProjectType type, int points, @NotNull List<BlockVector2D> regionPoints) throws IOException {

        String id = CodeGenerator.generateCode(6, plugin.getProjectsManager().getIDs());

        File projectFile = new File(folder, id + ".yml");
        if (projectFile.createNewFile()) {

            Configuration config = new Configuration(plugin, "countries/" + city.getCountry().getName() + "/cities/" + city.getName() + "/projects/" + id);
            config.set("type", type.getId());
            config.set("points", points);
            config.set("pending", false);
            config.save();

            ProtectedPolygonalRegion region = new ProtectedPolygonalRegion("project_" + id, regionPoints, -100, 8000);

            region.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
            region.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.MEMBERS);
            region.setPriority(1);

            FlagRegistry registry = plugin.getWorldGuard().getFlagRegistry();

            region.setFlag((StateFlag) registry.get("worldedit"), StateFlag.State.ALLOW);
            region.setFlag(registry.get("worldedit").getRegionGroupFlag(), RegionGroup.MEMBERS);

            plugin.getRegionsManager().addRegion(region);

            // WRITE MAP FILE
            BufferedImage map = ImageIO.read(new URL(SatMapHelper.getURL(new Pair<>(regionPoints, "6382DC50"))));
            File output = new File(imagesFolder, id + ".png");
            ImageIO.write(map, "png", output);

            // WRITE TERRAMAP FILES

            ids.add(id);

            plugin.getProjectsManager().add(this.get(id));

            this.get(id).updatePlayersScoreboard();

            return this.get(id);
        }
        throw new IOException();
    }

    public boolean deleteProject(@NotNull String id) {

        if (exists(id)) {
            File projectFile = new File(folder, id + ".yml");
            File imageFile = new File(imagesFolder, id + ".png");
            if (projectFile.exists()) {
                if (projectFile.delete() && imageFile.delete()) {
                    Project project = this.get(id);
                    UpdateScoreboardProjectAction action = new UpdateScoreboardProjectAction(project);
                    project.empty().exec();
                    this.unregister(id);
                    plugin.getRegionsManager().removeRegion("project_" + id);
                    ids.remove(id);
                    plugin.getProjectsManager().remove(id);
                    action.exec();
                    return true;
                }
            }
        }
        return false;
    }
}
