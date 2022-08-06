package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Exceptions.ProjectActionException;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Remove a set of {@link java.util.UUID} as members from the project this action belongs to. The projects on the player's side will also be updated with this.
 */
public class RemoveMembersProjectAction implements ProjectAction {

    private final Project project;
    private final UUID member;

    public RemoveMembersProjectAction(Project project, UUID member) {
        this.project = project;
        this.member = member;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() throws ProjectActionException {

        if (project.members.contains(member)) {
            BteConoSur plugin = project.getPlugin();
            PlayerRegistry registry = plugin.getPlayerRegistry();
            ProtectedRegion region = project.getRegion();
            DefaultDomain domain = new DefaultDomain(region.getMembers());
            domain.removePlayer(member);
            registry.get(member).getProjectsManager().removeProject(project);
            project.getRegion().setMembers(domain);
            project.members.remove(member);
            project.updatePlayersScoreboard();
        } else {
            throw new ProjectActionException(ProjectActionException.Type.PlayerNotMember);
        }

    }
}
