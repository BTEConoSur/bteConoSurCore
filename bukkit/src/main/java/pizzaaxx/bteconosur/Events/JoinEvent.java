package pizzaaxx.bteconosur.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;

public class JoinEvent implements Listener {

    private final BTEConoSur plugin;

    public JoinEvent(BTEConoSur bteConoSur) {
        this.plugin = bteConoSur;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        plugin.add(event.getPlayer().getUniqueId());
    }
}
