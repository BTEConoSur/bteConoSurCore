package pizzaaxx.bteconosur.testing;

import com.sk89q.worldedit.Vector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.worldedit.trees.Tree;

public class testing implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("test")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;

                try {
                    new Tree("test").place(new Vector(p.getLocation().getBlockX(), p.getLocation().getBlockY() + 1, p.getLocation().getBlockZ()), p, null);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        return true;
    }
}
