package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import pizzaaxx.bteconosur.BteConoSur;
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
    private final Set<UUID> members = new HashSet<>();

    public RemoveMembersProjectAction(Project project, UUID... members) {
        this.project = project;
        this.members.addAll(Arrays.asList(members));
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() {

        BteConoSur plugin = project.getPlugin();
        PlayerRegistry registry = plugin.getPlayerRegistry();
        ProtectedRegion region = project.getRegion();
        DefaultDomain domain = new DefaultDomain(region.getMembers());
        for (UUID uuid : members) {
            domain.removePlayer(uuid);
            registry.get(uuid).getProjectsManager().removeProject(project);
        }
        project.getRegion().setMembers(domain);
        project.members.removeAll(members);
        project.saveToDisk();

    }
}
