package pizzaaxx.bteconosur.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class TPOfflineCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public TPOfflineCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(PREFIX + "Debes especificar un jugador.");
            return true;
        }

        // check if player is currently online
        Player target = Bukkit.getPlayer(args[0]);
        if (target != null) {
            player.teleport(target);
            player.sendMessage(PREFIX + "Te has teletransportado a " + args[0] + ".");
            return true;
        }

        try (SQLResult result = plugin.getSqlManager().select(
                "players",
                new SQLColumnSet("last_location"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("name", "=", args[0])
                )
        ).retrieve()) {

            ResultSet set = result.getResultSet();
            if (!set.next()) {
                player.sendMessage(PREFIX + "No se ha encontrado al jugador.");
                return true;
            }

            JsonNode locationNode = plugin.getJsonMapper().readTree(set.getString("last_location"));
            Location location = new Location(
                    Bukkit.getWorld(UUID.fromString(locationNode.get("world").asText())),
                    locationNode.get("x").asDouble(),
                    locationNode.get("y").asDouble(),
                    locationNode.get("z").asDouble()
            );

            player.teleport(location);
            player.sendMessage(PREFIX + "Te has teletransportado a la última ubicación de " + args[0] + ".");

        } catch (SQLException | JsonProcessingException e) {
            player.sendMessage(PREFIX + "Ha ocurrido un error en la base de datos.");
        }

        return true;
    }
}
