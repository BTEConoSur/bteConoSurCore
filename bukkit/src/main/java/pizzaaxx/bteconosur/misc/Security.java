package pizzaaxx.bteconosur.misc;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class Security implements Listener {

    private final List<Material> bannedItemsUsage = Arrays.asList(
            Material.FLINT_AND_STEEL,
            Material.WATER_BUCKET,
            Material.LAVA_BUCKET,
            Material.FIRE,
            Material.MONSTER_EGG
    );

    @EventHandler
    public void onPortalCreate(@NotNull PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem() != null && bannedItemsUsage.contains(event.getItem().getType())) {
            event.setCancelled(true);
        }
    }

}
