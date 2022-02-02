package pizzaaxx.bteconosur.points;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;

import java.util.List;

import static pizzaaxx.bteconosur.ranks.points.getScoreboard;

public class scoreboard implements Listener, CommandExecutor {

    public static String scoreboardPrefix = "§f[§9SCOREBOARD§f] §7>>§r ";

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        List<ServerPlayer> top = getScoreboard(new Country(p.getLocation()));


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("scoreboard")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ServerPlayer s = new ServerPlayer(p);
                if (args.length > 0) {
                    if (args[0].equals("project") || args[0].equals("me") || args[0].equals("server") || args[0].equals("top") || args[0].equals("proyecto")) {
                        s.setScoreboardHide(false);
                        s.setScoreboard(args[0].replace("proyecto", "project"));

                        p.sendMessage(scoreboardPrefix + "Has establecido tu §oscoreboard§f en §a" + args[0].replace("proyecto", "project").toUpperCase() + "§f.");
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
