package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.List;

/**
 * Change the {@link pizzaaxx.bteconosur.country.cities.projects.Project.Tag} of the project this belongs to.
 */
public class SetTagProjectAction implements ProjectAction {

    private final Project project;
    private final Project.Tag oldTag;
    private final Project.Tag newTag;

    public SetTagProjectAction(Project project, Project.Tag oldTag, Project.Tag newTag) {

        this.project = project;
        this.newTag = newTag;
        this.oldTag = oldTag;

    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() {

        if (oldTag != newTag) {
            Configuration tags = project.getCountry().getTags();

            if (oldTag != null) {
                List<String> projectsOld = tags.getStringList(oldTag.toString());
                projectsOld.remove(project.getId());
                tags.set(oldTag.toString(), projectsOld);
            }

            if (newTag != null) {
                List<String> projectsNew = tags.getStringList(newTag.toString());
                projectsNew.add(project.getId());
                tags.set(newTag.toString(), projectsNew);
            }

            tags.save();
            project.tag = newTag;
            project.updatePlayersScoreboard();
        }
    }
}
