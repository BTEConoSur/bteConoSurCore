package pizzaaxx.bteconosur.building.worldedit;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.worldedit.WorldEditManager;

import java.sql.SQLException;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class PresetsCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public PresetsCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(PREFIX + "Introduce un subcomando.");
            return true;
        }

        try {
            OnlineServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId()).asOnlinePlayer();
            WorldEditManager manager = s.getWorldEditManager();

            switch (args[0]) {
                case "list" -> {
                    player.sendMessage(PREFIX + "Lista de presets:");
                    manager.getPresets().forEach((name, value) -> player.sendMessage("§7- §a" + name + "§7: §f" + value));
                }
                case "set" -> {
                    if (args.length < 3) {
                        player.sendMessage(PREFIX + "Introduce un nombre y un valor.");
                        return true;
                    }

                    String name = args[1];
                    String value = args[2];

                    manager.setPreset(name, value);
                    player.sendMessage(PREFIX + "Preset §a" + name + "§f establecido a §a" + value + "§f.");
                }
                case "remove" -> {
                    if (args.length < 2) {
                        player.sendMessage(PREFIX + "Introduce un nombre.");
                        return true;
                    }

                    String name = args[1];

                    manager.removePreset(name);
                    player.sendMessage(PREFIX + "Preset §a" + name + "§f eliminado.");
                }
            }

        } catch (SQLException | JsonProcessingException e) {
            player.sendMessage(PREFIX + "Ha ocurrido un error en la base de datos.");
        }

        return true;
    }
}
