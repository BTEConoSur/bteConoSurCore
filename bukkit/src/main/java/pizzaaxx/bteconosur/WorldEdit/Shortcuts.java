package pizzaaxx.bteconosur.WorldEdit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;

public class Shortcuts implements Listener {

    private final BTEConoSur plugin;

    public Shortcuts(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(@NotNull PlayerInteractEvent event) {
        Player p = event.getPlayer();
        int increment = plugin.getPlayerRegistry().get(p.getUniqueId()).getMiscManager().getIncrement();
        if (event.getItem() != null && event.getItem().getType() == Material.WOOD_AXE) {
            switch (event.getAction()) {
                case LEFT_CLICK_AIR:
                    if (p.isSneaking()) {
                        p.performCommand("/shift " + increment);
                    } else {
                        p.performCommand("/expand " + increment);
                    }
                    break;
                case RIGHT_CLICK_AIR:
                    p.performCommand("/contract " + increment);
            }
        }
    }
}
