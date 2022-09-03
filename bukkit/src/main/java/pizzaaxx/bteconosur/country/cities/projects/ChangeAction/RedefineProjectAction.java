package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.bukkit.Bukkit;
import pizzaaxx.bteconosur.HelpMethods.SatMapHelper;
import pizzaaxx.bteconosur.country.cities.projects.Exceptions.ProjectActionException;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.helper.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;


/**
 * Redefine the region and difficulty of the project this belongs to.
 */
public class RedefineProjectAction implements ProjectAction {

    private final Project project;
    private final List<BlockVector2D> points;
    private final Project.Difficulty difficulty;

    public RedefineProjectAction(Project project, List<BlockVector2D> points, Project.Difficulty difficulty) {
        this.project = project;
        this.points = points;
        this.difficulty = difficulty;
    }

    /**
     * Create an instance of this ProjectAction that does not change the project's difficutly.
     * @param project The project this action belongs to.
     * @param points The points of the new project's region.
     */
    public RedefineProjectAction(Project project, List<BlockVector2D> points) {
        this(project, points, project.difficulty);
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() throws ProjectActionException {

        project.difficulty = difficulty;
        RegionManager manager = project.getPlugin().getRegionsManager();

        ProtectedPolygonalRegion newRegion = new ProtectedPolygonalRegion("project_" + project.getId(), points, -100, 8000);

        if (!newRegion.getIntersectingRegions(project.getCountry().getRegions()).isEmpty()) {
            newRegion.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
            newRegion.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.MEMBERS);
            newRegion.setPriority(1);

            FlagRegistry registry = project.getPlugin().getWorldGuard().getFlagRegistry();
            newRegion.setFlag((StateFlag) registry.get("worldedit"), StateFlag.State.ALLOW);
            newRegion.setFlag(registry.get("worldedit").getRegionGroupFlag(), RegionGroup.MEMBERS);

            if (!project.pending) {
                DefaultDomain domain = new DefaultDomain();
                for (UUID uuid : project.members) {
                    domain.addPlayer(uuid);
                }
                newRegion.setMembers(domain);
            }

            manager.addRegion(newRegion);
            project.region = manager.getRegion("project_" + project.getId());
            project.updatePlayersScoreboard();

            try {
                BufferedImage map = ImageIO.read(new URL(SatMapHelper.getURL(new Pair<>(project.getPoints(), "6382DC50"))));
                File output = new File(project.getRegistry().getImagesFolder(), project.getId() + ".png");
                ImageIO.write(map, "png", output);
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage("Ha ocurrido un error al cargar el mapa del proyecto " + project.getId().toUpperCase() + ".");
            }

        } else {
            throw new ProjectActionException(ProjectActionException.Type.NewRegionOutsideCountry);
        }

    }
}
