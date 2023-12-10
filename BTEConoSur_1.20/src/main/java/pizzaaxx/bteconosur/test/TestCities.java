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

public class TestCities implements Listener {

    private final BTEConoSurPlugin plugin;

    public TestCities(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(@NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().getType() == Material.IRON_SWORD) {
            Country country = plugin.getCountriesRegistry().getCountryAt(event.getInteractionPoint());
            if (country == null) {
                event.getPlayer().sendMessage("No estás en ningún país.");
                return;
            }
            City city = country.getCityAt(
                    TerraCoords.fromMc(
                            event.getInteractionPoint().getBlockX(),
                            event.getInteractionPoint().getBlockZ()
                    )
            );
            if (city == null) {
                event.getPlayer().sendMessage("No estás en ninguna ciudad. Estás en " + country.getName() + ".");
                return;
            }
            event.getPlayer().sendMessage("Estás en " + city.getName() + ", " + country.getName());
        }
    }

}
