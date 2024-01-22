package pizzaaxx.bteconosur.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.utilities.BackCommand;

public class TeleportEvent implements Listener {
    @EventHandler
    public void onTeleport(@NotNull PlayerTeleportEvent event) {
        BackCommand.BACK_LOCATIONS.put(event.getPlayer().getUniqueId(), event.getFrom());
        event.getPlayer().playSound(event.getTo(), "minecraft:entity.enderman.teleport", 1, 1);
    }

}
