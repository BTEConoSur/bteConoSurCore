package pizzaaxx.bteconosur.player.discord;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.PlayerManager;
import pizzaaxx.bteconosur.utils.registry.RegistrableEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

import static pizzaaxx.bteconosur.discord.DiscordConnector.BOT;

public class DiscordManager implements PlayerManager, RegistrableEntity<String> {

    private final BTEConoSurPlugin plugin;
    private final OfflineServerPlayer player;
    private String id;
    private String username;

    public DiscordManager(@NotNull BTEConoSurPlugin plugin, @NotNull OfflineServerPlayer player) throws SQLException {
        this.plugin = plugin;
        this.player = player;

        try (SQLResult result = plugin.getSqlManager().select(
                "discord_managers",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("uuid", "=", player.getUUID())
                )
        ).retrieve()) {
            ResultSet set = result.getResultSet();
            if (!set.next()) {
                id = null;
                username = null;
            } else {
                id = set.getString("id");
                username = set.getString("name");
            }
        }
    }

    public void link(String id) {
        BOT.retrieveUserById(id).queue(
                user -> {
                    try {
                        plugin.getSqlManager().insert(
                                "discord_managers",
                                new SQLValuesSet(
                                        new SQLValue("uuid", player.getUUID()),
                                        new SQLValue("id", id),
                                        new SQLValue("name", user.getName())
                                )
                        ).execute();
                        this.id = id;
                        this.username = user.getName();
                        this.plugin.getLinkRegistry().registerID(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        plugin.error("Error linking discord account. (ID:" + id + ", UUID:" + player.getUUID() + ")");
                        return;
                    }

                    this.username = user.getName();
                }
        );
    }

    public void unlink() {
        try {
            plugin.getSqlManager().delete(
                    "discord_managers",
                    new SQLANDConditionSet(
                            new SQLOperatorCondition("id", "=", id)
                    )
            ).execute();
            this.plugin.getLinkRegistry().unregisterID(id);
            this.id = null;
            this.username = null;
        } catch (SQLException e) {
            plugin.error("Error unlinking discord account. (ID:" + id + ", UUID:" + player.getUUID() + ")");
        }
    }

    @Override
    public void saveValue(String key, Object value) throws SQLException {
        plugin.getSqlManager().update(
                "discord_managers",
                new SQLValuesSet(
                        new SQLValue(key, value)
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("uuid", "=", player.getUUID())
                )
        ).execute();
    }

    @Override
    public String getID() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isLinked() {
        return this.id != null;
    }

    public CacheRestAction<User> getUser() {
        return BOT.retrieveUserById(this.id);
    }

    @Override
    public void disconnected() {}
}
