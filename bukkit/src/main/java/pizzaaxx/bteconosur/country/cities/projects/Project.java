package pizzaaxx.bteconosur.country.cities.projects;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.Chat.ProjectChat;
import pizzaaxx.bteconosur.HelpMethods.RegionHelper;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.ProjectTypes.ProjectType;
import pizzaaxx.bteconosur.country.cities.City;
import pizzaaxx.bteconosur.country.cities.projects.ChangeAction.*;

import java.util.*;

public class Project {

    /**
     * Represents what kind of buildings this project contains.
     */
    public enum Tag {

        EDIFICIOS(":department_store:"),
        DEPARTAMENTOS(":hotel:"),
        CASAS(":homes:"),
        PARQUES(":deciduous_tree:"),
        ESTABLECIMIENTOS(":school:"),
        CARRETERAS(":motorway:"),
        CENTROS_COMERCIALES(":shopping_bags:");

        private final String emoji;

        Tag(String emoji) {
            this.emoji = emoji;
        }

        public String getEmoji() {
            return emoji;
        }

        public @NotNull String toFormattedString() {
            return super.toString().replace("_", " ");
        }

        public @NotNull List<Project> getAvailableProjects(Country country) {
            List<String> ids = country.getTags().getStringList(this.toString().toLowerCase());

            List<Project> result = new ArrayList<>();

            for (String id : ids) {

                GlobalProjectsManager manager = country.getPlugin().getProjectsManager();

                if (manager.exists(id)) {
                    result.add(manager.getFromId(id));
                }
            }

            return result;
        }

        public @NotNull List<Project> getAvailableProjects(Country country, Difficulty difficulty) {

            List<Project> available = this.getAvailableProjects(country);

            List<Project> result = new ArrayList<>();

            for (Project project : available) {
                if (project.getDifficulty() == difficulty) {
                    result.add(project);
                }
            }

            return result;
        }
    }

    private final BteConoSur plugin;
    private final Configuration config;
    private final String id;
    private final Country country;
    private final City city;

    public ProjectType type;

    public int points;

    public final Set<UUID> members = new HashSet<>();
    public UUID owner;

    public String name;
    public Tag tag;
    public boolean pending;

    public ProtectedRegion region;
    private final ProjectChat chat;
    private final ProjectsRegistry registry;

    /**
     * Loads a project from the server's storage. You should check first if the project exists.
     * @param registry The {@link ProjectsRegistry} this project belongs to.
     * @param id The id of this project.
     */
    public Project(@NotNull ProjectsRegistry registry, @NotNull String id) {

        this.registry = registry;

        this.id = id;

        this.plugin = registry.getPlugin();

        this.region = plugin.getRegionsManager().getRegion("project_" + id);

        this.city = registry.getCity();

        this.country = city.getCountry();

        config = new Configuration(plugin, "countries/" + country.getName() + "/cities/" + city.getName() + "/projects/" + id);

        type = country.getProjectType(config.getString("type"));

        points = config.getInt("points");

        if (config.contains("tag")) {
            tag = Tag.valueOf(config.getString("tag").toUpperCase());
        }

        name = config.getString("name");

        if (config.contains("members")) {
            for (String uuid : config.getStringList("members")) {
                members.add(UUID.fromString(uuid));
            }
        }

        if (config.contains("owner")) {
            owner = UUID.fromString(config.getString("owner"));
        }

        pending = config.getBoolean("pending");

        this.chat = plugin.getChatManager().getChat(this);

    }

    public Country getCountry() {
        return country;
    }

    public City getCity() {
        return city;
    }

    public String getId() {
        return id;
    }

    public ProjectType getType() {
        return type;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    /**
     *
     * @return All members, included the owner.
     */
    public Set<UUID> getAllMembers() {
        Set<UUID> members = new HashSet<>(this.members);
        members.add(owner);
        return members;
    }

    public UUID getOwner() {
        return owner;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return id.toUpperCase();
    }

    public BteConoSur getPlugin() {
        return plugin;
    }

    public Tag getTag() {
        return tag;
    }

    public boolean isPending() {
        return pending;
    }

    public Configuration getConfig() {
        return config;
    }

    public void updatePlayersScoreboard() {
        new UpdateScoreboardProjectAction(this).exec();
    }

    public SetNameProjectAction setName(String name) {
        return new SetNameProjectAction(this, name);
    }

    public SetTagProjectAction setTag(Tag tag) {
        return new SetTagProjectAction(this, this.tag, tag);
    }

    public TransferProjectAction transferTo(UUID target) {
        return new TransferProjectAction(this, this.owner, target);
    }

    public ClaimProjectAction claim(UUID target) {
        return new ClaimProjectAction(this, target, plugin);
    }

    public RedefineProjectAction redefine(List<BlockVector2D> regionPoints, Difficulty difficulty) {
        return new RedefineProjectAction(this, regionPoints, difficulty);
    }

    public SetPendingProjectAction setPending(boolean pending) {
        return new SetPendingProjectAction(this, pending);
    }

    public AddMembersProjectAction addMember(UUID uuid) {
        return new AddMembersProjectAction(this, uuid);
    }

    public RemoveMembersProjectAction removeMember(UUID uuid) {
        return new RemoveMembersProjectAction(this, uuid);
    }

    public ProjectChat getChat() {
        return chat;
    }

    public List<BlockVector2D> getPoints() {
        return region.getPoints();
    }

    public Coords2D getAverageCoordinate() {
        return RegionHelper.getAverageCoordinate(getPoints());
    }

    public boolean isClaimed() {
        return owner != null;
    }

    public boolean hasName() {
        return name != null;
    }

    public ProjectsRegistry getRegistry() {
        return registry;
    }

    public EmptyProjectAction empty() {
        return new EmptyProjectAction(this);
    }

    public Set<Player> getPlayersInside() {
        Set<Player> result = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            BlockVector2D vector = new BlockVector2D(player.getLocation().getX(), player.getLocation().getZ());
            if (region.contains(vector)) {
                result.add(player);
            }
        }
        return result;
    }

    /**
     * Saves this project to the server's storage.
     */
    public void saveToDisk() {

        config.set("type", type.getId());

        config.set("pending", pending);

        if (name != null) {
            config.set("name", name);
        }

        if (tag != null) {
            config.set("tag", tag.toString());
        }

        if (owner != null) {
            config.set("owner", owner.toString());
        }

        if (!members.isEmpty()) {
            List<String> memberUUIDs = new ArrayList<>();
            for (UUID uuid : members) {
                memberUUIDs.add(uuid.toString());
            }
            config.set("members", memberUUIDs);
        }
    }
}
