package pizzaaxx.bteconosur.testing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.yaml.Configuration;

import java.io.File;
import java.util.*;

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

            File dir = new File(plugin.getDataFolder(), "toFix/players");
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {

                    Configuration player = new Configuration(plugin, "toFix/players/" + file.getName().replace(".yml", ""));

                    if (player.contains("points")) {

                        Map<String, Integer> points = new HashMap<>();
                        points.put("chile", player.getInt("points"));

                        player.set("points", points);

                    }

                    player.set("builder_rank", null);

                    player.set("primaryGroup", player.getString("primary_group"));
                    player.set("rank", null);
                    player.set("primary_group", null);

                    player.set("scoreboard.auto", true);
                    player.set("scoreboard.type", "server");
                    player.set("scoreboard.hidden", false);

                    player.set("chat.actual", "global");
                    player.set("chat.default", "global");
                    player.set("chat.hidden", false);
                    player.set("hide_chat", null);

                    player.set("increment", 1);
                    player.set("weincrement", null);

                    player.set("builder_rank", null);

                    if (player.contains("projects")) {
                        Set<String> projects = new HashSet<>(player.getStringList("projects"));

                        player.set("projects.chile", new ArrayList<>(projects));
                    }

                    if (player.contains("finished_projects")) {
                        int finished = player.getInt("finished_projects");
                        player.set("finishedProjects.chile", finished);
                    }
                    player.set("finished_projects", null);

                    player.save();

                }
            }

        }

        return true;
    }
}
