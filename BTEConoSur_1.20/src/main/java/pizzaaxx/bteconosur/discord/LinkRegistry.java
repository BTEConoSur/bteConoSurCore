package pizzaaxx.bteconosur.discord;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.discord.DiscordManager;
import pizzaaxx.bteconosur.utils.SQLUtils;
import pizzaaxx.bteconosur.utils.registry.BaseRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LinkRegistry extends BaseRegistry<DiscordManager, String> {

    public LinkRegistry(BTEConoSurPlugin plugin) {
        super(
                plugin,
                () -> {
                    List<String> ids = new ArrayList<>();
                    try (SQLResult result = plugin.getSqlManager().select(
                            "discord_managers",
                            new SQLColumnSet("id"),
                            new SQLANDConditionSet()
                    ).retrieve()) {
                        ResultSet set = result.getResultSet();
                        while (set.next()) {
                            ids.add(set.getString("id"));
                        }
                    } catch (SQLException e) {
                        plugin.error("Could not load link registry.");
                    }
                    return ids;
                },
                id -> {
                    try (SQLResult result = plugin.getSqlManager().select(
                            "discord_managers",
                            new SQLColumnSet("uuid"),
                            new SQLANDConditionSet(
                                    new SQLOperatorCondition("id", "=", id)
                            )
                    ).retrieve()) {
                        ResultSet set = result.getResultSet();
                        if (!set.next()) {
                            plugin.error("Link instance not found. (ID:" + id + ")");
                            return null;
                        }

                        UUID uuid = SQLUtils.uuidFromBytes(set.getBytes("uuid"));
                        return plugin.getPlayerRegistry().get(uuid).getDiscordManager();
                    } catch (SQLException e) {
                        plugin.error("Error loading link instance. (ID:" + id + ")");
                        return null;
                    }
                },
                true
        );
    }

}
