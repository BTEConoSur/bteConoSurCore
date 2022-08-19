package pizzaaxx.bteconosur.country.cities.projects.ProjectSelector;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MemberProjectSelector implements IProjectSelector {

    private final UUID target;
    private final BteConoSur plugin;
    private final boolean exclusive;

    public MemberProjectSelector(UUID target, boolean exclusive, BteConoSur plugin) {
        this.target = target;
        this.exclusive = exclusive;
        this.plugin = plugin;
    }

    @Override
    public Project select(@NotNull Collection<Project> projects) throws NoProjectsFoundException {

        if (projects.size() > 0) {
            Set<Project> memberProjects = new HashSet<>();

            for (Project project : projects) {

                if (project.getAllMembers().contains(target)) {
                    memberProjects.add(project);
                }

            }

            if (memberProjects.isEmpty() && !exclusive) {
                return new SmallestProjectSelector(plugin).select(projects);
            }

            return new SmallestProjectSelector(plugin).select(memberProjects);
        }
        throw new NoProjectsFoundException();

    }

}
