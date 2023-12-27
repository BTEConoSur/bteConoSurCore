package pizzaaxx.bteconosur.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

public class QuitEvent implements Listener {

    private final BTEConoSurPlugin plugin;

    public QuitEvent(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    // REPLACES THE CURRENT SERVERPLAYER OBJECT IN REGISTRY WITH AN OFFLINE VERSION OF ITSELF.
    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        plugin.getPlayerRegistry().quit(event.getPlayer().getUniqueId());
        plugin.getPlayerClickEvent().unregisterProtector(event.getPlayer().getUniqueId());
    }
}

