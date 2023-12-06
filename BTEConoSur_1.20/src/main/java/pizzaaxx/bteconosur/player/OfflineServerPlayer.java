package pizzaaxx.bteconosur.player;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.discord.DiscordManager;
import pizzaaxx.bteconosur.utils.registry.RegistrableEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class OfflineServerPlayer implements RegistrableEntity<UUID> {

    private final BTEConoSurPlugin plugin;
    private final UUID uuid;
    private final String name;
    private final DiscordManager discordManager;

    public OfflineServerPlayer(@NotNull BTEConoSurPlugin plugin, UUID uuid) throws SQLException {
        this.plugin = plugin;
        this.uuid = uuid;

        try (SQLResult result = plugin.getSqlManager().select(
                "players",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("uuid", "=", uuid)
                )
        ).retrieve()) {
            ResultSet set = result.getResultSet();
            if (!set.next()) {
                throw new SQLException("Player not found.");
            }

            this.name = set.getString("name");

            this.discordManager = new DiscordManager(plugin, this);
        }
    }

    @Contract(pure = true)
    public OfflineServerPlayer(@NotNull OfflineServerPlayer base) {
        this.plugin = base.plugin;
        this.uuid = base.uuid;
        this.discordManager = base.discordManager;
        this.name = base.name;
    }

    public OnlineServerPlayer asOnlinePlayer() throws SQLException {
        return new OnlineServerPlayer(plugin, this);
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public UUID getID() {
        return this.uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public void disconnected() {}

    public boolean isOnline() {
        return Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getUniqueId().equals(this.uuid));
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public void sendNotification(String minecraftMessage, String discordMessage) throws SQLException {
        if (this.discordManager.isLinked()) {
            this.discordManager.getUser().queue(user -> user.openPrivateChannel().queue(channel -> channel.sendMessage(":bell:" + discordMessage).queue()));
        } else {
            try {
                plugin.getSqlManager().insert(
                        "notifications",
                        new SQLValuesSet(
                                new SQLValue("target", this.uuid),
                                new SQLValue("discord_message", discordMessage),
                                new SQLValue("minecraft_message", minecraftMessage),
                                new SQLValue("date", System.currentTimeMillis())
                        )
                ).execute();
            } catch (SQLException e) {
                throw new SQLException("Error saving notification.");
            }
        }
    }
}
