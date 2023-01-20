package pizzaaxx.bteconosur.Projects.Actions;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public class RemoveMemberProjectAction {

    private final BTEConoSur plugin;
    private final Project project;
    private final UUID member;

    public RemoveMemberProjectAction(BTEConoSur plugin, Project project, UUID member) {
        this.plugin = plugin;
        this.project = project;
        this.member = member;
    }

    public void execute() throws SQLException, IOException {
        Set<UUID> members = project.getMembers();
        members.remove(member);

        plugin.getSqlManager().update(
                "projects",
                new SQLValuesSet(
                        new SQLValue(
                                "members", members
                        )
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", project.getId()
                        )
                )
        ).execute();

        project.update();

        project.getCountry().getLogsChannel().sendMessage(":pencil: **" + plugin.getPlayerRegistry().get(project.getOwner()).getName() + "** ha removido a **" + plugin.getPlayerRegistry().get(member).getName() + "** del proyecto `" + project.getId() + "`.").queue();
    }

}
