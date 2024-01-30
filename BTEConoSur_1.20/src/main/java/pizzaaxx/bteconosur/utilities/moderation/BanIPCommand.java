package pizzaaxx.bteconosur.utilities.moderation;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;

import java.util.UUID;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class BanIPCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public BanIPCommand(BTEConoSurPlugin plugin) {
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

        Player target;
        try {
            UUID targetUUID = UUID.fromString(args[0]);
            target = plugin.getServer().getPlayer(targetUUID);
        } catch (IllegalArgumentException e) {
            target = plugin.getServer().getPlayer(args[0]);
        }

        if (target == null) {
            sender.sendMessage(PREFIX + "El jugador no está conectado.");
            return true;
        }

        if (target.isBanned()) {
            sender.sendMessage(PREFIX + "El jugador ya está baneado.");
            return true;
        }

        String reason = "Has sido baneado por §a" + sender.getName() + "§r.";
        if (args.length > 1) {
            reason = "Has sido baneado por §a" + sender.getName() + "§r: " + String.join(" ", args).substring(args[0].length() + 1);
        }

        target.banPlayerFull(reason, null, null);

        sender.sendMessage(PREFIX + "La IP del jugador ha sido baneado.");
        return true;
    }
}
