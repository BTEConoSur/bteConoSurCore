package pizzaaxx.bteconosur.player;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.discord.DiscordManager;
import pizzaaxx.bteconosur.player.projects.ProjectsManager;
import pizzaaxx.bteconosur.utils.StringUtils;
import pizzaaxx.bteconosur.utils.registry.RegistrableEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static pizzaaxx.bteconosur.utils.ChatUtils.*;

public class OfflineServerPlayer implements RegistrableEntity<UUID> {

    public enum Role {
        ADMIN, MOD, STREAMER, DONOR
    }

    private final BTEConoSurPlugin plugin;
    private final UUID uuid;
    private final String name;
    private final Set<Role> roles;
    private final Set<String> managedCountries;
    private final DiscordManager discordManager;
    private final ProjectsManager projectsManager;
    private final long lastDisconnection;
    private final Location lastLocation;

    public OfflineServerPlayer(@NotNull BTEConoSurPlugin plugin, UUID uuid) throws SQLException, JsonProcessingException {
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
            this.roles = new HashSet<>();
            JsonNode rolesNode = plugin.getJsonMapper().readTree(set.getString("roles"));
            for (JsonNode roleNode : rolesNode) {
                this.roles.add(Role.valueOf(roleNode.asText()));
            }

            this.managedCountries = new HashSet<>();
            JsonNode managedCountriesNode = plugin.getJsonMapper().readTree(set.getString("managed_countries"));
            for (JsonNode managedCountryNode : managedCountriesNode) {
                this.managedCountries.add(managedCountryNode.asText());
            }

            this.discordManager = new DiscordManager(plugin, this);

            this.projectsManager = new ProjectsManager(plugin, this);

            this.lastDisconnection = set.getLong("last_disconnected");

            if (set.getString("last_location") == null) {
                this.lastLocation = null;
            } else {
                JsonNode lastLocationNode = plugin.getJsonMapper().readTree(set.getString("last_location"));
                if (lastLocationNode.isEmpty()) {
                    this.lastLocation = null;
                } else {
                    this.lastLocation = new Location(
                            Bukkit.getWorld(lastLocationNode.get("world").asText()),
                            lastLocationNode.get("x").asDouble(),
                            lastLocationNode.get("y").asDouble(),
                            lastLocationNode.get("z").asDouble()
                    );
                }
            }

        }
    }

    @Contract(pure = true)
    public OfflineServerPlayer(@NotNull OfflineServerPlayer base) {
        this.plugin = base.plugin;
        this.uuid = base.uuid;
        this.discordManager = base.discordManager;
        this.name = base.name;
        this.roles = base.roles;
        this.managedCountries = base.managedCountries;
        this.projectsManager = base.projectsManager;
        this.lastDisconnection = base.lastDisconnection;
        this.lastLocation = base.lastLocation;
    }

    public OnlineServerPlayer asOnlinePlayer() throws SQLException, JsonProcessingException {
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

    public Set<Role> getRoles() {
        return roles;
    }

    public Set<String> getManagedCountries() {
        return managedCountries;
    }

    @Override
    public void disconnected() {}

    public boolean isOnline() {
        return Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getUniqueId().equals(this.uuid));
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public ProjectsManager getProjectsManager() {
        return projectsManager;
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

    public List<Component> getLore() {
        List<Component> lines = new ArrayList<>(List.of(
                Component.text("◆")
                        .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                        .append(Component.text("◆"))
                        .color(TextColor.color(DARK_GRAY)),

                Component.text(StringUtils.transformToSmallCapital("  Jugador"), Style.style(TextColor.color(WHITE), TextDecoration.BOLD)),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("Estado: "), TextColor.color(GRAY)))
                        .append(
                                (
                                        this.isOnline()
                                                ? Component.text(StringUtils.transformToSmallCapital("Online"), TextColor.color(169, 242, 157))
                                                : Component.text(StringUtils.transformToSmallCapital("Offline"), TextColor.color(242, 191, 191))
                                )
                        ),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("Discord: "), TextColor.color(GRAY)))
                        .append(
                                (
                                        this.discordManager.isLinked()
                                                ? Component.text(StringUtils.transformToSmallCapital(this.discordManager.getUsername()), TextColor.color(191, 242, 233))
                                                : Component.text(StringUtils.transformToSmallCapital("No vinculado"), TextColor.color(242, 191, 191))
                                )
                        ),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("Rango: "), TextColor.color(GRAY)))
                        .append(Component.text(
                                StringUtils.transformToSmallCapital(this.projectsManager.getBuilderRank() == ProjectsManager.BuilderRank.NONE ? "Visita" : (this.projectsManager.getBuilderRank() == ProjectsManager.BuilderRank.APPLIER ? "Postulante" : "Builder")),
                                TextColor.color(191, 242, 233)
                        )),

                Component.text(StringUtils.transformToSmallCapital("  Proyectos"), Style.style(TextColor.color(WHITE), TextDecoration.BOLD)),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("Activos: "), TextColor.color(GRAY)))
                        .append(Component.text(this.projectsManager.getProjects().size(), TextColor.color(191, 242, 233))),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("Terminados: "), TextColor.color(GRAY)))
                        .append(Component.text(this.projectsManager.getFinishedProjects(), TextColor.color(191, 242, 233))),

                Component.text(StringUtils.transformToSmallCapital("  Puntos"), Style.style(TextColor.color(WHITE), TextDecoration.BOLD))
        ));

        plugin.getCountriesRegistry().getCountries().stream()
                .map(country -> Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital(country.getDisplayName() + ": "), TextColor.color(GRAY)))
                        .append(Component.text(this.projectsManager.getPoints(country), TextColor.color(191, 242, 233))))
                .forEach(lines::add);

        lines.add(
                Component.text("◆")
                        .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                        .append(Component.text("◆"))
                        .color(TextColor.color(DARK_GRAY))
        );

        List<Component> finalList = new ArrayList<>();
        for (Component line : lines) {
            finalList.add(line.decoration(TextDecoration.ITALIC, false));
        }
        return finalList;
    }

    public List<Component> getLoreWithTitle() {
        // get lore and add name on top
        List<Component> lore = this.getLore();
        lore.add(0, Component.text(StringUtils.transformToSmallCapital(this.name), Style.style(TextDecoration.BOLD, TextColor.color(GREEN))));
        return lore;
    }
}
