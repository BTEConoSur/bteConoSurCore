package pizzaaxx.bteconosur.Projects.Actions;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

public class EmptyProjectAction {

    private final BTEConoSur plugin;
    private final Project project;

    public EmptyProjectAction(BTEConoSur plugin, Project project) {
        this.plugin = plugin;
        this.project = project;
    }

    public void execute() throws SQLException, IOException {

        plugin.getSqlManager().update(
                "projects",
                new SQLValuesSet(
                        new SQLValue(
                                "owner", null
                        ),
                        new SQLValue(
                                "members", new HashSet<>()
                        ),
                        new SQLValue(
                                "pending", false
                        ),
                        new SQLValue(
                                "showcase_ids", new HashSet<>()
                        ),
                        new SQLValue(
                                "name", null
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", project.getId()
                        )
                )
        ).execute();

        project.update();

        project.getCountry().getLogsChannel().sendMessage(":book: El proyecto `" + project.getId() + "` está disponible de nuevo.").queue();
    }
}
