package pizzaaxx.bteconosur.Projects.Actions;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class DeleteProjectAction {

    private final BTEConoSur plugin;
    private final Project project;
    private final UUID moderatorUUID;


    public DeleteProjectAction(BTEConoSur plugin, Project project, UUID moderatorUUID) {
        this.plugin = plugin;
        this.project = project;
        this.moderatorUUID = moderatorUUID;
    }

    public void exec() throws SQLException, IOException {

        plugin.getRegionManager().removeRegion("project_" + project.getId());

        for (UUID memberUUID : project.getAllMembers()) {
            ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(memberUUID);
            serverPlayer.sendNotification(
                    project.getPrefix() + "Tu proyecto §a" + project.getDisplayName() + "§f ha sido eliminado.",
                    "**[PROYECTOS]** » Tu proyecto **" + project.getDisplayName() + "** ha sido eliminado."
            );
            serverPlayer.getProjectManager().removeProject(project);
        }

        plugin.getTerramapHandler().deletePolygon(project.getId());

        plugin.getSqlManager().delete(
                "projects",
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", project.getId()
                        )
                )
        ).execute();

        // TODO ADD EMBED
        project.getCountry().getLogsChannel().sendMessage(
                ":wastebasket: **" + plugin.getPlayerRegistry().get(moderatorUUID).getName() + "** ha eliminado el proyecto `" + project.getId() + "`."
        ).queue();

    }
}
