package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import pizzaaxx.bteconosur.country.cities.projects.NewProject;

/**
 * Represents an action to be called on a {@link NewProject}.
 */
public interface ProjectAction {

    /**
     *
     * @return The {@link NewProject} this action belongs to.
     */
    NewProject getProject();

    /**
     * Execute this action. Each Class handles this differently.
     */
    void exec();

}
