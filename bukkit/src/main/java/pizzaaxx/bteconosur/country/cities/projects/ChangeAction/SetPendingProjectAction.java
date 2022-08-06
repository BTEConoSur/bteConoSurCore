package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import com.sk89q.worldguard.domains.DefaultDomain;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.List;
import java.util.UUID;

public class SetPendingProjectAction implements ProjectAction {

    private final Project project;
    private final boolean pending;

    public SetPendingProjectAction(Project project, boolean pending) {
        this.project = project;
        this.pending = pending;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() {

        if (project.pending != pending) {
            project.pending = pending;

            Configuration pending = project.getCountry().getPending();

            List<String> pendingProjects = pending.getStringList("pending");

            DefaultDomain domain = new DefaultDomain();
            if (this.pending) {
                pendingProjects.add(project.getId());
            } else {
                pendingProjects.remove(project.getId());
                for (UUID uuid : project.members) {
                    domain.addPlayer(uuid);
                }
            }
            project.region.setMembers(new DefaultDomain());

            pending.set("pending", pendingProjects);
            pending.save();
        }

    }
}
