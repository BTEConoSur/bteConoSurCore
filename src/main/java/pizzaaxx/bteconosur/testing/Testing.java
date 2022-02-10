package pizzaaxx.bteconosur.testing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pizzaaxx.bteconosur.yaml.YamlManager;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;

public class Testing implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("test")) {
            sender.sendMessage((String) new YamlManager(pluginFolder, "help.yml").getValue("discord.help.usage"));
        }

        return true;
    }
}
