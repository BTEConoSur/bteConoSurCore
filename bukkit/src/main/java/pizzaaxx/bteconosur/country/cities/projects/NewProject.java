package pizzaaxx.bteconosur.country.cities.projects;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.cities.City;
import pizzaaxx.bteconosur.country.cities.projects.ChangeAction.AddMembersProjectAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NewProject {

    /**
     * The difficulty of a project. A higher difficulty gives a higher amount of points.
     */
    public enum Difficulty {
        FACIL(15), INTERMEDIO(50), DIFICIL(100);

        private final int points;

        Difficulty(int points) {
            this.points = points;
        }

        public int getPoints() {
            return points;
        }
    }

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

        @Override
        public @NotNull String toString() {
            return super.toString().replace("_", "");
        }
    }

    private final BteConoSur plugin;
    private final Configuration config;
    private final String id;
    private final Country country;
    private final City city;
    private Difficulty difficulty;
    private final List<BlockVector2D> regionPoints;
    public final Set<UUID> members = new HashSet<>();
    private UUID owner;
    private String name;
    private Tag tag;
    private boolean pending;

    /**
     * Loads a project from the server's storage. You should check first if the project exists.
     * @param city The city this project belongs to.
     * @param id The id of this project.
     * @param plugin The plugin running. Needed for Configuration loading.
     * @param manager WorldGuard's region manager.
     */
    public NewProject(@NotNull City city, @NotNull String id, @NotNull Plugin plugin, @NotNull RegionManager manager) {
        this.id = id;

        this.city = city;

        this.country = city.getCountry();

        config = new Configuration(plugin, "countries/" + country.getName() + "/cities/" + city.getName() + "/projects/" + id);

        difficulty = Difficulty.valueOf(config.getString("difficulty").toUpperCase());

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

        regionPoints = manager.getRegion("project_" + id).getPoints();

        pending = config.getBoolean("pending");

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

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public UUID getOwner() {
        return owner;
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

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public AddMembersProjectAction addMembers(UUID... uuid) {
        return new AddMembersProjectAction(this, uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public void saveToDisk() {

    }
}
