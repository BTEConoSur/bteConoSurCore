package pizzaaxx.bteconosur.Projects.Actions;

import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
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

        File file = new File(plugin.getDataFolder(), "projects/images/" + project.getId() + ".png");
        file.delete();

        ResultSet set = plugin.getSqlManager().select(
                "posts",
                new SQLColumnSet(
                        "channel_id"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "target_type", "=", "project"
                        ),
                        new SQLOperatorCondition(
                                "target_id", "=", project.getId()
                        )
                )
        ).retrieve();

        if (set.next()) {
            ThreadChannel channel = project.getCountry().getGuild().getThreadChannelById(set.getString("channel_id"));
            if (channel == null) {
                return;
            }
            channel.delete().queue();

            plugin.getSqlManager().delete(
                    "posts",
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "target_type", "=", "project"
                            ),
                            new SQLOperatorCondition(
                                    "target_id", "=", project.getId()
                            )
                    )
            ).execute();
        }

        project.getCountry().getLogsChannel().sendMessage(
                ":wastebasket: **" + plugin.getPlayerRegistry().get(moderatorUUID).getName() + "** ha eliminado el proyecto `" + project.getId() + "`."
        ).queue();

    }
}
