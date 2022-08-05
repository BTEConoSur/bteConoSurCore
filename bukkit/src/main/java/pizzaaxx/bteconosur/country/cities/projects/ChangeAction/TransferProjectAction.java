package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.UUID;

/**
 * Transfer the project this action belongs to, to a specified player.
 */
public class TransferProjectAction implements ProjectAction {

    private final Project project;
    private final UUID from;
    private final UUID target;

    public TransferProjectAction(Project project, UUID from, UUID target) {

        this.from = from;
        this.project = project;
        this.target = target;

    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() {

        project.members.remove(target);
        project.members.add(from);
        project.owner = target;
        project.saveToDisk();

    }
}
