package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import pizzaaxx.bteconosur.country.cities.projects.Exceptions.ProjectActionException;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.UUID;

public class EmptyProjectAction implements ProjectAction {

    private final Project project;

    public EmptyProjectAction(Project project) {
        this.project = project;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() {

        for (UUID uuid : project.getAllMembers()) {
            project.getPlugin().getPlayerRegistry().get(uuid).getProjectsManager().removeProject(project);
        }
        project.pending = false;
        project.name = null;
        project.owner = null;
        project.members.clear();

    }
}
