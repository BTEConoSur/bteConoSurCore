package pizzaaxx.bteconosur.testing;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import pizzaaxx.bteconosur.BteConoSur;

public class ReloadPlayer implements CommandExecutor {

    private final BteConoSur plugin;

    public ReloadPlayer(BteConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof ConsoleCommandSender) {

            if (args.length > 0) {

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

                if (player.hasPlayedBefore()) {

                    plugin.getPlayerRegistry().remove(player.getUniqueId());
                    plugin.getPlayerRegistry().load(player.getUniqueId());

                    sender.sendMessage("Jugador recargado.");

                } else {

                    sender.sendMessage("El jugador introducido nunca ha entrado al servidor.");

                }

            } else {

                sender.sendMessage("Introduce un jugador.");

            }

        }

        return true;
    }
}
