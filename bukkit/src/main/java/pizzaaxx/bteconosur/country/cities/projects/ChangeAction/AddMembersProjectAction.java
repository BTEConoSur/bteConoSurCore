package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.NewProject;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Add a set of {@link UUID}s as members to the project this action belongs to. The projects on the player's side will also be updated with this.
 */
public class AddMembersProjectAction implements ProjectAction {

    private final NewProject project;
    private final Set<UUID> members = new HashSet<>();

    public AddMembersProjectAction(@NotNull NewProject project, UUID... members) {
        this.project = project;
        this.members.addAll(Arrays.asList(members));
    }

    @Override
    public NewProject getProject() {
        return project;
    }

    @Override
    public void exec() {

        project.members.addAll(members);
        BteConoSur plugin = project.getPlugin();
        PlayerRegistry registry = plugin.getPlayerRegistry();
        DefaultDomain domain = new DefaultDomain();
        for (UUID uuid : members) {
            domain.addPlayer(uuid);
            registry.get(uuid).getProjectsManager().addProject(project);
        }
        ProtectedRegion region = plugin.getRegionsManager().getRegion("project_" + project.getId());
        if (region != null) {
            region.setMembers(domain);
        }
        project.saveToDisk();

    }
}
