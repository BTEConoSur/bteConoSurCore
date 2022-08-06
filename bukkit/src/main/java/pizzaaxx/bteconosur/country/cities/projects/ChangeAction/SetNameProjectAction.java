package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import pizzaaxx.bteconosur.country.cities.projects.Exceptions.ProjectActionException;
import pizzaaxx.bteconosur.country.cities.projects.Project;

public class SetNameProjectAction implements ProjectAction {

    private final Project project;
    private final String name;

    public SetNameProjectAction(Project project, String name) {
        this.project = project;
        this.name = name;

    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() throws ProjectActionException {

        if (name.matches("[a-zA-Z\\s_-]{1,32}")) {
            project.name = name;
            project.updatePlayersScoreboard();
        } else {
            throw new ProjectActionException(ProjectActionException.Type.InvalidName);
        }

    }
}
