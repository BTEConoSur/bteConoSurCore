package pizzaaxx.bteconosur.countries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.projects.ProjectType;
import pizzaaxx.bteconosur.utils.registry.RegistrableEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static pizzaaxx.bteconosur.discord.DiscordConnector.BOT;

public class Country implements RegistrableEntity<String> {

    private final BTEConoSurPlugin plugin;
    private final String name;
    private final Component displayName;
    private final String abbreviation;
    private final String guildId;
    private final String showcaseId;
    private final String chatId;
    private final String logsId;
    private final String requestsId;
    private final String iconUrl;
    private final Location spawn;
    private final List<ProjectType> projectTypes;
    private final String headValue;
    private final String emoji;
    private final Component chatPrefix;
    private final Component tabPrefix;
    private final List<ProtectedPolygonalRegion> regions = new ArrayList<>();

    public Country(@NotNull BTEConoSurPlugin plugin, String name) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        this.name = name;
        try (SQLResult result = plugin.getSqlManager().select(
                "countries",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("name", "=", name)
                )
        ).retrieve()) {
            ResultSet set = result.getResultSet();
            if (!set.next()) {
                throw new SQLException("Country not found.");
            }

            this.displayName = LegacyComponentSerializer.legacyAmpersand().deserialize(set.getString("display_name"));
            this.abbreviation = set.getString("abbreviation");
            this.guildId = set.getString("guild_id");
            this.showcaseId = set.getString("showcase_id");
            this.chatId = set.getString("chat_id");
            this.logsId = set.getString("logs_id");
            this.requestsId = set.getString("requests_id");
            this.iconUrl = set.getString("icon_url");

            JsonNode spawnNode = plugin.getJsonMapper().readTree(set.getString("spawn_point"));
            this.spawn = new Location(
                    plugin.getWorld(spawnNode.path("y").asDouble()),
                    spawnNode.path("x").asDouble(),
                    spawnNode.path("y").asDouble(),
                    spawnNode.path("z").asDouble()
            );

            this.projectTypes = new ArrayList<>();
            JsonNode projectTypesNode = plugin.getJsonMapper().readTree(set.getString("project_types"));
            for (JsonNode typeNode : projectTypesNode) {
                this.projectTypes.add(
                        new ProjectType(plugin, typeNode.asText())
                );
            }

            this.headValue = set.getString("head_value");
            this.emoji = set.getString("emoji");
            this.chatPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(set.getString("chat_prefix"));
            this.tabPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(set.getString("tab_prefix"));

            JsonNode regionsNode = plugin.getJsonMapper().readTree(set.getString("regions"));
            for (JsonNode regionNode : regionsNode) {
                List<BlockVector2> vectors = new ArrayList<>();
                regionNode.forEach(
                        coordinateNode -> vectors.add(
                                BlockVector2.at(
                                        coordinateNode.path("x").asInt(),
                                        coordinateNode.path("z").asInt()
                                )
                        )
                );
                regions.add(
                        new ProtectedPolygonalRegion(
                                this.name,
                                vectors,
                                -100,
                                8000
                        )
                );
            }
        }
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Component getDisplayName() {
        return displayName;
    }

    public @NotNull String getAbbreviation() {
        return abbreviation;
    }

    public @NotNull String getGuildID() {
        return guildId;
    }

    public Guild getGuild() {
        return BOT.getGuildById(guildId);
    }

    public @NotNull String getShowcaseID() {
        return showcaseId;
    }

    public TextChannel getShowcase() {
        return BOT.getTextChannelById(showcaseId);
    }

    public @NotNull String getChatID() {
        return chatId;
    }

    public TextChannel getChat() {
        return BOT.getTextChannelById(chatId);
    }

    public @NotNull String getLogsID() {
        return logsId;
    }

    public TextChannel getLogs() {
        return BOT.getTextChannelById(logsId);
    }

    public @NotNull String getRequestsID() {
        return requestsId;
    }

    public TextChannel getRequests() {
        return BOT.getTextChannelById(requestsId);
    }

    public @NotNull String getIconURL() {
        return iconUrl;
    }

    public @NotNull Location getSpawn() {
        return spawn;
    }

    public @NotNull List<ProjectType> getProjectTypes() {
        return projectTypes;
    }

    public @NotNull String getHeadValue() {
        return headValue;
    }

    public @NotNull String getEmoji() {
        return emoji;
    }

    public @NotNull Component getChatPrefix() {
        return chatPrefix;
    }

    public @NotNull Component getTabPrefix() {
        return tabPrefix;
    }

    public @NotNull List<ProtectedPolygonalRegion> getRegions() {
        return regions;
    }

    @Override
    public String getID() {
        return name;
    }

    @Override
    public void disconnected() {

    }
}
