package pizzaaxx.bteconosur.Player;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.Managers.ChatManager;
import pizzaaxx.bteconosur.Player.Managers.MiscManager;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ServerPlayer {

    private final BTEConoSur plugin;
    private final UUID uuid;
    private final String name;
    private final ChatManager chatManager;
    private final MiscManager miscManager;

    public ServerPlayer(@NotNull BTEConoSur plugin, UUID uuid) throws SQLException {

        ResultSet set = plugin.getSqlManager().select(
                "players",
                new SQLColumnSet(
                        "*"
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", uuid
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.plugin = plugin;
            this.uuid = uuid;
            this.name = set.getString("name");

            this.chatManager = new ChatManager(this, plugin, set);
            this.miscManager = new MiscManager(plugin, this, set);

        } else {
            plugin.error("Missing player data: " + uuid);
            throw new SQLException();
        }

    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public MiscManager getMiscManager() {
        return miscManager;
    }

    public BTEConoSur getPlugin() {
        return plugin;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void save() {

    }

}
