package pizzaaxx.bteconosur.utilities.moderation;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;

import java.net.InetAddress;
import java.util.UUID;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class UnbanIPCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public UnbanIPCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        boolean canBan;
        if (sender instanceof ConsoleCommandSender) {
            canBan = true;
        } else {
            Player player = (Player) sender;
            OfflineServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId());
            canBan = s.getRoles().contains(OfflineServerPlayer.Role.ADMIN) || s.getRoles().contains(OfflineServerPlayer.Role.MOD);
        }

        if (!canBan) {
            sender.sendMessage(PREFIX + "No tienes permisos para ejecutar este comando.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(PREFIX + "Debes especificar un jugador.");
            return true;
        }

        InetAddress target;
        try {
            target = InetAddress.getByName(args[0]);
        } catch (Exception e) {
            sender.sendMessage(PREFIX + "No se pudo encontrar la IP especificada.");
            return true;
        }

        if (!Bukkit.getIPBans().contains(target.getHostAddress())) {
            sender.sendMessage(PREFIX + "La IP no está baneada.");
            return true;
        }

        Bukkit.unbanIP(target);
        sender.sendMessage(PREFIX + "La IP ha sido desbaneada.");

        return true;
    }
}
