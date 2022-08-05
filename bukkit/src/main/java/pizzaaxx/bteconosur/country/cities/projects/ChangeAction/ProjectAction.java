package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import pizzaaxx.bteconosur.country.cities.projects.Project;

/**
 * Represents an action to be called on a {@link Project}.
 */
public interface ProjectAction {

    /**
     *
     * @return The {@link Project} this action belongs to.
     */
    Project getProject();

    /**
     * Execute this action. Each Class handles this differently.
     */
    void exec();

}
