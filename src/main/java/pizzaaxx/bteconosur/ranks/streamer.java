package pizzaaxx.bteconosur.ranks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.ServerPlayer;

public class streamer implements CommandExecutor {
    public String streamerPrefix = "§f[§aSTREAMER§f] §7>>§r ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("streamer")) {
            if (args.length > 0 && Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                ServerPlayer s = new ServerPlayer(target);

                if (s.getSecondaryGroups().contains("streamer")) {
                    s.removeSecondaryGroup("streamer");
                    sender.sendMessage(streamerPrefix + "Has quitado el rango §aSTREAMER§f a §a" + s.getName() + "§f.");
                    s.sendNotification(streamerPrefix + "Te han quitado el rango §a**STREAMER**§f.");
                } else {
                    s.addSecondaryGroup("streamer");
                    sender.sendMessage(streamerPrefix + "Has dado el rango §aSTRAEMER§f a §a" + s.getName() + "§f.");
                    s.sendNotification(streamerPrefix + "Te han dado el rango §a**STREAMER**§f.");
                }
            } else {
                sender.sendMessage(streamerPrefix + "Introduce un jugador válido.");
            }
        }

        return true;
    }
}
