package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Exceptions.ProjectActionException;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.UUID;

public class ClaimProjectAction implements ProjectAction {

    private final BteConoSur plugin;
    private final Project project;
    private final UUID target;

    public ClaimProjectAction(Project project, UUID target, BteConoSur plugin) {
        this.plugin = plugin;
        this.project = project;
        this.target = target;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() throws ProjectActionException {

        if (!project.isClaimed()) {
            project.owner = target;
            plugin.getPlayerRegistry().get(target).getProjectsManager().addProject(project);
        } else {
            throw new ProjectActionException(ProjectActionException.Type.ProjectAlreadyClaimed);
        }

    }
}
