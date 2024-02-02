package pizzaaxx.bteconosur.events;

import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.sql.SQLException;

public class PreLoginEvent implements Listener {

    private final BTEConoSurPlugin plugin;

    public PreLoginEvent(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {
        plugin.log("Attempt to join: " + event.getUniqueId());
        if (!plugin.getPlayerRegistry().exists(event.getUniqueId())) { // IF PLAYER HASN'T ENTERED BEFORE
            try {
                plugin.getSqlManager().insert(
                        "players",
                        new SQLValuesSet(
                                new SQLValue("uuid", event.getUniqueId()),
                                new SQLValue("name", event.getName()),
                                new SQLValue("last_disconnected", System.currentTimeMillis())
                        )
                ).execute();
                plugin.getPlayerRegistry().registerID(event.getUniqueId());
            } catch (SQLException e) {
                e.printStackTrace();
                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        Component.text("Ha ocurrido un error en la base de datos.")
                );
            }
        }
    }
}
