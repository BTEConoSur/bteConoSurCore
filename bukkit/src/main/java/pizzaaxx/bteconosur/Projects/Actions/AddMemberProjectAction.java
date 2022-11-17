package pizzaaxx.bteconosur.Projects.Actions;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public class AddMemberProjectAction {

    private final BTEConoSur plugin;

    private final Project project;
    private final UUID member;

    public AddMemberProjectAction(BTEConoSur plugin, Project project, UUID member) {
        this.plugin = plugin;
        this.project = project;
        this.member = member;
    }

    public void execute() throws SQLException {
        Set<UUID> members = project.getMembers();
        members.add(member);

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

        project.getCountry().getLogsChannel().sendMessage(":pencil: **" + plugin.getPlayerRegistry().get(project.getOwner()).getName() + "** ha agregado a **" + plugin.getPlayerRegistry().get(member).getName() + "** al proyecto `" + project.getId() + "`.").queue();
    }
}
