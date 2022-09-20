package pizzaaxx.bteconosur.testing;

import com.sk89q.worldguard.protection.managers.RegionManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Fixing implements CommandExecutor {

    private final Plugin plugin;

    public Fixing(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, String label, String[] args) {

        if (command.getName().equals("fix")) {

            if (args[0].equals("projects")) {
                File dir = new File(Bukkit.getPluginManager().getPlugin("Skript").getDataFolder(), "scripts/projects");
                File[] files = dir.listFiles();

                if (files != null) {
                    for (File file : files) {

                        Configuration project = new Configuration(Bukkit.getPluginManager().getPlugin("Skript"), "scripts/projects/" + file.getName().replace(".yml", ""));

                        project.set("country", "chile");
                        project.set("pending", false);
                        project.set("location", null);

                        project.save();

                        try {
                            FileUtils.copyFile(file, new File(plugin.getDataFolder(), "projects/" + file.getName().replace("project_", "")));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (args[0].equals("players2")) {

                File dir = new File(plugin.getDataFolder(), "playerData");
                File[] files = dir.listFiles();

                if (files != null) {

                    for (File file : files) {

                        Configuration player = new Configuration(plugin, "playerData/" + file.getName().replace(".yml", ""));

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().replace(".yml", "")));

                        if (offlinePlayer.getName() != null) {
                            player.set("name", offlinePlayer.getName());
                        }

                        player.save();
                    }
                }

            } else if (args[0].equals("players")) {

                File dir = new File(Bukkit.getPluginManager().getPlugin("Skript").getDataFolder(), "scripts/playerDataFix/playerData");
                File[] files = dir.listFiles();

                if (files != null) {
                    for (File file : files) {

                        Configuration player = new Configuration(Bukkit.getPluginManager().getPlugin("Skript"), "scripts/playerDataFix/playerData/" + file.getName().replace(".yml", ""));

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

                        try {
                            FileUtils.copyFile(file, new File(plugin.getDataFolder(), "playerData/" + file.getName()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (args[0].equals("links")) {

                Configuration links = new Configuration(plugin, "link/link");

                for (String key : links.getKeys(false)) {

                    String name = links.getString(key);

                    OfflinePlayer player = Bukkit.getOfflinePlayer(name);

                    if (player.hasPlayedBefore()) {

                        links.set(key, player.getUniqueId().toString());

                    }

                }

                links.save();


            } else if (args[0].equals("projects2")) {

                File dir = new File(plugin.getDataFolder(), "playerData");
                File[] files = dir.listFiles();

                if (files != null) {

                    for (File file : files) {

                        Configuration player = new Configuration(plugin, "playerData/" + file.getName().replace(".yml", ""));

                        if (player.contains("projects.chile")) {

                            List<String> projects = player.getStringList("projects.chile");

                            projects.removeIf(id -> !OldProject.projectExists(id));

                            player.set("projects.chile", projects);

                        }

                        player.save();
                    }
                }

            } else if (args[0].equals("projects3")) {

                File dir = new File(plugin.getDataFolder(), "projects");
                File[] files = dir.listFiles();

                RegionManager manager = getWorldGuard().getRegionContainer().get(mainWorld);

                if (files != null) {

                    for (File file : files) {

                        Configuration project = new Configuration(plugin, "projects/" + file.getName().replace(".yml", ""));

                        String id = file.getName().replace(".yml", "");

                        if (!manager.hasRegion("project_" + id)) {

                            project.save();

                            file.delete();

                            continue;

                        }

                        if (!project.contains("difficulty")) {

                            project.set("difficulty", "intermedio");

                        }

                        if (!project.contains("pending")) {

                            project.set("pending", false);

                        }

                        project.save();

                    }
                }

            } else if (args[0].equals("warps")) {

                File dir = new File(Bukkit.getPluginManager().getPlugin("Essentials").getDataFolder(), "warps");
                File[] files = dir.listFiles();

                if (files != null) {

                    for (File file : files) {

                        Configuration warp = new Configuration(Bukkit.getPluginManager().getPlugin("Essentials"), "warps/" + file.getName().replace(".yml", ""));

                        warp.set("world", "BTECS");

                        warp.save();
                    }

                }

            }
        }

        return true;
    }
}
