package pizzaaxx.bteconosur.utilities.moderation;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class TempBanCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public TempBanCommand(BTEConoSurPlugin plugin) {
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

        if (args.length < 2) {
            sender.sendMessage(PREFIX + "Debes especificar una duración.");
            return true;
        }

        String durationString = args[1];
        if (!durationString.matches("\\d+[smhd]")) {
            sender.sendMessage(PREFIX + "Duración inválida.");
            return true;
        }

        Duration duration;
        long number;
        try {
            number = Long.parseLong(durationString.substring(0, durationString.length() - 1));
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + "Duración inválida.");
            return true;
        }
        duration = switch (durationString.charAt(durationString.length() - 1)) {
            case 's' -> Duration.ofSeconds(number);
            case 'm' -> Duration.ofMinutes(number);
            case 'h' -> Duration.ofHours(number);
            case 'd' -> Duration.ofDays(number);
            default -> throw new IllegalStateException("Unexpected value: " + durationString.charAt(durationString.length() - 1));
        };
        Date date = new Date(System.currentTimeMillis() + duration.toMillis());

        String reason = "Has sido baneado por §a" + sender.getName() + "§r.";

        target.banPlayer(reason, date, null, true);

        sender.sendMessage(PREFIX + "El jugador ha sido baneado por " + durationString + ".");

        return true;
    }
}
