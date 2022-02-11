package pizzaaxx.bteconosur.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static pizzaaxx.bteconosur.bteConoSur.mainWorld;

public class LobbyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (command.getName().equals("assets")) {
                p.teleport(new Location(mainWorld, 10000.5, 407, 0.5));
                p.sendMessage("§f[§7ASSETS§f] §7>>§r Teletransportándote a §oassets§r.");
            }

            if (command.getName().equals("lobby")) {

            }
        }

        return true;
    }
}
