package pizzaaxx.bteconosur.country.cities;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.worldedit.WorldEditHelper;
import pizzaaxx.bteconosur.worldguard.WorldGuardProvider;

import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class CityDefiningCommand implements CommandExecutor {

    private final String prefix = "§f[§3CIUDADES§f] §7>>§r ";
    private final Set<ProtectedRegion> countryRegions = new HashSet<>();
    private final Map<String, CityRegistry> registries;

    public CityDefiningCommand(Map<String, CityRegistry> registries) {
        this.registries = registries;
        RegionManager manager = WorldGuardProvider.getWorldGuard().getRegionManager(mainWorld);
        countryRegions.add(manager.getRegion("argentina"));
        countryRegions.add(manager.getRegion("bolivia"));
        countryRegions.add(manager.getRegion("chile_cont"));
        countryRegions.add(manager.getRegion("chile_idp"));
        countryRegions.add(manager.getRegion("paraguay"));
        countryRegions.add(manager.getRegion("peru"));
        countryRegions.add(manager.getRegion("uruguay"));
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("city")) {

            if (sender instanceof Player) {

                Player p = (Player) sender;

                // /city define Temuco chile
                // /city define Temuco
                // /city defineUrban Temuco
                // /city defineDisplayName Pucon Pucón
                // /city delete Temuco

                if (args.length > 0) {

                    switch (args[0]) {

                        case "define":

                            if (args.length < 2) {
                                p.sendMessage(prefix + "Introduce un nombre.");
                                return true;
                            }

                            if (!args[1].matches("[a-zA-Z_]{1,64}") || args[1].contains(" ") || args[1].startsWith("argentina_") || args[1].startsWith("bolivia_") || args[1].startsWith("chile_") || args[1].startsWith("paraguay_") || args[1].startsWith("peru_") || args[1].startsWith("uruguay_")) {
                                p.sendMessage(prefix + "Introduce un nombre válido. Si necesitas usar espacios o tildes, puedes agregarlos más tarde usando §a/city defineDisplayName§f.");
                                return true;
                            }

                            String name = args[1];

                            Region rawSelection;
                            try {
                                rawSelection = WorldEditHelper.getSelection(p);
                            } catch (IncompleteRegionException e) {
                                p.sendMessage(prefix + "Selecciona un área válida. Debe ser de tipo poligonal.");
                                return true;
                            }

                            if (!(rawSelection instanceof Polygonal2DRegion)) {
                                p.sendMessage(prefix + "Selecciona un área de tipo poligonal.");
                                return true;
                            }

                            Polygonal2DRegion selection = (Polygonal2DRegion) rawSelection;

                            ProtectedPolygonalRegion protectedPolygonalRegion = new ProtectedPolygonalRegion("temp", selection.getPoints(), -100, 8000);

                            List<ProtectedRegion> intersectingCountriesRegions =  protectedPolygonalRegion.getIntersectingRegions(countryRegions);

                            List<String> intersectingCountries = new ArrayList<>();

                            for (ProtectedRegion region : intersectingCountriesRegions) {
                                intersectingCountries.add(region.getId().replace("_idp", "").replace("_cont", ""));
                            }

                            OldCountry country;
                            if (args.length > 2) {
                                if (intersectingCountries.contains(args[2].toLowerCase())) {
                                    country = new OldCountry(args[2].toLowerCase());
                                } else {
                                    p.sendMessage("Tu selección no intersecta con el país introducido. Asegúrate de haberlo escrito bien (sin tildes).");
                                    return true;
                                }
                            } else {
                                if (!intersectingCountries.isEmpty()) {

                                    if (intersectingCountries.size() == 1) {
                                        country = new OldCountry(intersectingCountries.get(0));
                                    } else {
                                        p.sendMessage(prefix + "Tu selección pertenece a 2 o más países. Introduce el país al final del comando para especificar a cual pertenece la ciudad.");
                                        return true;
                                    }

                                } else {
                                    p.sendMessage(prefix + "Tu selección de WorldEdit no se encuentra dentro de ningún país.");
                                    return true;
                                }
                            }

                            CityRegistry registry = registries.get(country.getName());

                            registry.createCity(name, selection.getPoints());

                            p.sendMessage(prefix + "Ciudad con el nombre §a" + name + "§f creada correctamente.");

                            break;
                        case "defineUrban":
                            break;
                        case "defineDisplayName":
                            break;
                        case "delete":
                            break;
                    }

                } else {

                    p.sendMessage(prefix + "Introduce un subcomando.");

                }
            }


        }

        return true;
    }
}
