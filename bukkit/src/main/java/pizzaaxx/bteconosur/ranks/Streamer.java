package pizzaaxx.bteconosur.ranks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pizzaaxx.bteconosur.server.player.GroupsManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

public class Streamer implements CommandExecutor {
    public static String streamerPrefix = "§f[§aSTREAMER§f] §7>>§r ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0 && Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            ServerPlayer s = new ServerPlayer(target);
            GroupsManager manager = s.getGroupsManager();

            if (manager.getSecondaryGroups().contains(GroupsManager.SecondaryGroup.STREAMER)) {
                manager.removeSecondaryGroup(GroupsManager.SecondaryGroup.STREAMER);
                sender.sendMessage(streamerPrefix + "Has quitado el rango §aSTREAMER§f a §a" + s.getName() + "§f.");
                s.sendNotification(streamerPrefix + "Te han quitado el rango §a**STREAMER**§f.");
            } else {
                manager.addSecondaryGroup(GroupsManager.SecondaryGroup.STREAMER);
                sender.sendMessage(streamerPrefix + "Has dado el rango §aSTRAEMER§f a §a" + s.getName() + "§f.");
                s.sendNotification(streamerPrefix + "Te han dado el rango §a**STREAMER**§f.");
            }
        } else {
            sender.sendMessage(streamerPrefix + "Introduce un jugador válido.");
        }

        return true;
    }
}
