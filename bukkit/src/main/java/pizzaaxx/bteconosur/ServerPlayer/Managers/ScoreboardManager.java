package pizzaaxx.bteconosur.ServerPlayer.Managers;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.ServerPlayer.Managers.Scoreboards.PlayerScoreboard;
import pizzaaxx.bteconosur.ServerPlayer.Managers.Scoreboards.ProjectsScoreboard;
import pizzaaxx.bteconosur.ServerPlayer.Managers.Scoreboards.ServerScoreboard;
import pizzaaxx.bteconosur.ServerPlayer.Managers.Scoreboards.TopScoreboard;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

import java.util.List;

public class ScoreboardManager {

    private final ServerPlayer serverPlayer;
    private final DataManager data;
    private final ConfigurationSection scoreboard;
    private ScoreboardType type;
    private boolean auto;
    private boolean hidden;

    public static void checkAutoScoreboards(BteConoSur plugin) {

        for (Player player : Bukkit.getOnlinePlayers()) {
            ServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId());
            ScoreboardManager manager = s.getScoreboardManager();
            if (manager.isAuto()) {
                manager.setType(ScoreboardType.getFrom(manager.getType().getOrder() + 1));
            }
        }
    }

    public enum ScoreboardType {
        SERVER(1), PLAYER(2), PROJECT(3), TOP(4);

        private final int order;

        ScoreboardType(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }

        public static ScoreboardType getFrom(int order) {
            switch (order) {
                case 2:
                    return PLAYER;
                case 3:
                    return PROJECT;
                case 4:
                    return TOP;
                default:
                    return SERVER;
            }
        }
    }

    public ScoreboardManager(@NotNull ServerPlayer s) {
        serverPlayer = s;
        data = s.getDataManager();

        if (data.contains("scoreboard")) {
            scoreboard = data.getConfigurationSection("scoreboard");
            type = ScoreboardType.valueOf((scoreboard.getString("type", "server")).toUpperCase());
            auto = scoreboard.getBoolean("auto", true);
            hidden = scoreboard.getBoolean("hidden", false);
        } else {
            scoreboard = data.createSection("scoreboard");
            type = ScoreboardType.SERVER;
            auto = true;
            hidden = false;
        }

    }

    public void update() {
        Player p = (Player) serverPlayer.getPlayer();
        if (!hidden) {

            pizzaaxx.bteconosur.ServerPlayer.Managers.Scoreboards.ScoreboardType scoreboard;

            switch (type) {
                case PLAYER:
                    scoreboard = new PlayerScoreboard(serverPlayer);
                    break;
                case TOP:
                    scoreboard = new TopScoreboard(Bukkit.getPlayer(serverPlayer.getId()).getLocation(), serverPlayer.getPlugin());
                    break;
                case PROJECT:
                    scoreboard = new ProjectsScoreboard(serverPlayer, Bukkit.getPlayer(serverPlayer.getId()).getLocation(), serverPlayer.getPlugin());
                    break;
                default:
                    scoreboard = new ServerScoreboard(serverPlayer.getPlugin());
                    break;
            }

            Netherboard nb = Netherboard.instance();
            List<String> lines = scoreboard.getLines();
            if (!nb.hasBoard(p) || nb.getBoard(p).getLines().values() != lines) {
                BPlayerBoard board = Netherboard.instance().createBoard(p, scoreboard.getTitle());
                board.setAll(lines.toArray(new String[0]));
            }

        } else {

            if (Netherboard.instance().hasBoard(p)) {
                Netherboard.instance().getBoard(p).clear();
            }

        }
    }

    public boolean toggleHidden() {
        hidden = !hidden;
        update();
        save();
        return hidden;
    }

    public boolean toggleAuto() {
        auto = !auto;
        save();
        return auto;
    }

    public boolean isAuto() {
        return auto;
    }

    public ScoreboardType getType() {
        return type;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        update();
    }

    public void setType(@NotNull ScoreboardType type) {
        this.type = type;
        update();
        scoreboard.set("type", type.toString().toLowerCase());
        data.set("scoreboard", scoreboard);
        data.save();
    }

    public void save() {
        scoreboard.set("type", type.toString().toLowerCase());
        scoreboard.set("hidden", hidden);
        scoreboard.set("auto", auto);
        data.set("scoreboard", scoreboard);
        data.save();
    }
}
