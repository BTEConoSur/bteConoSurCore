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
    private final SelectionCommands selectionCommands;

    public ShortCuts(PlayerRegistry playerRegistry, SelectionCommands selectionCommands) {
        this.playerRegistry = playerRegistry;
        this.selectionCommands = selectionCommands;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        int increment = playerRegistry.get(player.getUniqueId()).getDataManager().getInt("increment", 1);

        if (event.getItem() != null && event.getItem().getType() == Material.WOOD_AXE) {
            if (event.getAction() == Action.LEFT_CLICK_AIR) {

                if (player.isSneaking()) {
                    selectionCommands.onShortcutBefore(player);
                    player.performCommand("/shift " + increment);
                    selectionCommands.onShortcutAfter(player);
                    return;
                }

                selectionCommands.onShortcutBefore(player);
                player.performCommand("/expand " + increment);
                selectionCommands.onShortcutAfter(player);
                return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {

                selectionCommands.onShortcutBefore(player);
                player.performCommand("/contract " + increment);
                selectionCommands.onShortcutAfter(player);
            }


        }
    }
}
