package pizzaaxx.bteconosur.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;

import static pizzaaxx.bteconosur.utilities.BackCommand.BACK_LOCATIONS;

public class JoinEvent implements Listener {

    private final BTEConoSurPlugin plugin;

    public JoinEvent(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        OnlineServerPlayer serverPlayer = (OnlineServerPlayer) plugin.getPlayerRegistry().get(event.getPlayer().getUniqueId());
        serverPlayer.getScoreboardManager().startBoard();
        plugin.getPlayerClickEvent().registerProtector(event.getPlayer().getUniqueId());
        BACK_LOCATIONS.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
    }

}
