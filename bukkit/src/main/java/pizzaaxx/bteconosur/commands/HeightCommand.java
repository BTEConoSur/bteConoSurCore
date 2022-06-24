package pizzaaxx.bteconosur.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HeightCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            sender.sendMessage(" ");
            sender.sendMessage("Tu altitud actual es §a" + p.getLocation().getBlockY() + "§f.");
            sender.sendMessage(" ");
        }

        return true;
    }
}
