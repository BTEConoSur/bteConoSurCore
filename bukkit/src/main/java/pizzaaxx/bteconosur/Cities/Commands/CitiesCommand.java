package pizzaaxx.bteconosur.Cities.Commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Cities.Actions.CityActionException;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Countries.Country;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class CitiesCommand implements CommandExecutor, Prefixable {

    private final BTEConoSur plugin;

    public CitiesCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, @NotNull String[] args) {

        // /city create cl puertoVaras Puerto Varas ✔️
        // /city setDisplay puertoVaras Puerto Varas ✔️
        // /city setUrban puertoVaras ✔️
        // /city deleteUrban puertoVaras ✔️
        // /city redefine puertoVaras ✔️
        // /city delete puertoVaras

        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length < 1) {
            p.sendMessage("Introduce un subcomando.");
            return true;
        }

        switch (args[0]) {
            case "create": {
                if (args.length < 2) {
                    p.sendMessage(this.getPrefix() + "Introduce el código de un país.");
                    return true;
                }

                String countryAbbreviation = args[1];
                if (!plugin.getCountryManager().exists(countryAbbreviation)) {
                    p.sendMessage(this.getPrefix() + "El pais introducido no existe.");
                    return true;
                }

                Country country = plugin.getCountryManager().get(countryAbbreviation);

                if (!p.hasPermission("bteconosur.city.admin." + country.getName())) {
                    p.sendMessage("No puedes manejar ciudades de este país.");
                    return true;
                }

                if (args.length < 3) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre.");
                    return true;
                }

                String name = args[2];

                if (!name.matches("[a-z]{1,32}")) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre válido. Solo minúsculas, sin espacios ni guiones bajos.");
                    return true;
                }

                if (plugin.getCityManager().exists(name)) {
                    p.sendMessage(this.getPrefix() + "El nombre usado ya está en uso.");
                    return true;
                }

                if (args.length < 4) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre para mostrar.");
                    return true;
                }

                String displayName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                List<BlockVector2D> points;
                try {
                    points = plugin.getWorldEdit().getSelectionPoints(p);
                } catch (IncompleteRegionException e) {
                    p.sendMessage(this.getPrefix() + "Selecciona un área cúbica o poligonal completa.");
                    return true;
                }

                try {
                    plugin.getCityManager().createCity(
                            name,
                            displayName,
                            country,
                            points
                    ).execute();
                } catch (CityActionException | SQLException | JsonProcessingException e) {
                    p.sendMessage(this.getPrefix() + "Ha ocurrido un error.");
                    e.printStackTrace();
                    return true;
                }
                break;
            }
            case "setDisplay": {
                if (args.length < 2) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre.");
                }

                String name = args[1];

                if (!name.matches("[a-zA-Z]{1,32}")) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre válido. Solo mayúsculas y minúsculas, sin espacios ni guiones bajos.");
                    return true;
                }

                if (!plugin.getCityManager().exists(name)) {
                    p.sendMessage(this.getPrefix() + "La ciudad introducida no existe.");
                    return true;
                }

                if (args.length < 3) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre para mostrar.");
                    return true;
                }

                City city = plugin.getCityManager().get(name);

                if (!p.hasPermission("bteconosur.city.admin." + city.getCountry().getName())) {
                    p.sendMessage("No puedes manejar ciudades de este país.");
                    return true;
                }

                String displayName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                try {
                    city.setDisplayName(displayName).execute();
                } catch (CityActionException | SQLException e) {
                    p.sendMessage(this.getPrefix() + "Ha ocurrido un error.");
                    e.printStackTrace();
                    return true;
                }
                break;
            }
            case "setUrban": {
                if (args.length < 2) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre.");
                }

                String name = args[1];

                if (!name.matches("[a-zA-Z]{1,32}")) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre válido. Solo mayúsculas y minúsculas, sin espacios ni guiones bajos.");
                    return true;
                }

                if (!plugin.getCityManager().exists(name)) {
                    p.sendMessage(this.getPrefix() + "La ciudad introducida no existe.");
                    return true;
                }

                City city = plugin.getCityManager().get(name);

                if (!p.hasPermission("bteconosur.city.admin." + city.getCountry().getName())) {
                    p.sendMessage(this.getPrefix() + "No puedes manejar ciudades de este país.");
                    return true;
                }

                List<BlockVector2D> points;
                try {
                    points = plugin.getWorldEdit().getSelectionPoints(p);
                } catch (IncompleteRegionException e) {
                    p.sendMessage(this.getPrefix() + "Selecciona un área cúbica o poligonal completa.");
                    return true;
                }

                try {
                    city.setUrbanArea(points).execute();
                } catch (CityActionException | SQLException e) {
                    p.sendMessage(this.getPrefix() + "Ha ocurrido un error.");
                    e.printStackTrace();
                    return true;
                }
                break;
            }
            case "deleteUrban": {
                if (args.length < 2) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre.");
                }

                String name = args[1];

                if (!name.matches("[a-zA-Z]{1,32}")) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre válido. Solo mayúsculas y minúsculas, sin espacios ni guiones bajos.");
                    return true;
                }

                if (!plugin.getCityManager().exists(name)) {
                    p.sendMessage(this.getPrefix() + "La ciudad introducida no existe.");
                    return true;
                }

                City city = plugin.getCityManager().get(name);

                if (!p.hasPermission("bteconosur.city.admin." + city.getCountry().getName())) {
                    p.sendMessage(this.getPrefix() + "No puedes manejar ciudades de este país.");
                    return true;
                }

                try {
                    city.deleteUrbanArea().execute();
                } catch (CityActionException | SQLException e) {
                    p.sendMessage(this.getPrefix() + "Ha ocurrido un error.");
                    e.printStackTrace();
                    return true;
                }
                break;
            }
            case "redefine": {
                if (args.length < 2) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre.");
                }

                String name = args[1];

                if (!name.matches("[a-zA-Z]{1,32}")) {
                    p.sendMessage(this.getPrefix() + "Introduce un nombre válido. Solo mayúsculas y minúsculas, sin espacios ni guiones bajos.");
                    return true;
                }

                if (!plugin.getCityManager().exists(name)) {
                    p.sendMessage(this.getPrefix() + "La ciudad introducida no existe.");
                    return true;
                }

                City city = plugin.getCityManager().get(name);

                if (!p.hasPermission("bteconosur.city.admin." + city.getCountry().getName())) {
                    p.sendMessage(this.getPrefix() + "No puedes manejar ciudades de este país.");
                    return true;
                }

                List<BlockVector2D> points;
                try {
                    points = plugin.getWorldEdit().getSelectionPoints(p);
                } catch (IncompleteRegionException e) {
                    p.sendMessage(this.getPrefix() + "Selecciona un área cúbica o poligonal completa.");
                    return true;
                }

                try {
                    city.redefine(points).execute();
                } catch (CityActionException e) {
                    p.sendMessage(this.getPrefix() + "Ha ocurrido un error.");
                    e.printStackTrace();
                    return true;
                }
                break;
            }
            case "delete": {
                // TODO DELETE SUBCOMMAND
            }
        }
        return true;
    }

    @Override
    public String getPrefix() {
        return "§f[§9CIUDADES§f] §7>> §f";
    }
}
