package pizzaaxx.bteconosur.country.cities.projects.ProjectSelector;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NonMemberProjectSelector implements IProjectSelector {

    private final UUID target;
    private final BteConoSur plugin;

    public NonMemberProjectSelector(UUID target, BteConoSur plugin) {
        this.target = target;
        this.plugin = plugin;
    }

    @Override
    public Project select(@NotNull Collection<Project> projects) throws NoProjectsFoundException, NotInsideProjectException {

        if (!projects.isEmpty()) {
            Set<Project> nonMemberProjects = new HashSet<>();

            for (Project project : projects) {

                if (!project.getAllMembers().contains(target)) {
                    nonMemberProjects.add(project);
                }

            }

            if (!nonMemberProjects.isEmpty()) {
                return new SmallestProjectSelector(plugin).select(nonMemberProjects);
            }
            throw new NoProjectsFoundException();

        }
        throw new NotInsideProjectException();

    }

}
