package pizzaaxx.bteconosur.testing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pizzaaxx.bteconosur.yaml.YamlManager;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;

public class Testing implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        YamlManager yaml = new YamlManager(pluginFolder, "events.yml");
        if (command.getName().equals("test")) {
            switch (args[0]) {
                case "get":
                    sender.sendMessage(yaml.getValue(args[1]).toString());
                    break;
                case "set":
                    yaml.setValue(args[1], "test");
                    yaml.write();
                    break;
                case "delete":
                    yaml.deleteValue(args[1]);
                    yaml.write();
                    break;
            }
        }
        yaml.write();

        return true;
    }
}
