package pizzaaxx.bteconosur.player.scoreboard;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import fr.mrmicky.fastboard.adventure.FastBoard;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.PlayerManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class ScoreboardManager implements PlayerManager {

    private final BTEConoSurPlugin plugin;
    private final OnlineServerPlayer player;
    private FastBoard board;

    private boolean hidden;
    private boolean auto;
    private String type;

    private ScheduledTask updateTask;
    private ScoreboardDisplay currentDisplay;

    public ScoreboardManager(@NotNull BTEConoSurPlugin plugin, @NotNull OnlineServerPlayer player) throws SQLException {
        this.plugin = plugin;
        this.player = player;
        this.board = new FastBoard(player.getPlayer());

        ResultSet set = plugin.getSqlManager().select(
                "scoreboard_managers",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("uuid", "=", player.getUUID())
                )
        ).retrieve();

        if (set.next()) {

            this.type = set.getString("type");
            this.auto = set.getBoolean("auto");
            this.hidden = set.getBoolean("hidden");

        } else {
            plugin.getSqlManager().insert(
                    "scoreboard_managers",
                    new SQLValuesSet(
                            new SQLValue("uuid", player.getUUID()),
                            new SQLValue("type", "server"),
                            new SQLValue("auto", true),
                            new SQLValue("hidden", false)
                    )
            ).execute();
            this.type = "server";
            this.auto = true;
            this.hidden = false;
        }
    }

    public boolean isAuto() {
        return auto;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setAuto(boolean auto) throws SQLException {
        this.auto = auto;
        this.saveValue("auto", auto);
    }

    public void setHidden(boolean hidden) throws SQLException {
        this.hidden = hidden;
        this.saveValue("hidden", hidden);
        if (this.hidden) {
            this.board.delete();
        } else {
            this.setDisplay(this.currentDisplay);
        }
    }

    public void setDisplay(@NotNull ScoreboardDisplay display) throws SQLException {
        this.currentDisplay = display;
        if (this.board.isDeleted()) {
            this.board = new FastBoard(this.player.getPlayer());
        }
        if (!this.hidden) {
            this.board.updateLines(
                    display.getLines()
            );
            this.board.updateTitle(display.getTitle());
        }
        if (display.isSavable()) {
            this.type = display.getProvider().getIdentifier();
            this.saveValue("type", display.getProvider().getIdentifier());
        }
    }

    @Override
    public void saveValue(String key, Object value) throws SQLException {
        plugin.getSqlManager().update(
                "scoreboard_managers",
                new SQLValuesSet(
                        new SQLValue(key, value)
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", this.player.getUUID()
                        )
                )
        ).execute();
    }

    public String getType() {
        return type;
    }
}
