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

import java.time.Duration;
import java.util.UUID;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class UnbanCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public UnbanCommand(BTEConoSurPlugin plugin) {
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

        OfflinePlayer target;
        try {
            UUID targetUUID = UUID.fromString(args[0]);
            target = plugin.getServer().getOfflinePlayer(targetUUID);
        } catch (IllegalArgumentException e) {
            target = plugin.getServer().getOfflinePlayer(args[0]);
        }

        if (!target.isBanned()) {
            sender.sendMessage(PREFIX + "El jugador no está baneado.");
            return true;
        }

        ProfileBanList banList = Bukkit.getBanList(BanList.Type.PROFILE);
        banList.pardon(target.getPlayerProfile());
        sender.sendMessage(PREFIX + "El jugador ha sido desbaneado.");
        return true;
    }
}
