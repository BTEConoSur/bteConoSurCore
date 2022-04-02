package pizzaaxx.bteconosur.teleport;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OnTeleport implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        event.getPlayer().playSound(event.getTo(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
    }
}
