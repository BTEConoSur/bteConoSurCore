package pizzaaxx.bteconosur.Projects.Actions;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Projects.Project;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class ClaimProjectAction {

    private final BTEConoSur plugin;
    private final Project project;
    private final UUID owner;

    public ClaimProjectAction(BTEConoSur plugin, Project project, UUID owner) {
        this.plugin = plugin;
        this.project = project;
        this.owner = owner;
    }

    public void execute() throws SQLException, IOException {
        new SetOwnerProjectAction(plugin, project, owner).execute();

        project.getCountry().getLogsChannel().sendMessage(":inbox_tray: **" + plugin.getPlayerRegistry().get(owner).getName() + "** ha reclamado el proyecto `" + project.getId() + "`.").queue();
    }
}
