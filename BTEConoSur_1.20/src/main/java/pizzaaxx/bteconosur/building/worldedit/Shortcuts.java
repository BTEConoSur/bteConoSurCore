package pizzaaxx.bteconosur.building.worldedit;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;

import java.sql.SQLException;

public class Shortcuts implements Listener {

    private final BTEConoSurPlugin plugin;

    public Shortcuts(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(@NotNull PlayerInteractEvent event) {

        // check if item in hand is not null and wooden axe
        if (event.getItem() != null && event.getItem().getType() == Material.WOODEN_AXE) {

            OnlineServerPlayer player;
            try {
                player = plugin.getPlayerRegistry().get(event.getPlayer().getUniqueId()).asOnlinePlayer();
            } catch (SQLException e) {
                plugin.error("Error getting online player from registry. (UUID:" + event.getPlayer().getUniqueId() + ")");
                return;
            }

            // EXPAND OR SHIFT
            if (event.getAction() == Action.LEFT_CLICK_AIR) {

                if (event.getPlayer().isSneaking()) {
                    event.getPlayer().performCommand("/shift " + player.getWorldEditManager().getIncrement());
                } else {
                    event.getPlayer().performCommand("/expand " + player.getWorldEditManager().getIncrement());
                }

            } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                event.getPlayer().performCommand("/contract " + player.getWorldEditManager().getIncrement());
            }
        }

    }

}
