package pizzaaxx.bteconosur.projects;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import org.bukkit.Bukkit;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;

import java.sql.SQLException;
import java.util.UUID;

public class ProjectEditor {

    private final BTEConoSurPlugin plugin;
    private final Project project;

    public ProjectEditor(BTEConoSurPlugin plugin, Project project) {
        this.plugin = plugin;
        this.project = project;
    }

    public void setName(String name) throws SQLException {
        project.name = name;
        this.saveValue("name", name);
        this.updateScoreboards();
    }

    public void claim(UUID uuid) throws SQLException {
        if (project.owner != null) {
            return;
        }

        project.owner = uuid;
        OfflineServerPlayer owner = plugin.getPlayerRegistry().get(uuid);
        owner.getProjectsManager().addProject(project);

        this.saveValue("owner", uuid);

        project.getCountry().getLogs().sendMessage(
                ":inbox_tray: El jugador **" + owner.getName() + "** ha reclamado el proyecto `" + project.getID() + "`."
        ).queue();

        this.updateScoreboards();
    }

    private void saveValue(String key, Object value) throws SQLException {
        plugin.getSqlManager().update(
                "projects",
                new SQLValuesSet(
                        new SQLValue(key, value)
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("id", "=", project.getID())
                )
        ).execute();
    }

    private void updateScoreboards() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> project.polygon.contains(
                        new GeometryFactory().createPoint(
                                new Coordinate(
                                        player.getLocation().getX(),
                                        player.getLocation().getZ()
                                )
                        )
                )).forEach(
                        player -> {
                            try {
                                OnlineServerPlayer onlinePlayer = (OnlineServerPlayer) plugin.getPlayerRegistry().get(player.getUniqueId());
                                onlinePlayer.getScoreboardManager().setDisplay(project);
                            } catch (SQLException ignored) {}
                        }
                );
    }

}
