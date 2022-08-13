package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import com.sk89q.worldguard.domains.DefaultDomain;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Exceptions.ProjectActionException;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.ServerPlayer.PlayerRegistry;

import java.util.UUID;

/**
 * Add a set of {@link UUID}s as members to the project this action belongs to. The projects on the player's side will also be updated with this.
 */
public class AddMembersProjectAction implements ProjectAction {

    private final Project project;
    private final UUID member;

    public AddMembersProjectAction(@NotNull Project project, UUID member) {
        this.project = project;
        this.member = member;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() throws ProjectActionException {

        if (project.getMembers().size() < 14) {

            if (!project.members.contains(member)) {

                BteConoSur plugin = project.getPlugin();
                PlayerRegistry registry = plugin.getPlayerRegistry();
                DefaultDomain domain = new DefaultDomain();
                if (Bukkit.getOfflinePlayer(member).isOnline()) {
                    domain.addPlayer(member);
                    registry.get(member).getProjectsManager().addProject(project);

                    if (!project.pending) {
                        project.getRegion().setMembers(domain);
                    }

                    project.members.add(member);
                    project.updatePlayersScoreboard();
                } else {
                    throw new ProjectActionException(ProjectActionException.Type.PlayerNotOnline);
                }
            } else {
                throw new ProjectActionException(ProjectActionException.Type.PlayerAlreadyAMember);
            }
        } else {
            throw new ProjectActionException(ProjectActionException.Type.MemberLimitReached);
        }

    }
}
