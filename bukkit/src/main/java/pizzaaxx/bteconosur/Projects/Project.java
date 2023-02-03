package pizzaaxx.bteconosur.Projects;

import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.PrefixHolder;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Inventory.ItemBuilder;
import pizzaaxx.bteconosur.Projects.Actions.*;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Project {

    private final BTEConoSur plugin;

    private final String id;
    private String displayName;
    private final Country country;
    private final Set<String> cities;
    private boolean pending;
    private final ProjectType type;
    private int points;
    public Set<UUID> members;
    public UUID owner;
    private ProjectTag tag;
    private final ProtectedPolygonalRegion region;

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

            this.pending = set.getBoolean("pending");

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

    public Country getCountry() {
        return country;
    }

    public Set<City> getCities() {
        Set<City> cities = new HashSet<>();
        for (String cityName : this.cities) {
            cities.add(plugin.getCityManager().get(cityName));
        }
        return cities;
    }

    public boolean isPending() {
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

    public ProjectTag getTag() {
        return tag;
    }

    public ProtectedPolygonalRegion getRegion() {
        return region;
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
        return ItemBuilder.of(Material.MAP)
                .name("§aProyecto " + this.getDisplayName())
                .lore(lore)
                .build();
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

    public void update() throws SQLException, IOException {
        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "name",
                        "pending",
                        "points",
                        "owner",
                        "tag",
                        "members"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", id
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.displayName = set.getString("name");
            this.pending = set.getBoolean("pending");
            this.points = set.getInt("points");
            this.owner = plugin.getSqlManager().getUUID(set, "owner");
            String tag = set.getString("tag");
            this.tag = tag == null ? null : ProjectTag.valueOf(tag);
            this.members = new HashSet<>();
            Set<String> uuids = plugin.getJSONMapper().readValue(set.getString("members"), HashSet.class);
            for (String uuid : uuids) {
                members.add(UUID.fromString(uuid));
            }

        } else {
            throw new IllegalArgumentException();
        }
    }

    public AddMemberProjectAction addMember(UUID uuid) {
        return new AddMemberProjectAction(plugin, this, uuid);
    }

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
}
