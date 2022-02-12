package pizzaaxx.bteconosur.worldedit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pizzaaxx.bteconosur.player.data.PlayerData;

import java.util.Objects;

public class ShortCuts implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        int d = (Integer) new PlayerData(p).getData("increment");
        if (Objects.equals(e.getItem(), new ItemStack(Material.WOOD_AXE))) {
            if (e.getAction() == Action.LEFT_CLICK_AIR) {
                if (p.isSneaking()) {
                    p.performCommand("/shift " + d);
                } else {
                    p.performCommand("/expand " + d);
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.performCommand("/contract " + d);
            }
        }
    }
}
