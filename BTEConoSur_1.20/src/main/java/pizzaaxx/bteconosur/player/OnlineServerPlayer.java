package pizzaaxx.bteconosur.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardManager;

import java.sql.SQLException;

public class OnlineServerPlayer extends OfflineServerPlayer {

    private final BTEConoSurPlugin plugin;
    private final ScoreboardManager scoreboardManager;

    public OnlineServerPlayer(@NotNull BTEConoSurPlugin plugin, OfflineServerPlayer offlinePlayer) throws SQLException {
        super(offlinePlayer);
        this.plugin = plugin;
        this.scoreboardManager = new ScoreboardManager(plugin, this);
    }

    public OfflineServerPlayer asOfflinePlayer() {
        return new OfflineServerPlayer(this);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(super.getUUID());
    }

    @Override
    public void disconnected() {}

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Override
    public void sendNotification(String minecraftMessage, String discordMessage) {
        this.getPlayer().sendMessage(minecraftMessage);
    }
}
