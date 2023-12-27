package pizzaaxx.bteconosur.test;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.cities.City;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.terra.TerraCoords;

import java.util.Set;

public class TestCities implements Listener {

    private final BTEConoSurPlugin plugin;

    public TestCities(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(@NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().getType() == Material.IRON_SWORD) {
            Set<String> regions = plugin.getRegionListener().getRegionsAt(
                    event.getInteractionPoint().getX(),
                    event.getInteractionPoint().getZ()
            );
            event.getPlayer().sendMessage("Regions: " + regions);
        }
    }

}
