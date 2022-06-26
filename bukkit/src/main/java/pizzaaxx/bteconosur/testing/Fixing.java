package pizzaaxx.bteconosur.testing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.yaml.Configuration;

import java.io.File;

public class Fixing implements CommandExecutor {

    private final Plugin plugin;

    public Fixing(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, String label, String[] args) {

        if (command.getName().equals("fixProjects")) {

            File dir = new File(plugin.getDataFolder(), "toFix/projects");
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {

                    Configuration project = new Configuration(plugin, "toFix/projects/" + file.getName().replace(".yml", ""));

                    project.set("country", "chile");
                    project.set("pending", false);
                    project.set("location", null);

                    project.save();

                }
            }
        }

        if (command.getName().equals("fixPlayers")) {

        }

        return true;
    }
}
