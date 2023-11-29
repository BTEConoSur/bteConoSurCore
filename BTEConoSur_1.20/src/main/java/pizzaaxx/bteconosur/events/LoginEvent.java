package pizzaaxx.bteconosur.events;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.sql.SQLException;

public class LoginEvent implements Listener {

    private final BTEConoSurPlugin plugin;

    public LoginEvent(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    // REPLACES THE CURRENT SERVERPLAYER OBJECT IN REGISTRY WITH AN ONLINE VERSION OF ITSELF.
    // THE WAY TO CHECK IF A PLAYER IS ONLINE IS CHECKING IF THE OFFLINESERVERPLAYER OBJECT IS
    // AN INSTANCE OF AN ONLINESERVERPLAYER.
    @EventHandler
    public void login(@NotNull PlayerLoginEvent event) {
        try {
            plugin.getPlayerRegistry().login(event.getPlayer().getUniqueId());
        } catch (SQLException e) {
            event.disallow(
                    PlayerLoginEvent.Result.KICK_OTHER,
                    Component.text("Ha ocurrido un error en la base de datos.")
            );
        }
    }
}
