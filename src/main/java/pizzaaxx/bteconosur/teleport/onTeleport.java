package pizzaaxx.bteconosur.teleport;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class onTeleport implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        p.playSound(e.getTo(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
    }
}
