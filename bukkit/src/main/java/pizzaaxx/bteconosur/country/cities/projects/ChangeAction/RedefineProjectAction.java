package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.jetbrains.annotations.Contract;
import pizzaaxx.bteconosur.country.cities.projects.Exceptions.ProjectActionException;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;


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

        if (!newRegion.getIntersectingRegions(Collections.singletonList(project.getCountry().getRegion())).isEmpty()) {
            newRegion.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
            newRegion.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.MEMBERS);
            newRegion.setPriority(1);

            FlagRegistry registry = getWorldGuard().getFlagRegistry();
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
        } else {
            throw new ProjectActionException(ProjectActionException.Type.NewRegionOutsideCountry);
        }

    }
}
