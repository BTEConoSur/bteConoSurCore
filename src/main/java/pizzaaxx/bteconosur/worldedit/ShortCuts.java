package pizzaaxx.bteconosur.worldedit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pizzaaxx.bteconosur.PlayerRegistry;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.player.data.PlayerData;

import java.util.Objects;

public class ShortCuts implements Listener {

    private final PlayerRegistry playerRegistry;

    public ShortCuts(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ServerPlayer serverPlayer = playerRegistry.get(player.getUniqueId());
        PlayerData playerData = serverPlayer.getData();

        int increment = (int) playerData.getData("increment");

        if (event.getItem().getType() == Material.WOOD_AXE) {
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
