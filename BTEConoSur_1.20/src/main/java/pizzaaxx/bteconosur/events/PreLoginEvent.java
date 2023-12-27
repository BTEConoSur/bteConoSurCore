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
        if (!plugin.getPlayerRegistry().exists(event.getUniqueId())) { // IF PLAYER HASN'T ENTERED BEFORE
            try {
                plugin.getSqlManager().insert(
                        "players",
                        new SQLValuesSet(
                                new SQLValue("uuid", event.getUniqueId()),
                                new SQLValue("name", event.getName())
                        )
                ).execute();
                plugin.getPlayerRegistry().registerID(event.getUniqueId());
            } catch (SQLException e) {
                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        Component.text("Ha ocurrido un error en la base de datos.")
                );
            }
        }
    }
}
