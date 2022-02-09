package pizzaaxx.bteconosur.events;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender p, Command command, String label, String[] args) {
        if (command.getName().equals("event")) {
            if (args.length > 0) {

            } else {
                // TODO GUI
            }
        }

        if (command.getName().equals("manageevent")) {

        }

        return true;
    }
}
