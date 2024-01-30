package pizzaaxx.bteconosur.utilities.moderation;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.util.UUID;

public class ReloadPlayerCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public ReloadPlayerCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por la consola.");
            return true;
        }

        if (strings.length != 1) {
            sender.sendMessage("§cIntroduce el nombre del jugador.");
            return true;
        }

        String playerName = strings[0];
        UUID playerUUID = Bukkit.getPlayerUniqueId(playerName);
        if (playerUUID == null) {
            sender.sendMessage("§cEl jugador no existe.");
            return true;
        }

        if (!plugin.getPlayerRegistry().isLoaded(playerUUID)) {
            sender.sendMessage("§cEl jugador no está cargado.");
            return true;
        }
        plugin.getPlayerRegistry().unload(playerUUID);
        sender.sendMessage("§aJugador recargado.");
        return true;
    }
}
