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

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class ProjectEditor {

    // :pencil: for edits
    // :inbox_tray: for claims
    // :incoming_envelope: for transfers
    // :waste_basket: for deletion

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
                ":pencil: El jugador **" + owner.getName() + "** ha reclamado el proyecto `" + project.getID() + "`."
        ).queue();

        this.updateScoreboards();
    }

    public void removeMember(UUID uuid) throws SQLException {
        project.members.remove(uuid);
        this.saveValue("members", project.members);
        OfflineServerPlayer member = plugin.getPlayerRegistry().get(uuid);
        member.getProjectsManager().removeProject(project);

        project.getCountry().getLogs().sendMessage(
                ":pencil: El jugador **" + member.getName() + "** ha sido removido del proyecto `" + project.getID() + "`."
        ).queue();

        try {
            member.sendNotification(
                    PREFIX + "Has sido removido del proyecto §a" + project.getID() + "§f.",
                    ":bell: Has sido removido del proyecto `" + project.getID() + "`."
            );
        } catch (SQLException e) {
            plugin.log("Error sending notification to player " + member.getName() + ".");
        }

        this.updateScoreboards();
    }

    public void addMember(UUID uuid) throws SQLException {
        project.members.add(uuid);
        this.saveValue("members", project.members);
        OfflineServerPlayer member = plugin.getPlayerRegistry().get(uuid);
        member.getProjectsManager().addProject(project);

        project.getCountry().getLogs().sendMessage(
                ":pencil: El jugador **" + member.getName() + "** ha sido añadido al proyecto `" + project.getID() + "`."
        ).queue();

        try {
            member.sendNotification(
                    PREFIX + "Has sido añadido al proyecto §a" + project.getID() + "§f.",
                    ":bell: Has sido añadido al proyecto `" + project.getID() + "`."
            );
        } catch (SQLException e) {
            plugin.log("Error sending notification to player " + member.getName() + ".");
        }

        this.updateScoreboards();
    }

    public void transfer(UUID target) throws SQLException {
        UUID currentOwner = project.getOwner();
        project.members.add(currentOwner);
        project.members.remove(target);
        project.owner = target;

        saveValue("owner", target);
        saveValue("members", project.members);

        project.getCountry().getLogs().sendMessage(
                ":incoming_envelope: **" + plugin.getPlayerRegistry().get(currentOwner).getName() + "** ha transferido el proyecto `" + project.getID() + "` al jugador **" + plugin.getPlayerRegistry().get(target).getName() + "**."
        ).queue();
        this.updateScoreboards();
    }

    public void finish() throws SQLException {
        this.setPending(true);
        project.getCountry().getLogs().sendMessage(
                ":lock: El proyecto `" + project.getID() + "` ha sido marcado como finalizado."
        ).queue();
    }

    private void setPending(boolean pending) throws SQLException {
        if (pending) {
            project.pending = System.currentTimeMillis();
        } else {
            project.pending = 0;
        }
        saveValue("pending", project.pending);
        this.updateScoreboards();
    }

    public void memberLeave(UUID uuid) throws SQLException {
        project.members.remove(uuid);
        saveValue("members", project.members);
        // notify owner
        OfflineServerPlayer owner = plugin.getPlayerRegistry().get(project.owner);
        try {
            owner.sendNotification(
                    PREFIX + "El jugador §a" + plugin.getPlayerRegistry().get(uuid).getName() + "§f ha abandonado el proyecto §a" + project.getID() + "§f.",
                    ":bell: El jugador `" + plugin.getPlayerRegistry().get(uuid).getName() + "` ha abandonado el proyecto `" + project.getID() + "`."
            );
        } catch (SQLException e) {
            plugin.log("Error sending notification to player " + owner.getName() + ".");
        }
        // log
        project.getCountry().getLogs().sendMessage(
                ":pencil: El jugador **" + plugin.getPlayerRegistry().get(uuid).getName() + "** ha abandonado el proyecto `" + project.getID() + "`."
        ).queue();
        this.updateScoreboards();
    }

    public void ownerLeave() throws SQLException {
        // notify members
        project.members.forEach(
                uuid -> {
                    OfflineServerPlayer member = plugin.getPlayerRegistry().get(uuid);
                    try {
                        member.sendNotification(
                                PREFIX + "El dueño del proyecto §a" + project.getID() + "§f ha abandonado el proyecto.",
                                ":bell: El dueño del proyecto `" + project.getID() + "` ha abandonado el proyecto."
                        );
                    } catch (SQLException e) {
                        plugin.log("Error sending notification to player " + member.getName() + ".");
                    }
                }
        );
        // log
        project.getCountry().getLogs().sendMessage(
                ":pencil: El dueño del proyecto `" + project.getID() + "` ha abandonado el proyecto."
        ).queue();
        // clear project
        this.clearProject();
        // update scoreboards
        this.updateScoreboards();
    }

    private void clearProject() throws SQLException {
        project.members.clear();
        project.owner = null;
        project.name = null;
        project.pending = 0;
        plugin.getSqlManager().update(
                "projects",
                new SQLValuesSet(
                        new SQLValue("name", null),
                        new SQLValue("owner", null),
                        new SQLValue("members", project.members),
                        new SQLValue("pending", 0)
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("id", "=", project.getID())
                )
        ).execute();
        // log Esta disponible de nuevo
        project.getCountry().getLogs().sendMessage(
                ":flag_white: El proyecto `" + project.getID() + "` está disponible de nuevo."
        ).queue();
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
