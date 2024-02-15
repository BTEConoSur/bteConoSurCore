package pizzaaxx.bteconosur.projects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;
import org.locationtech.jts.geom.Polygon;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.chat.Chat;
import pizzaaxx.bteconosur.cities.City;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplay;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplayProvider;
import pizzaaxx.bteconosur.utils.SQLUtils;
import pizzaaxx.bteconosur.utils.StringUtils;
import pizzaaxx.bteconosur.utils.registry.RegistrableEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static pizzaaxx.bteconosur.utils.ChatUtils.DARK_GRAY;
import static pizzaaxx.bteconosur.utils.ChatUtils.GRAY;

public class Project implements RegistrableEntity<String>, ScoreboardDisplay, Chat {

    private final BTEConoSurPlugin plugin;
    private final ProjectEditor editor;

    private final String id;
    protected String name;
    private final Country country;
    private final int city;
    protected long pending;
    protected ProjectType type;
    protected int points;
    protected Set<UUID> members;
    protected UUID owner;
    protected org.locationtech.jts.geom.Polygon polygon;

    public Project(BTEConoSurPlugin plugin, String id) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        this.id = id;
        try (SQLResult result = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet("*, ST_AsWKT(region) AS region_wkt"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("id", "=", id)
                )
        ).retrieve()) {
            ResultSet set = result.getResultSet();
            if (!set.next()) {
                throw new SQLException("Project not found.");
            }
            this.name = set.getString("name");
            this.country = plugin.getCountriesRegistry().get(set.getString("country"));
            this.city = set.getInt("city");
            this.pending = set.getLong("pending");
            this.type = country.getProjectType(set.getString("type"));
            this.points = set.getInt("points");

            JsonNode membersNode = plugin.getJsonMapper().readTree(set.getString("members"));
            members = new HashSet<>();
            for (JsonNode memberNode : membersNode) {
                members.add(UUID.fromString(memberNode.asText()));
            }

            if (set.getBytes("owner") != null) this.owner = SQLUtils.uuidFromBytes(set.getBytes("owner"));

            this.polygon = SQLUtils.polygonFromWKT(set.getString("region_wkt"));

            this.editor = new ProjectEditor(plugin, this);
        }
    }

    public ProjectEditor getEditor() {
        return editor;
    }

    @Override
    public String getID() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public String getDisplayName() {
        if (this.name != null) {
            return name;
        }
        return id.toUpperCase();
    }

    @Override
    public void playerJoin(UUID uuid) {
        plugin.getChatHandler().sendMessage(
                this,
                Component.text("§a" + plugin.getPlayerRegistry().get(uuid).getName() + " ha entrado al chat.")
        );
    }

    @Override
    public void playerLeave(UUID uuid) {
        plugin.getChatHandler().sendMessage(
                this,
                Component.text("§a" + plugin.getPlayerRegistry().get(uuid).getName() + " ha salido del chat.")
        );
    }

    public int getPoints() {
        return points;
    }

    public long getPending() {
        return pending;
    }

    public boolean isPending() {
        return pending > 0;
    }

    public ProjectType getType() {
        return type;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public City getCity() {
        return country.get(city);
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public boolean isClaimed() {
        return owner != null;
    }

    @Override
    public void disconnected() {

    }

    @Override
    public Component getTitle() {
        return Component.text(StringUtils.transformToSmallCapital("Proyecto " + this.getDisplayName()), Style.style(TextColor.color(this.type.getColor().getRGB()), TextDecoration.BOLD));
    }

    @Override
    public List<Component> getLines() {
        List<Component> lines = new java.util.ArrayList<>(List.of(
                Component.text("◆")
                        .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                        .append(Component.text("◆"))
                        .color(TextColor.color(DARK_GRAY)),

                Component.text(StringUtils.transformToSmallCapital("  Proyecto"), Style.style(TextDecoration.BOLD)),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("ID: "), TextColor.color(GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital(id), TextColor.color(191, 242, 233))),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("Tipo: "), TextColor.color(GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital(type.getDisplayName()), TextColor.color(191, 242, 233))),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("Puntaje: "), TextColor.color(GRAY)))
                        .append(Component.text(points, TextColor.color(191, 242, 233))),

                Component.text(StringUtils.transformToSmallCapital("  Lugar"), Style.style(TextDecoration.BOLD)),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("País: "), TextColor.color(GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital(country.getDisplayName()), TextColor.color(191, 242, 233))),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("Ciudad: "), TextColor.color(GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital(this.getCity().getName()), TextColor.color(191, 242, 233)))
                ));

        if (this.isClaimed()) {
            OfflineServerPlayer owner = plugin.getPlayerRegistry().get(this.owner);
            lines.addAll(
                    List.of(
                            Component.text(StringUtils.transformToSmallCapital("  Personas"), Style.style(TextDecoration.BOLD)),
                            Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                                    .append(Component.text(StringUtils.transformToSmallCapital("Líder: "), TextColor.color(GRAY)))
                                    .append(Component.text(StringUtils.transformToSmallCapital(owner.getName()), TextColor.color(191, 242, 233))),
                            Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                                    .append(Component.text(StringUtils.transformToSmallCapital("Miembros: "), TextColor.color(GRAY)))
                                    .append(Component.text(members.size(), TextColor.color(191, 242, 233)))
                    )
            );
        }

        lines.add(
                Component.text("◆")
                        .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                        .append(Component.text("◆"))
                        .color(TextColor.color(DARK_GRAY))
        );

        return lines;
    }

    @Override
    public ScoreboardDisplayProvider getProvider() {
        return plugin.getProjectsRegistry();
    }

    @Override
    public String getProviderId() {
        return "project";
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    @Override
    public String getChatId() {
        return id;
    }

    @Override
    public void sendMessage(Chat origin, OnlineServerPlayer player, Component message) {
        Component component;
        if (origin != this) {
            // [§6PING§f] <nickname> <message>
            component = Component.text("[")
                    .append(Component.text("PING", GOLD))
                    .append(Component.text("] <", WHITE))
                    .append(player.getChatManager().getNickname())
                    .append(Component.text("> ", WHITE).decoration(TextDecoration.BOLD, false))
                    .append(message);
        } else {
            // <role in project> <nickname> <message>
            Component role;
            if (player.getUUID().equals(owner)) {
                role = Component.text("LÍDER", GOLD);
            } else if (members.contains(player.getUUID())) {
                role = Component.text("MIEMBRO", YELLOW);
            } else {
                role = Component.text("VISITA", WHITE);
            }

            component = Component.text("[")
                    .append(role)
                    .append(Component.text("] <", WHITE))
                    .append(player.getChatManager().getNickname())
                    .append(Component.text("> ", WHITE).decoration(TextDecoration.BOLD, false))
                    .append(message);
        }
        plugin.getChatHandler().sendMessage(this, component);
    }
}
