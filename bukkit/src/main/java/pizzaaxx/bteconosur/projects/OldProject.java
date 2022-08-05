package pizzaaxx.bteconosur.projects;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.Config;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.server.player.ProjectsManager;
import pizzaaxx.bteconosur.server.player.ScoreboardManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static pizzaaxx.bteconosur.BteConoSur.*;
import static pizzaaxx.bteconosur.methods.CodeGenerator.generateCode;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getPlayersInRegion;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class OldProject {
    private final OldCountry country;
    private final String id;
    private final Configuration config;
    private Difficulty difficulty;
    private Boolean pending;
    private final Set<UUID> members = new HashSet<>();
    private final Set<UUID> removedMembers = new HashSet<>();
    private UUID owner = null;
    private String name = null;
    private List<BlockVector2D> points;
    private Tag tag = null;
    private Tag oldTag = null;

    /**
     * The difficulty of a project, holds how many points a player should be given for finishing a project.
     */
    public enum Difficulty {
        FACIL, INTERMEDIO, DIFICIL;

        public int getPoints() {
            return Config.points.get(this);
        }
    }


    /**
     *
     * @param country The country to search in.
     * @param difficulty The specific difficulty to search.
     * @return A map containing a list of project IDs which match the country and difficulty, addressed by Tag.
     */
    public static Map<Tag, List<String>> getAvailableProjects(OldCountry country, Difficulty difficulty) {
        Map<Tag, List<String>> projects = new HashMap<>();

        for (Tag tag : Tag.values()) {
            String tagName = tag.toString().toLowerCase();

            projects.put(tag, new ArrayList<>());

            Configuration tags = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "projectTags/tags");
            if (tags.contains(tagName)) {
                for (String id : tags.getConfigurationSection(tagName).getStringList(country.getName())) {
                    OldProject project = new OldProject(id);
                    if (project.getOwner() == null && project.getDifficulty() == difficulty) {
                        projects.get(tag).add(id);
                    }
                }
            }
        }
        return projects;
    }

    public enum Tag {
        EDIFICIOS, DEPARTAMENTOS, CASAS, PARQUES, ESTABLECIMIENTOS, CARRETERAS, CENTROS_COMERCIALES
    }

    // CHECKER

    public static boolean projectExists(String id) {
        return new File(pluginFolder, "projects/" + id + ".yml").exists();
    }

    public static boolean isProjectAt(Location location) {
        Set<ProtectedRegion> regions = getWorldGuard().getRegionManager(mainWorld).getApplicableRegions(location).getRegions();

        for (ProtectedRegion region : regions) {
            if (region.getId().startsWith("project_")) {
                return true;
            }
        }
        return false;
    }

    public static String getProjectAt(Location loc) {
        Set<ProtectedRegion> regions = getWorldGuard().getRegionManager(mainWorld).getApplicableRegions(loc).getRegions();
        List<ProtectedRegion> projects = new ArrayList<>();

        regions.forEach(region -> {
            if (region.getId().startsWith("project_")) {
                projects.add(region);
            }
        });

        projects.sort(new RegionAreaComparator());

        return projects.get(0).getId().replace("project_", "");
    }

    // CONSTRUCTORS

    public OldProject(String id) {

        if (projectRegistry.exists(id)) {
            OldProject origin = projectRegistry.get(id);

            this.id = origin.id;
            this.country = origin.country;
            this.config = origin.config;
            this.difficulty = origin.difficulty;
            this.pending = origin.pending;
            if (!origin.members.isEmpty()) {
                this.members.addAll(origin.members);
            }
            if (origin.owner != null) {
                this.owner = origin.owner;
            }
            this.points = origin.points;
            if (origin.name != null) {
                this.name = origin.name;
            }
            if (origin.tag != null) {
                this.tag = origin.tag;
            }

        } else {
            this.id = id;

            this.config = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "projects/" + id);

            this.country = new OldCountry(config.getString("country"));

            difficulty = Difficulty.valueOf(config.getString("difficulty").toUpperCase());

            pending = config.getBoolean("pending");

            if (config.contains("members")) {
                config.getStringList("members").forEach(uuid -> members.add(UUID.fromString(uuid)));
            }

            if (config.contains("owner")) {
                owner = UUID.fromString(config.getString("owner"));
            }

            if (config.contains("name")) {
                name = config.getString("name");
            }

            if (config.contains("tag")) {
                tag = Tag.valueOf(config.getString("tag").toUpperCase());
            }

            points = getWorldGuard().getRegionManager(mainWorld).getRegion("project_" + id).getPoints();
        }

    }

    public OldProject(Location location) {
        this(getProjectAt(location));
    }

    public OldProject(BlockVector2D location) {
        this(getProjectAt(new Location(mainWorld, location.getX(), 100, location.getZ())));
    }

    public OldProject(OldCountry country, Difficulty difficulty, List<BlockVector2D> points) {
        this.country = country;
        this.difficulty = difficulty;
        this.points = points;
        this.pending = false;

        String rndmID = generateCode(6);

        RegionManager regions = getWorldGuard().getRegionContainer().get(mainWorld);
        while (regions.hasRegion("project_" + rndmID)) {
            rndmID = generateCode(6);
        }

        this.id = rndmID;

        this.config = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "projects/" + id);
    }

    // CLAIMED

    public boolean isClaimed() {
        return (this.owner != null || !this.members.isEmpty() || this.pending);
    }

    // --- GETTERS ---

    public OfflinePlayer getOwner() {
        if (owner != null) {
            return Bukkit.getOfflinePlayer(owner);
        }
        return null;
    }

    public OldCountry getCountry() {
        return country;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Tag getTag() {
        return tag;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return this.id;
    }

    public String getName(boolean formatted) {
        if (name != null) {
            return this.name;
        }
        if (formatted) {
            return this.id.toUpperCase();
        }
        return this.id;
    }

    public boolean isPending() {
        return this.pending;
    }

    public List<BlockVector2D> getPoints() {
        return this.points;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        if (points != null && points.size() > 1) {
            List<String> coords = new ArrayList<>();
            for (BlockVector2D point : this.points) {
                coords.add(new Coords2D(point).getLat() + "," + new Coords2D(point).getLon());
            }
            coords.add(new Coords2D(this.points.get(0)).getLat() + "," + new Coords2D(this.points.get(0)).getLon());
            return "https://open.mapquestapi.com/staticmap/v5/map?key=" + key + "&type=sat&shape=" + String.join("|", coords) + "|fill:6382DC50&size=1920,1080&imagetype=png";
        }
        return null;
    }

    public List<OfflinePlayer> getAllMembers() {
        List<OfflinePlayer> allMembers = new ArrayList<>(getMembers());
        if (owner != null) {
            allMembers.add(Bukkit.getOfflinePlayer(owner));
        }
        return allMembers;
    }

    public List<OfflinePlayer> getMembers() {
        List<OfflinePlayer> members = new ArrayList<>();
        this.members.forEach(uuid -> members.add(Bukkit.getOfflinePlayer(uuid)));
        return members;
    }

    public List<UUID> getMemberUUIDs() {

        List<UUID> list = new ArrayList<>();

        for (OfflinePlayer member: getMembers()) {

            list.add(member.getUniqueId());

        }

        return list;

    }

    public BlockVector2D getAverageCoordinate() {
        double minX = this.points.get(0).getX();
        double maxX = this.points.get(0).getX();
        double minZ = this.points.get(0).getZ();
        double maxZ = this.points.get(0).getZ();

        for (BlockVector2D point : this.points) {
            if (point.getX() > maxX) {
                maxX = point.getX();
            }
            if (point.getX() < minX) {
                minX = point.getX();
            }
            if (point.getZ() > maxZ) {
                maxZ = point.getZ();
            }
            if (point.getZ() < minZ) {
                minZ = point.getZ();
            }
        }

        Polygonal2DRegion region = new Polygonal2DRegion((World) new BukkitWorld(mainWorld), points, 100, 100);

        com.sk89q.worldedit.Vector vector = new Vector((minX + maxX) / 2, 100.0, (minZ + maxZ) / 2);
        if (region.contains(vector)) {
            return new BlockVector2D((minX + maxX) / 2, (minZ + maxZ) / 2);
        }
        return points.get(0);
    }

    // --- SETTERS ---

    public void setTag(Tag tag) {
        if (oldTag == tag) {
            oldTag = null;
        }
        if (this.tag != null) {
            oldTag = this.tag;
        }
        this.tag = tag;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public void setPoints(List<BlockVector2D> points) {
        this.points = points;
    }

    public void setOwner(OfflinePlayer player) {
        this.owner = player.getUniqueId();
    }

    // --- ADDERS ---
    public void addMember(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        members.add(uuid);
        removedMembers.remove(uuid);
    }

    // REMOVERS

    // INSTANTANEO
    public void removeMember(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        members.remove(uuid);
        removedMembers.add(uuid);
    }

    // DELETE PROJECT

    public void delete() {
        File file = new File(pluginFolder, "projects/" + id + ".yml");

        if (file.isFile()) {
            file.delete();
        }

        if (pending) {
            Configuration pending = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "pending_projects/pending");
            if (pending.contains(country.getName())) {
                List<String> pendingProjects = pending.getStringList(country.getName());
                pendingProjects.remove(id);
                pending.set(country.getName(), pendingProjects);
                pending.save();
            }
        }

        Configuration tags = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "projectTags/tags");
        if (tag != null) {
            String tagName = tag.toString().toLowerCase();
            if (tags.contains(tagName)) {
                ConfigurationSection tag = tags.getConfigurationSection(tagName);
                // COUNTRY
                List<String> country = tag.getStringList(this.country.getName());
                country.remove(id);
                tag.set(this.country.getName(), country);
                tags.set(tagName, tag);
            }
        }
        if (oldTag != null) {
            String tagName = oldTag.toString().toLowerCase();
            if (tags.contains(tagName)) {
                ConfigurationSection tag = tags.getConfigurationSection(tagName);
                // COUNTRY
                List<String> country = tag.getStringList(this.country.getName());
                country.remove(id);
                tag.set(this.country.getName(), country);
                tags.set(tagName, tag);
            }
            oldTag = null;
        }
        tags.save();

        if (owner != null) {
            ServerPlayer s = new ServerPlayer(owner);
            s.getProjectsManager().removeProject(this);
        }

        for (UUID uuid : members) {
            ServerPlayer s = new ServerPlayer(uuid);
            s.getProjectsManager().removeProject(this);
        }

        for (UUID uuid : removedMembers) {
            ServerPlayer s = new ServerPlayer(uuid);
            s.getProjectsManager().removeProject(this);
            removedMembers.clear();
        }

        Set<ScoreboardManager> insideManagers = new HashSet<>();

        for (Player p : getPlayersInRegion("project_" + id)) {
            ScoreboardManager manager = new ServerPlayer(p).getScoreboardManager();
            if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                insideManagers.add(manager);
            }
        }

        RegionManager manager = getWorldGuard().getRegionManager(mainWorld);
        if (manager.hasRegion("project_" + id)) {
            manager.removeRegion("project_" + id);
        }

        for (ScoreboardManager m : insideManagers) {
            m.update();
        }

        projectRegistry.remove(this.id);
    }

    public void transfer(OfflinePlayer target) {
        members.add(owner);
        members.remove(target.getUniqueId());
        owner = target.getUniqueId();
    }

    // UPLOAD PROJECT

    public void save() {

        config.set("difficulty", difficulty.toString().toLowerCase());

        config.set("pending", pending);
        Configuration pending = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "pending_projects/pending");
        List<String> pendingProjects = (pending.contains(country.getName()) ? pending.getStringList(country.getName()) : new ArrayList<>());
        if (this.pending) {
            if (!pendingProjects.contains(id)) {
                pendingProjects.add(id);
            }
        } else {
            pendingProjects.remove(id);
        }
        pending.set(country.getName(), pendingProjects);
        pending.save();

        config.set("country", country.getName());

        if (name != null) {
            config.set("name", name);
        }

        if (owner != null) {
            config.set("owner", owner.toString());
        } else {
            config.set("owner", null);
        }

        config.set("members", (members.isEmpty() ? null : members.stream().map(UUID::toString).collect(Collectors.toList())));

        if (tag != null) {
            config.set("tag", tag.toString().toLowerCase());
        }

        config.save();

        // WORLDGUARD

        RegionManager manager = getWorldGuard().getRegionManager(mainWorld);
        DefaultDomain domain = new DefaultDomain();
        for (OfflinePlayer member : getAllMembers()) {
            domain.addPlayer(member.getUniqueId());
            ServerPlayer s = new ServerPlayer(member);
            ProjectsManager pManager = s.getProjectsManager();
            if (!pManager.getAllProjects().contains(id)) {
                s.getProjectsManager().addProject(this);
                ScoreboardManager sManager = s.getScoreboardManager();
                if (sManager.getType() == ScoreboardManager.ScoreboardType.ME) {
                    sManager.update();
                }
            }
        }

        ProtectedRegion region;
        if (manager.hasRegion("project_" + id) && manager.getRegion("project_" + id).getPoints() == points) {
            region = manager.getRegion("project_" + id);

            if (this.pending) {
                region.setMembers(new DefaultDomain());
            } else {
                if (region.getMembers() != domain) {
                    region.setMembers(domain);
                }
            }

        } else {
            region = new ProtectedPolygonalRegion("project_" + id, points, -100, 8000);

            region.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
            region.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.MEMBERS);
            region.setPriority(1);

            FlagRegistry registry = getWorldGuard().getFlagRegistry();

            region.setFlag((StateFlag) registry.get("worldedit"), StateFlag.State.ALLOW);
            region.setFlag(registry.get("worldedit").getRegionGroupFlag(), RegionGroup.MEMBERS);

            if (!this.pending) {
                region.setMembers(domain);
            }

        }
        manager.addRegion(region);

        // REMOVED STUFF

        // TAG

        Configuration tags = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "projectTags/tags");
        if (oldTag != null) {
            String tagName = oldTag.toString().toLowerCase();
            if (tags.contains(tagName)) {
                ConfigurationSection tag = tags.getConfigurationSection(tagName);
                // COUNTRY
                List<String> country = tag.getStringList(this.country.getName());
                country.remove(id);
                tag.set(this.country.getName(), country);
                tags.set(tagName, tag);
            }
            oldTag = null;
        }
        if (tag != null) {
            String tagName = tag.toString().toLowerCase();
            if (tags.contains(tagName)) {
                ConfigurationSection tag = tags.getConfigurationSection(tagName);
                // COUNTRY
                List<String> country = tag.getStringList(this.country.getName());
                if (!country.contains(id)) {
                    country.add(id);
                    tag.set(this.country.getName(), country);
                    tags.set(tagName, tag);
                }
            }
        }
        tags.save();

        // MEMBERS

        for (UUID uuid : removedMembers) {
            ServerPlayer s = new ServerPlayer(uuid);
            s.getProjectsManager().removeProject(this);
        }
        removedMembers.clear();

        for (Player p : getPlayersInRegion("project_" + id)) {
            ScoreboardManager sManager = new ServerPlayer(p).getScoreboardManager();
            if (sManager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {
                sManager.update();
            }
        }

        projectRegistry.register(this);
    }

    // EMPTY PROJECT

    public void empty() {
        removedMembers.addAll(members);
        if (owner != null) {
            removedMembers.add(owner);
        }
        members.clear();
        owner = null;
    }
}
