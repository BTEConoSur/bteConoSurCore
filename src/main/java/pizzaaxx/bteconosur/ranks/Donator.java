package pizzaaxx.bteconosur.ranks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pizzaaxx.bteconosur.ServerPlayer;

public class Donator implements CommandExecutor {
    public static String donatorPrefix = "§f[§dDONADOR§f] §7>>§r ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            if (args.length > 0 && Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                ServerPlayer s = new ServerPlayer(target);

                if (s.getSecondaryGroups().contains("donator")) {
                    s.removeSecondaryGroup("donator");
                    sender.sendMessage(donatorPrefix + "Has quitado el rango §aDONADOR§f a §a" + s.getName() + "§f.");
                    s.sendNotification(donatorPrefix + "Te han quitado el rango §a**DONADOR**§f.");
                } else {
                    s.addSecondaryGroup("donator");
                    sender.sendMessage(donatorPrefix + "Has dado el rango §aDONADOR§f a §a" + s.getName() + "§f.");
                    s.sendNotification(donatorPrefix + "Te han dado el rango §a**DONADOR**§f. ¡Gracias por donar!");
                }
            } else {
                sender.sendMessage(donatorPrefix + "Introduce un jugador válido.");
            }

        return true;
    }
}
