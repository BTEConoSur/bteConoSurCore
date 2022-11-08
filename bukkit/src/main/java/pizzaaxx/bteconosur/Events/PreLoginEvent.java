package pizzaaxx.bteconosur.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;

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
            ResultSet set = plugin.getDBConnection().createStatement().executeQuery("SELECT uuid, name FROM players WHERE uuid = unhex(replace('" + uuid.toString() + "','-',''))");
            if (set.next()) {
                if (!set.getString("name").equals(event.getName())) {
                    plugin.getDBConnection().prepareStatement("UPDATE players SET name = " + event.getName() + " WHERE uuid = unhex(replace('" + uuid + "','-',''))").executeUpdate();
                    plugin.log("Updated name of player with UUID §f" + uuid + "§7: §f" + set.getString("name") + "§7 > §f" + event.getName());
                }
            } else {
                plugin.getDBConnection().prepareStatement("INSERT INTO players(uuid, name) VALUES (unhex(replace('" + uuid + "','-','')), \"" + event.getName() + "\")").executeUpdate();
                plugin.log("Created database registry for player with UUID §f" + uuid);
            }
        } catch (SQLException e) {
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "Ha ocurrido un error con la base de datos.");
        }

    }
}
