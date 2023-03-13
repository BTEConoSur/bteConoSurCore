package pizzaaxx.bteconosur.Player.Managers;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.Scoreboard.ScoreboardDisplay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ScoreboardManager {

    List<Class<?>> AUTO_ORDER = Arrays.asList(
            BTEConoSur.class, Country.class, City.class, Project.class, ServerPlayer.class
    );

    private final ServerPlayer serverPlayer;
    private ScoreboardDisplay display;
    private boolean auto;
    private boolean hidden;

    public ScoreboardManager(@NotNull ServerPlayer s) throws SQLException {

        this.serverPlayer = s;

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

    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isAuto() {
        return auto;
    }



}
