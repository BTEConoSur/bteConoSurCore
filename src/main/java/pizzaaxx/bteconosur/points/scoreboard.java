package pizzaaxx.bteconosur.points;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pizzaaxx.bteconosur.ServerPlayer;

import java.util.*;

import static pizzaaxx.bteconosur.country.Country.countryRegionNames;
import static pizzaaxx.bteconosur.worldguard.RegionEvents.getEnteredRegions;
import static pizzaaxx.bteconosur.worldguard.RegionEvents.getLeftRegions;

public class scoreboard implements Listener, CommandExecutor {

    public static String scoreboardPrefix = "§f[§9SCOREBOARD§f] §7>>§r ";

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new ServerPlayer(e.getPlayer()).updateScoreboard();
    }

    public void checkScoreboardMovement(Location from, Location to, Player player) {
        boolean project = false;
        boolean country = false;

        for (ProtectedRegion region : getEnteredRegions(from, to)) {
            if (region.getId().startsWith("project_")) {
                project = true;
            }
            if (countryRegionNames.contains(region.getId())) {
                country = true;
            }
        }

        for (ProtectedRegion region : getLeftRegions(from, to)) {
            if (region.getId().startsWith("project_")) {
                project = true;
            }
            if (countryRegionNames.contains(region.getId())) {
                country = true;
            }
        }

        ServerPlayer s = new ServerPlayer(player);
        if (project && s.getScoreboard().equals("project")) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    s.updateScoreboard();
                }
            };
            runnable.runTaskLater(Bukkit.getPluginManager().getPlugin("bteConoSur"), 1);
        } else if (country) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (s.getScoreboard().equals("top")) {
                        s.updateScoreboard();
                    }

                    for (Player oPlayer : Bukkit.getOnlinePlayers()) {
                        ServerPlayer p = new ServerPlayer(oPlayer);
                        if (p.getScoreboard().equals("server")) {
                            p.updateScoreboard();
                        }
                    }
                }
            };
            runnable.runTaskLater(Bukkit.getPluginManager().getPlugin("bteConoSur"), 1);
        }
    }

    public static List<String> scoreboardsOrder = Arrays.asList("server", "me", "project", "top");
    public static void checkAutoScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ServerPlayer s = new ServerPlayer(player);
            if (s.isScoreboardAuto()) {
                if (scoreboardsOrder.indexOf(s.getScoreboard()) + 1 != scoreboardsOrder.size()) {
                    s.setScoreboard(scoreboardsOrder.get(scoreboardsOrder.indexOf(s.getScoreboard()) + 1));
                } else {
                    s.setScoreboard(scoreboardsOrder.get(0));
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        checkScoreboardMovement(e.getFrom(), e.getTo(), e.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        checkScoreboardMovement(e.getFrom(), e.getTo(), e.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("scoreboard")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ServerPlayer s = new ServerPlayer(p);
                if (args.length > 0) {
                    if (args[0].equals("project") || args[0].equals("me") || args[0].equals("server") || args[0].equals("top") || args[0].equals("proyecto")) {
                        s.setScoreboardAuto(false);
                        s.setScoreboardHide(false);
                        s.setScoreboard(args[0].replace("proyecto", "project"));

                        p.sendMessage(scoreboardPrefix + "Has establecido tu §oscoreboard§f en §a" + args[0].replace("proyecto", "project").toUpperCase() + "§f.");
                    } else if (args[0].equals("auto")) {
                        if (s.isScoreboardAuto()) {
                            s.setScoreboardAuto(false);
                            p.sendMessage(scoreboardPrefix + "Has desactivado el §oscoreboard§f automático.");
                        } else {
                            s.setScoreboardHide(false);
                            s.setScoreboardAuto(true);
                            p.sendMessage(scoreboardPrefix + "Has activado el §oscoreboard§f automático.");
                        }
                    } else {
                        p.sendMessage(scoreboardPrefix + "Introduce un tipo válido.");
                    }
                } else {
                    if (s.isScoreboardHidden()) {
                        s.setScoreboardHide(false);
                        p.sendMessage(scoreboardPrefix + "Ahora puedes ver el §oscoreboard§f.");
                    } else {
                        s.setScoreboardHide(true);
                        p.sendMessage(scoreboardPrefix + "Has ocultado el §oscoreboard§f.");
                    }
                }
            }
        }

        return true;
    }
}
