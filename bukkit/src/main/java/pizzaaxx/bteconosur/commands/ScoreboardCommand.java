package pizzaaxx.bteconosur.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.ServerPlayer.ScoreboardManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

import static pizzaaxx.bteconosur.country.OldCountry.countryRegionNames;
import static pizzaaxx.bteconosur.misc.Misc.getSimplePrefix;
import static pizzaaxx.bteconosur.worldguard.RegionEvents.getEnteredRegions;
import static pizzaaxx.bteconosur.worldguard.RegionEvents.getLeftRegions;

public class ScoreboardCommand implements Listener, CommandExecutor {

    public static String scoreboardPrefix = getSimplePrefix("SCOREBOARD", "9");

    private final BteConoSur plugin;

    public ScoreboardCommand(BteConoSur plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(@NotNull PlayerJoinEvent e) {
        plugin.getPlayerRegistry().get(e.getPlayer().getUniqueId()).getScoreboardManager().update();
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

        ServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId());
        ScoreboardManager manager = s.getScoreboardManager();
        if (project && manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    manager.update();
                }
            };
            runnable.runTaskLater(Bukkit.getPluginManager().getPlugin("bteConoSur"), 1);
        } else if (country) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (manager.getType() == ScoreboardManager.ScoreboardType.TOP) {
                        manager.update();
                    }

                    for (Player oPlayer : Bukkit.getOnlinePlayers()) {
                        ServerPlayer p = plugin.getPlayerRegistry().get(oPlayer.getUniqueId());
                        if (p.getScoreboardManager().getType() == ScoreboardManager.ScoreboardType.SERVER) {
                            p.getScoreboardManager().update();
                        }
                    }
                }
            };
            runnable.runTaskLater(Bukkit.getPluginManager().getPlugin("bteConoSur"), 1);
        }
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent e) {
        checkScoreboardMovement(e.getFrom(), e.getTo(), e.getPlayer());
    }

    @EventHandler
    public void onTeleport(@NotNull PlayerTeleportEvent e) {
        checkScoreboardMovement(e.getFrom(), e.getTo(), e.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, String label, String[] args) {

        if (command.getName().equals("scoreboard")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());
                ScoreboardManager manager = s.getScoreboardManager();
                if (args.length > 0) {
                    if (args[0].equals("project") || args[0].equals("me") || args[0].equals("server") || args[0].equals("top") || args[0].equals("proyecto")) {

                        manager.setAuto(false);
                        manager.setHidden(false);
                        manager.setType(ScoreboardManager.ScoreboardType.valueOf(args[0].replace("proyecto", "project").toUpperCase()));
                        manager.save();

                        p.sendMessage(scoreboardPrefix + "Has establecido tu §oscoreboard§f en §a" + args[0].replace("proyecto", "project").toUpperCase() + "§f.");
                    } else if (args[0].equals("auto")) {
                        if (manager.toggleAuto()) {
                            p.sendMessage(scoreboardPrefix + "Has activado el §oscoreboard§f automático.");
                        } else {
                            manager.setHidden(false);
                            p.sendMessage(scoreboardPrefix + "Has desactivado el §oscoreboard§f automático.");
                        }
                    } else {
                        p.sendMessage(scoreboardPrefix + "Introduce un tipo válido.");
                    }
                } else {
                    if (manager.toggleHidden()) {
                        p.sendMessage(scoreboardPrefix + "Has ocultado el §oscoreboard§f.");
                    } else {
                        p.sendMessage(scoreboardPrefix + "Ahora puedes ver el §oscoreboard§f.");
                    }
                }
            }
        }

        return true;
    }
}
