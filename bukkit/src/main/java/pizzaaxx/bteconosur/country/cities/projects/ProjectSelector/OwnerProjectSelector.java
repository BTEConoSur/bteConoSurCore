package pizzaaxx.bteconosur.country.cities.projects.ProjectSelector;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.*;

public class OwnerProjectSelector implements IProjectSelector {

    private final UUID target;
    private final BteConoSur plugin;

    public OwnerProjectSelector(UUID target, BteConoSur plugin) {
        this.target = target;
        this.plugin = plugin;
    }

    @Override
    public Project select(@NotNull Collection<Project> projects) throws IllegalArgumentException {

        if (projects.size() > 0) {
            Set<Project> ownedProjects = new HashSet<>();

            for (Project project : projects) {

                if (project.getOwner() == target) {
                    ownedProjects.add(project);
                }

            }

            return new SmallestProjectSelector(plugin).select(ownedProjects);
        }
        throw new IllegalArgumentException();

    }

}
