package pizzaaxx.bteconosur.testing;

import com.sk89q.worldedit.Vector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.worldedit.trees.Tree;
import pizzaaxx.bteconosur.yaml.YamlManager;

import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;

public class testing implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("test")) {
            sender.sendMessage((String) new YamlManager(pluginFolder, "help.yml").getValue("discord.help.usage"));
        }

        return true;
    }
}
