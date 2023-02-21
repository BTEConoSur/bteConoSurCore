package pizzaaxx.bteconosur.Projects;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.PrefixHolder;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Inventory.ItemBuilder;
import pizzaaxx.bteconosur.Projects.Actions.*;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.JSONParsable;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderExpression;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderSet;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Showcases.Showcase;
import pizzaaxx.bteconosur.Utils.RegionUtils;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class Project implements JSONParsable, Prefixable {

    public static int MAX_PROJECTS_PER_PLAYER = 15;

    private final BTEConoSur plugin;

    private final String id;
    private String displayName;
    private final Country country;
    private Set<String> cities;
    private Long pending;
    private final ProjectType type;
    private int points;
    public Set<UUID> members;
    public UUID owner;
    private ProjectTag tag;
    private final ProtectedPolygonalRegion region;
    private final Set<UUID> requests;
    private final List<Showcase> showcases;

    public Project(@NotNull BTEConoSur plugin, String id) throws SQLException, IOException {
        this.plugin = plugin;
        this.id = id;
        this.region = (ProtectedPolygonalRegion) plugin.getRegionManager().getRegion("project_" + id);

        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "*"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", id
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.displayName = set.getString("name");

            this.country = plugin.getCountryManager().get(set.getString("country"));

            this.cities = plugin.getJSONMapper().readValue(set.getString("cities"), HashSet.class);

            Timestamp timestamp = set.getTimestamp("pending");

            this.pending = (timestamp == null ? null : timestamp.getTime());

            this.type = country.getProjectType(set.getString("type"));

            this.points = set.getInt("points");

            this.members = new HashSet<>();
            Set<String> uuids = plugin.getJSONMapper().readValue(set.getString("members"), HashSet.class);
            for (String uuid : uuids) {
                members.add(UUID.fromString(uuid));
            }

            this.owner = plugin.getSqlManager().getUUID(set, "owner");

            String tag = set.getString("tag");

            this.tag = tag == null ? null : ProjectTag.valueOf(tag);

            ResultSet requestsSet = plugin.getSqlManager().select(
                    "project_join_requests",
                    new SQLColumnSet(
                            "target"
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "project_id", "=", this.id
                            )
                    )
            ).retrieve();

            requests = new HashSet<>();

            while (set.next()) {
                requests.add(plugin.getSqlManager().getUUID(set, "target"));
            }

            this.showcases = new ArrayList<>();
            ResultSet showcaseSet = plugin.getSqlManager().select(
                    "showcases",
                    new SQLColumnSet(
                            "message_id"
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "project_id", "=", this.id
                            )
                    ),
                    new SQLOrderSet(
                            new SQLOrderExpression(
                                    "date", SQLOrderExpression.Order.ASC
                            )
                    )
            ).retrieve();

            while (showcaseSet.next()) {
                this.showcases.add(new Showcase(showcaseSet.getString("message_id"), this.id));
            }

        } else {
            throw new IllegalArgumentException();
        }
    }

    public BTEConoSur getPlugin() {
        return plugin;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        if (displayName ==  null) {
            return id.toUpperCase();
        }
        return displayName;
    }

    public String getName() {
        return displayName;
    }

    public Country getCountry() {
        return country;
    }

    public List<Showcase> getShowcases() {
        return showcases;
    }

    public void addShowcase(String messageID, long time) throws SQLException {
        plugin.getSqlManager().insert(
                "showcases",
                new SQLValuesSet(
                        new SQLValue(
                                "message_id", messageID
                        ),
                        new SQLValue(
                                "project_id", this.id
                        ),
                        new SQLValue(
                                "cities", cities
                        ),
                        new SQLValue(
                                "date", new Date(time)
                        )
                )
        ).execute();
        this.showcases.add(new Showcase(messageID, this.id));
    }

    public Set<City> getCities() {
        Set<City> cities = new HashSet<>();
        for (String cityName : this.cities) {
            cities.add(plugin.getCityManager().get(cityName));
        }
        return cities;
    }

    public boolean isPending() {
        return pending != null;
    }

    public Long getPending() {
        return pending;
    }

    public ProjectType getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public Set<UUID> getAllMembers() {
        Set<UUID> result = new HashSet<>(members);
        result.add(owner);
        return result;
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isClaimed() {
        return owner != null;
    }

    public boolean isMember(UUID uuid) {
        return (owner.equals(uuid) || members.contains(uuid));
    }

    public boolean isFull() {
        return members.size() >= type.getMaxMembers();
    }

    public ProjectTag getTag() {
        return tag;
    }

    public ProtectedPolygonalRegion getRegion() {
        return region;
    }

    public Location getTeleportLocation() {
        BlockVector2D averageCoord = RegionUtils.getAveragePoint(region);
        return plugin.getWorld().getHighestBlockAt(averageCoord.getBlockX(), averageCoord.getBlockZ()).getLocation();
    }

    public ItemStack getItem() {
        List<String> lore = new ArrayList<>();
        lore.add("§f• ID: §7" + id);
        lore.add("§f• Tipo: §7" + this.getType().getDisplayName());
        if (this.isClaimed()) {
            lore.add("§f• Líder: §7" + plugin.getPlayerRegistry().get(this.getOwner()).getName());
            if (this.getMembers().size() > 0) {
                List<String> memberNames = new ArrayList<>();
                for (UUID memberUUID : this.getMembers()) {
                    memberNames.add(plugin.getPlayerRegistry().get(memberUUID).getName());
                }
                lore.add("§f• Miembros: §7" + String.join(", ", memberNames));
            }
        }
        if (tag != null) {
            return ItemBuilder.head(
                    tag.getHeadValue(),
                    "§aProyecto " + this.getDisplayName(),
                    lore
            );
        } else {
            return ItemBuilder.of(Material.MAP)
                    .name("§aProyecto " + this.getDisplayName())
                    .lore(lore)
                    .build();
        }
    }

    @Override
    public String getJSON(boolean insideJSON) {
        return (insideJSON?"\"":"'") + this.id + (insideJSON?"\"":"'");
    }

    @Override
    public String getPrefix() {
        return "§f[§dPROYECTO§f] §7>> §f";
    }

    public enum ProjectRole implements PrefixHolder {
        LEADER("§f[§6LÍDER§f] §r", ""),
        MEMBER("§f[§9MIEMBRO§f] §r", ""),
        GUEST("§f[§7INVITADO§f] §r", "");

        private final String prefix;
        private final String discordPrefix;

        ProjectRole(String prefix, String discordPrefix) {
            this.prefix = prefix;
            this.discordPrefix = discordPrefix;
        }

        @Override
        public String getPrefix() {
            return prefix;
        }

        @Override
        public String getDiscordPrefix() {
            return discordPrefix;
        }
    }

    public ProjectRole getProjectRole(UUID uuid) {
        if (owner.equals(uuid)) {
            return ProjectRole.LEADER;
        } else if (members.contains(uuid)) {
            return ProjectRole.MEMBER;
        } else {
            return ProjectRole.GUEST;
        }
    }

    public Set<UUID> getRequests() {
        return requests;
    }

    public boolean request(UUID target) throws SQLException {
        if (!requests.contains(target)) {
            requests.add(target);
            plugin.getSqlManager().insert(
                    "project_join_requests",
                    new SQLValuesSet(
                            new SQLValue(
                                    "project_id", this.id
                            ),
                            new SQLValue(
                                    "target", target
                            )
                    )
            ).execute();
            return true;
        }
        return false;
    }

    public boolean setTag(@Nullable ProjectTag tag) throws SQLException {
        if (tag != this.tag) {
            this.tag = tag;
            plugin.getSqlManager().update(
                    "projects",
                    new SQLValuesSet(
                            new SQLValue(
                                    "tag", tag
                            )
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "id", "=", this.id
                            )
                    )
            ).execute();
            return true;
        }
        return false;
    }

    public void update() throws SQLException, IOException {
        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "name",
                        "pending",
                        "points",
                        "owner",
                        "tag",
                        "members",
                        "cities"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", id
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.displayName = set.getString("name");

            Timestamp timestamp = set.getTimestamp("pending");

            this.pending = (timestamp == null ? null : timestamp.getTime());

            this.points = set.getInt("points");
            this.owner = plugin.getSqlManager().getUUID(set, "owner");
            String tag = set.getString("tag");
            this.tag = tag == null ? null : ProjectTag.valueOf(tag);
            this.members = new HashSet<>();
            Set<String> uuids = plugin.getJSONMapper().readValue(set.getString("members"), HashSet.class);
            for (String uuid : uuids) {
                members.add(UUID.fromString(uuid));
            }
            this.cities = plugin.getJSONMapper().readValue(set.getString("cities"), HashSet.class);

        } else {
            throw new IllegalArgumentException();
        }
    }

    public AddMemberProjectAction addMember(UUID uuid) {
        return new AddMemberProjectAction(plugin, this, uuid);
    }

    @CheckReturnValue
    public ClaimProjectAction claim(UUID owner) {
        return new ClaimProjectAction(plugin, this, owner);
    }

    public RemoveMemberProjectAction removeMember(UUID uuid) {
        return new RemoveMemberProjectAction(plugin, this, uuid);
    }

    public SetDisplayNameProjectAction setDisplayName(String name) {
        return new SetDisplayNameProjectAction(plugin, this, name);
    }

    public SetPendingProjectAction setPending(boolean pending) {
        return new SetPendingProjectAction(plugin, this, pending);
    }

    public TransferProjectAction transfer(UUID uuid) {
        return new TransferProjectAction(plugin, this, uuid);
    }

    public MemberLeaveProjectAction memberLeave(UUID uuid) {
        return new MemberLeaveProjectAction(plugin, this, uuid);
    }

    public EmptyProjectAction emptyProject() {
        return new EmptyProjectAction(plugin, this);
    }

    public ReviewProjectAction review(ReviewProjectAction.ReviewAction reviewAction, UUID moderator) {
        return new ReviewProjectAction(
                plugin,
                reviewAction,
                this,
                moderator
        );
    }

    @Override
    public boolean equals(Object obj) {

        if (getClass() != obj.getClass()) {
            return false;
        }

        Project project = (Project) obj;
        return this.id.equals(project.id);
    }
}
