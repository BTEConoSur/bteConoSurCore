package pizzaaxx.bteconosur.utilities.moderation;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class KickCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public KickCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        boolean canKick;
        if (sender instanceof ConsoleCommandSender) {
            canKick = true;
        } else {
            Player player = (Player) sender;
            OfflineServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId());
            canKick = s.getRoles().contains(OfflineServerPlayer.Role.ADMIN) || s.getRoles().contains(OfflineServerPlayer.Role.MOD);
        }

        if (!canKick) {
            sender.sendMessage(PREFIX + "No tienes permisos para ejecutar este comando.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(PREFIX + "Debes especificar un jugador.");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(PREFIX + "El jugador no está conectado.");
            return true;
        }

        Component reason = Component.text("Has sido expulsado por §a" + sender.getName() + "§r.");
        if (args.length > 1) {
            reason = Component.text("Has sido expulsado por §a" + sender.getName() + "§r: " + String.join(" ", args).substring(args[0].length() + 1));
        }

        target.kick(reason);

        sender.sendMessage(PREFIX + "El jugador ha sido expulsado.");

        return true;
    }
}
