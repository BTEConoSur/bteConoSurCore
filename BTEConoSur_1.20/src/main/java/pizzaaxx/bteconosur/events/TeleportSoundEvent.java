package pizzaaxx.bteconosur.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class TeleportSoundEvent implements Listener {

    @EventHandler
    public void onTeleport(@NotNull PlayerTeleportEvent event) {
        event.getPlayer().playSound(event.getTo(), "minecraft:entity.enderman.teleport", 1, 1);
    }

}
