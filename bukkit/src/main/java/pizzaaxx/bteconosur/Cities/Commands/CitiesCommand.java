package pizzaaxx.bteconosur.Cities.Commands;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.SQL.SQLParser;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class CitiesCommand implements CommandExecutor, Prefixable {

    private final BTEConoSur plugin;

    public CitiesCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, @NotNull String[] args) {

        // /city create cl puertoVaras Puerto Varas ✔️

        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
            return true;
        }

        Player p = (Player) sender;
        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());

        if (!s.getSecondaryRoles().contains(ServerPlayer.SecondaryRoles.ADMIN)) {
            p.sendMessage("Solo administradores pueden manejar las ciudades.");
            return true;
        }

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

        if (!s.getProjectManager().hasAdminPermission(country)) {
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

        if (!displayName.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ\\-_., \\d]{1,32}")) {
            p.sendMessage("Nombre a mostrar inválido.");
            return true;
        }

        List<BlockVector2D> points;
        try {
            points = plugin.getWorldEdit().getSelectionPoints(p);
        } catch (IncompleteRegionException e) {
            p.sendMessage(this.getPrefix() + "Selecciona un área cúbica o poligonal completa.");
            return true;
        }

        Map<String, Object> json = new HashMap<>();
        json.put("type", "minecraft");

        List<List<Integer>> coordinates = new ArrayList<>();
        for (BlockVector2D vector : points) {
            List<Integer> point = new ArrayList<>();
            point.add(vector.getBlockX());
            point.add(vector.getBlockZ());
            coordinates.add(point);
        }
        json.put("coordinates", coordinates);

        File target = new File(plugin.getDataFolder(), "cities/" + name + ".json");
        try {
            if (target.createNewFile()) {

                FileWriter writer = new FileWriter(target);
                writer.write(SQLParser.getString(json, true));
                writer.close();

            } else {
                p.sendMessage("Ha ocurrido un error.");
            }
        } catch (IOException e) {
            p.sendMessage("Ha ocurrido un error.");
        }

        try {
            plugin.getSqlManager().insert(
                    "cities",
                    new SQLValuesSet(
                            new SQLValue(
                                    "name", name
                            ),
                            new SQLValue(
                                    "display_name", displayName
                            ),
                            new SQLValue(
                                    "country", country
                            )
                    )
            ).execute();
        } catch (SQLException e) {
            target.delete();
            p.sendMessage("Ha ocurrido un error.");
        }

        return true;
    }

    @Override
    public String getPrefix() {
        return "§f[§9CIUDADES§f] §7>> §f";
    }
}
