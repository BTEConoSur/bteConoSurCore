package pizzaaxx.bteconosur.Events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PreLoginEvent implements Listener {

    private final BTEConoSur plugin;

    public PreLoginEvent(BTEConoSur plugin) {
        this.plugin = plugin;
    }


    // THIS HAS TO BE ASYNC BECAUSE IF NOT DATA WON'T BE AVAILABLE ON LOGIN
    @EventHandler
    public void onPreLogin(@NotNull PlayerPreLoginEvent event) {

        UUID uuid = event.getUniqueId();
        try {
            ResultSet set = plugin.getSqlManager().select(
                    "players",
                    new SQLColumnSet(
                            "name"
                    ),
                    new SQLConditionSet(
                            new SQLOperatorCondition("uuid", "=", event.getUniqueId())
                    )
            ).retrieve();
            if (set.next()) {
                if (!set.getString("name").equals(event.getName())) {
                    plugin.getSqlManager().update(
                            "players",
                            new SQLValuesSet(
                                    new SQLValue("name", event.getName())
                            ),
                            new SQLConditionSet(
                                    new SQLOperatorCondition("uuid", "=", event.getUniqueId())
                            )
                    ).execute();
                    plugin.log("Updated name of player with UUID §f" + uuid + "§7: §f" + set.getString("name") + "§7 > §f" + event.getName());
                }
            } else {
                plugin.getSqlManager().insert(
                        "players",
                        new SQLValuesSet(
                                new SQLValue("uuid", event.getUniqueId()),
                                new SQLValue("name", event.getName())
                        )
                ).execute();
                plugin.log("Created database registry for player with UUID §f" + uuid);
            }
        } catch (SQLException e) {
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "Ha ocurrido un error con la base de datos.");
        }

    }
}
