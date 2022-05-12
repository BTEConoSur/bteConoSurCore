package pizzaaxx.bteconosur.worldedit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;

public class ShortCuts implements Listener {

    private final PlayerRegistry playerRegistry;

    public ShortCuts(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        int increment = playerRegistry.get(player.getUniqueId()).getDataManager().getInt("increment", 1);

        if (event.getItem() != null && event.getItem().getType() == Material.WOOD_AXE) {
            if (event.getAction() == Action.LEFT_CLICK_AIR) {

                if (player.isSneaking()) {
                    player.performCommand("/shift " + increment);
                    return;
                }

                player.performCommand("/expand " + increment);
                return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                player.performCommand("/contract " + increment);
            }


        }
    }
}
