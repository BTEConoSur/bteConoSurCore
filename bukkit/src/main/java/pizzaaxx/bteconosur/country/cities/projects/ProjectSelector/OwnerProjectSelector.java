package pizzaaxx.bteconosur.country.cities.projects.ProjectSelector;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.*;

public class OwnerProjectSelector implements IProjectSelector {

    private final UUID target;
    private final BteConoSur plugin;
    private final boolean exclusive;

    public OwnerProjectSelector(UUID target, boolean exclusive, BteConoSur plugin) {
        this.target = target;
        this.exclusive = exclusive;
        this.plugin = plugin;
    }

    @Override
    public Project select(@NotNull Collection<Project> projects) throws NoProjectsFoundException, NotInsideProjectException {

        if (!projects.isEmpty()) {
            Set<Project> ownedProjects = new HashSet<>();

            for (Project project : projects) {

                if (project.getOwner() == target) {
                    ownedProjects.add(project);
                }

            }

            if (ownedProjects.isEmpty()) {
                if (!exclusive) {
                    return new SmallestProjectSelector(plugin).select(projects);
                }
                throw new NoProjectsFoundException();
            }

            return new SmallestProjectSelector(plugin).select(ownedProjects);
        }
        throw new NotInsideProjectException();

    }

}
