package pizzaaxx.bteconosur.player.scoreboard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;

import java.sql.SQLException;

public class ScoreboardCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public ScoreboardCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo jugadores pueden usar este comando.");
            return true;
        }

        OfflineServerPlayer sOffline = plugin.getPlayerRegistry().get(player.getUniqueId());

        if (!(sOffline instanceof OnlineServerPlayer s)) {
            player.sendMessage(plugin.getPrefix() + "Ha ocurrido un error.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("scoreboard")) {
            ScoreboardManager scoreboardManager = s.getScoreboardManager();
            if (args.length == 0) {
                try {
                    scoreboardManager.setHidden(!scoreboardManager.isHidden());
                } catch (SQLException e) {
                    player.sendMessage(plugin.getPrefix() + "Ha ocurrido un error en la base de datos.");
                }
            } else {
                if (args[0].equalsIgnoreCase("auto")) {
                    try {
                        scoreboardManager.setAuto(!scoreboardManager.isAuto());
                    } catch (SQLException e) {
                        player.sendMessage(plugin.getPrefix() + "Ha ocurrido un error en la base de datos.");
                    }
                } else if (ScoreboardDisplayProvider.PROVIDERS.containsKey(args[0])) {
                    try {
                        scoreboardManager.setDisplay(
                                ScoreboardDisplayProvider.PROVIDERS.get(args[0]).getDisplay(player)
                        );
                    } catch (SQLException e) {
                        player.sendMessage(plugin.getPrefix() + "Ha ocurrido un error en la base de datos.");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + "Introduce un subcomando válido.");
                }
            }
        }
        return true;
    }
}
