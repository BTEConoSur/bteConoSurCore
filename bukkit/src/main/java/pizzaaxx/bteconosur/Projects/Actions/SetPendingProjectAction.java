package pizzaaxx.bteconosur.Projects.Actions;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import javax.annotation.CheckReturnValue;
import java.io.IOException;
import java.sql.SQLException;

public class SetPendingProjectAction {

    private final BTEConoSur plugin;

    private final Project project;
    private final boolean pending;

    public SetPendingProjectAction(BTEConoSur plugin, Project project, boolean pending) {
        this.plugin = plugin;
        this.project = project;
        this.pending = pending;
    }

    public void execute() throws SQLException, IOException {

        plugin.getSqlManager().update(
                "projects",
                new SQLValuesSet(
                        new SQLValue(
                                "pending", pending
                        )
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", project.getId()
                        )
                )
        ).execute();

        project.update();

        if (pending) {
            project.getCountry().getLogsChannel().sendMessage(":lock: El proyecto `" + project.getId() + "` ha sido marcado como terminado.").queue();
        }
    }

}
