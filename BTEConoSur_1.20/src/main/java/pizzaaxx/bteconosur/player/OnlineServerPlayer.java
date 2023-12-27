package pizzaaxx.bteconosur.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardManager;
import pizzaaxx.bteconosur.player.worldedit.WorldEditManager;

import java.sql.SQLException;

public class OnlineServerPlayer extends OfflineServerPlayer {

    private final BTEConoSurPlugin plugin;
    private final ScoreboardManager scoreboardManager;
    private final WorldEditManager worldEditManager;

    public OnlineServerPlayer(@NotNull BTEConoSurPlugin plugin, OfflineServerPlayer offlinePlayer) throws SQLException {
        super(offlinePlayer);
        this.plugin = plugin;
        this.scoreboardManager = new ScoreboardManager(plugin, this);
        this.worldEditManager = new WorldEditManager(plugin, this);
    }

    public OfflineServerPlayer asOfflinePlayer() {
        return new OfflineServerPlayer(this);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(super.getUUID());
    }

    @Override
    public void disconnected() {
        plugin.getRegionListener().lastVisitedRegions.remove(this.getUUID());
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public WorldEditManager getWorldEditManager() {
        return worldEditManager;
    }

    @Override
    public void sendNotification(String minecraftMessage, String discordMessage) {
        this.getPlayer().sendMessage(minecraftMessage);
    }
}
