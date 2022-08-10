package pizzaaxx.bteconosur.country.cities.projects.ProjectSelector;

import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.Collection;

public interface IProjectSelector {

    Project select(Collection<Project> projects) throws IllegalArgumentException;

}
