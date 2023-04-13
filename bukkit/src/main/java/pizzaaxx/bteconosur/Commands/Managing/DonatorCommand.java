package pizzaaxx.bteconosur.Commands.Managing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;

import java.io.IOException;
import java.sql.SQLException;

public class DonatorCommand implements CommandExecutor {

    private final BTEConoSur plugin;

    public DonatorCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, @NotNull String[] args) {

        if (!(sender instanceof ConsoleCommandSender)) {

            if (!(sender instanceof Player)) {
                return true;
            }

            Player p = (Player) sender;
            ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(p.getUniqueId());

            if (!serverPlayer.getSecondaryRoles().contains(ServerPlayer.SecondaryRoles.ADMIN)) {
                p.sendMessage(plugin.getPrefix() + "No puedes hacer esto.");
                return true;
            }

        }

        if (args.length == 0) {
            sender.sendMessage(plugin.getPrefix() + "Introduce el nombre de un jugador.");
            return true;
        }

        try {
            ServerPlayer s = plugin.getPlayerRegistry().get(args[0]);

            if (s == null) {
                sender.sendMessage(plugin.getPrefix() + "El jugador introducido no existe.");
                return true;
            }

            if (s.getSecondaryRoles().contains(ServerPlayer.SecondaryRoles.DONADOR)) {
                s.removeSecondaryRole(ServerPlayer.SecondaryRoles.DONADOR);
                s.sendNotification(
                        plugin.getPrefix() + "¡Has conseguido el rol de §dDonador§f!",
                        "**[ROLES]** » ¡Has conseguido el rol de **Donador**!"
                );
            } else {
                s.addSecondaryRole(ServerPlayer.SecondaryRoles.DONADOR);
                s.sendNotification(
                        plugin.getPrefix() + "Has perdido el rol de §dDonador§f.",
                        "**[ROLES]** » Has perdido el rol de **Donador**."
                );
            }

        } catch (SQLException | IOException e) {
            sender.sendMessage(plugin.getPrefix() + "Ha ocurrido un error en la base de datos.");
        }

        return true;
    }
}
