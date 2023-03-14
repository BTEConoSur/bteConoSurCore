package pizzaaxx.bteconosur.Player.Managers;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Scoreboard.ScoreboardDisplay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ScoreboardManager {

    private final BTEConoSur plugin;
    private final ServerPlayer serverPlayer;
    private ScoreboardDisplay display;
    private String displayType;
    private boolean auto;
    private boolean hidden;

    public ScoreboardManager(@NotNull ServerPlayer s) throws SQLException {

        this.serverPlayer = s;
        this.plugin = s.getPlugin();

        ResultSet set = s.getPlugin().getSqlManager().select(
                "scoreboard_managers",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", s.getUUID()
                        )
                )
        ).retrieve();

        set.next();

        this.auto = set.getBoolean("auto");
        this.hidden = set.getBoolean("hidden");
        this.displayType = set.getString("type");
        if (Bukkit.getOfflinePlayer(s.getUUID()).isOnline()) {
            this.display = plugin.getScoreboardHandler().getDisplay(plugin.getScoreboardHandler().getClass(displayType), s);
        }

    }

    public void loadDisplay() {
        this.display = plugin.getScoreboardHandler().getDisplay(plugin.getScoreboardHandler().getClass(displayType), serverPlayer);
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) throws SQLException {

        plugin.getSqlManager().update(
                "scoreboard_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "hidden", hidden
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();

        this.hidden = hidden;

    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) throws SQLException {

        plugin.getSqlManager().update(
                "scoreboard_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "hidden", hidden
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();

        this.auto = auto;
        if (auto) {
            plugin.getScoreboardHandler().registerAuto(serverPlayer.getUUID());
        } else {
            plugin.getScoreboardHandler().unregisterAuto(serverPlayer.getUUID());

        }
    }

    public ScoreboardDisplay getDisplay() {
        return display;
    }

    public Class<? extends ScoreboardDisplay> getDisplayClass() {
        return display.getClass();
    }

    public void setDisplay(@NotNull ScoreboardDisplay display) throws SQLException {

        if (!isHidden()) {
            Player p = Bukkit.getPlayer(serverPlayer.getUUID());
            Netherboard netherboard = plugin.getNetherboard();

            if (netherboard.hasBoard(p)) {

                BPlayerBoard board = netherboard.getBoard(p);

                List<String> targetLines = display.getScoreboardLines();
                Map<Integer, String> sourceLines = board.getLines();
                if (targetLines.size() > sourceLines.size()) {

                    int counter = 15;
                    for (String line : targetLines) {
                        if (!sourceLines.containsKey(counter) || !sourceLines.get(counter).equals(line)) {
                            board.set(line, counter);
                        }
                        counter--;
                    }

                } else {

                    for (int i = 15; i > 15 - sourceLines.size(); i--) {

                        if (i <= 15 - targetLines.size()) {
                            board.remove(i);
                            continue;
                        }

                        if (!targetLines.get(i).equals(sourceLines.get(i))) {
                            board.set(targetLines.get(i), i);
                        }

                    }

                }

            } else {
                BPlayerBoard board = netherboard.createBoard(
                        p,
                        display.getScoreboardTitle()
                );

                board.setAll(
                        display.getScoreboardLines().toArray(new String[0])
                );
            }
        }

        if (!this.display.getScoreboardType().equals(display.getScoreboardType())) {
            plugin.getSqlManager().update(
                    "scoreboard_managers",
                    new SQLValuesSet(
                            new SQLValue(
                                    "type", display.getScoreboardType()
                            )
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "uuid", "=", serverPlayer.getUUID()
                            )
                    )
            ).execute();
        }

        this.display = display;
        this.displayType = display.getScoreboardType();
        plugin.getScoreboardHandler().registerDisplay(serverPlayer.getUUID(), this.display);
    }
}
