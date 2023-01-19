package pizzaaxx.bteconosur.Player.Managers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.Notifications.Notification;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DiscordManager {

    // --- STATIC ---

    public static boolean isLinked(@NotNull BTEConoSur plugin, String id) throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "discord_managers",
                new SQLColumnSet(
                        "id"
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", id
                        )
                )
        ).retrieve();

        return set.next();
    }

    @Nullable
    public static UUID getUUID(@NotNull BTEConoSur plugin, String id) throws SQLException, IOException {
        ResultSet set = plugin.getSqlManager().select(
                "discord_managers",
                new SQLColumnSet(
                        "uuid"
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", id
                        )
                )
        ).retrieve();

        if (set.next()) {
            return plugin.getSqlManager().getUUID(set, "uuid");
        }
        return null;
    }

    // --- CLASS ---

    private final BTEConoSur plugin;
    private final ServerPlayer serverPlayer;

    private String id;
    private String name;
    private String discriminator;

    private boolean hasSQLRow;

    public DiscordManager(@NotNull BTEConoSur plugin, @NotNull ServerPlayer serverPlayer) throws SQLException {
        this.plugin = plugin;
        this.serverPlayer = serverPlayer;

        ResultSet set = plugin.getSqlManager().select(
                "discord_managers",
                new SQLColumnSet(
                        "id", "name", "discriminator"
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).retrieve();

        if (set.next()) {
            this.hasSQLRow = true;
            this.id = set.getString("id");
            this.name = set.getString("name");
            this.discriminator = set.getString("discriminator");
        } else {
            this.hasSQLRow = false;
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public boolean isLinked() {
        return id != null;
    }

    public void link(String id) {
        plugin.getBot().retrieveUserById(id).queue(
                user -> {
                    this.id = id;
                    this.name = user.getName();
                    this.discriminator = user.getDiscriminator();

                    SQLValuesSet valuesSet = new SQLValuesSet(
                            new SQLValue(
                                    "id", this.id
                            ),
                            new SQLValue(
                                    "name", this.name
                            ),
                            new SQLValue(
                                    "discriminator", this.discriminator
                            )
                    );

                    try {
                        if (hasSQLRow) {
                            plugin.getSqlManager().update(
                                    "discord_managers",
                                    valuesSet,
                                    new SQLConditionSet(
                                            new SQLOperatorCondition(
                                                    "uuid", "=", serverPlayer.getUUID()
                                            )
                                    )
                            ).execute();
                        } else {
                            valuesSet.addValue(
                                    new SQLValue(
                                            "uuid", serverPlayer.getUUID()
                                    )
                            );
                            plugin.getSqlManager().insert(
                                    "discord_managers",
                                    valuesSet
                            ).execute();
                            this.hasSQLRow = true;
                        }

                        user.openPrivateChannel().queue(
                                channel -> {
                                    try {
                                        for (Notification notification : plugin.getNotificationsService().getNotifications(serverPlayer.getUUID())) {
                                            channel.sendMessage(notification.getDiscordMessage()).queue();
                                        }
                                        plugin.getNotificationsService().deleteNotifications(serverPlayer.getUUID());
                                    } catch (SQLException ignored) {}
                                }
                        );

                    } catch (SQLException e) {
                        this.id = null;
                        this.name = null;
                        this.discriminator = null;
                    }
                }
        );
    }

    public void unlink() throws SQLException {
        if (this.isLinked()) {
            plugin.getSqlManager().delete(
                    "discord_managers",
                    new SQLConditionSet(
                            new SQLOperatorCondition(
                                    "uuid", "=", serverPlayer.getUUID()
                            )
                    )
            ).execute();

            this.id = null;
            this.name = null;
            this.discriminator = null;
            this.hasSQLRow = false;
        }
    }
}
