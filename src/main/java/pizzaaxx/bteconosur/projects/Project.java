package pizzaaxx.bteconosur.projects;

import com.sk89q.worldedit.BlockVector2D;
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
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.serverPlayer.ProjectsManager;
import pizzaaxx.bteconosur.serverPlayer.ScoreboardManager;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;
import pizzaaxx.bteconosur.yaml.Configuration;

import java.io.File;
import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.*;
import static pizzaaxx.bteconosur.methods.CodeGenerator.generateCode;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getPlayersInRegion;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class Project {
    private final OldCountry country;
    private final String id;
    private final Configuration config;
    private Difficulty difficulty;
    private Boolean pending;
    private final Set<UUID> members = new HashSet<>();
    private final Set<UUID> removedMembers = new HashSet<>();
    private UUID owner = null;
    private String name = null;
    private List<BlockVector2D> points = null;
    private Tag tag = null;
    private Tag oldTag = null;

    public enum Difficulty {
        FACIL, INTERMEDIO, DIFICIL;

        public int getPoints() {
            return Config.points.get(this);
        }
    }

    public static Map<Tag, List<String>> getAvailableProjects(OldCountry country, Difficulty difficulty) {
        Map<Tag, List<String>> projects = new HashMap<>();

        for (Tag tag : Tag.values()) {
            String tagName = tag.toString().toLowerCase();

            Configuration tags = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "projectTags/tags");
            if (tags.contains(tagName)) {
                for (String id : tags.getConfigurationSection(tagName).getStringList(country.getName())) {
                    Project project = new Project(id);
                    if (project.getOwner() == null && project.getDifficulty() == difficulty) {
                        projects.get(tag).add(id);
                    }
                }
            }
        }
        return projects;
    }

    public enum Tag {
        EDIFICIOS, DEPARTAMENTOS, CASAS, PARQUES, ESTABLECIMIENTOS, CARRETERAS, CENTROS_COMERCIALES;
    }

    // CHECKER

    public static boolean projectExists(String id) {
        return new File(pluginFolder, "projects/" + id + ".yml").exists();
    }

    public static boolean isProjectAt(Location location) {
        Set<ProtectedRegion> regions = getWorldGuard().getRegionManager(mainWorld).getApplicableRegions(location).getRegions();
        List<String> projects = new ArrayList<>();

        for (ProtectedRegion region : regions) {
            if (region.getId().startsWith("project_")) {
                return true;
            }
        }
        return false;
    }

    public static String getProjectAt(Location loc) {
        Set<ProtectedRegion> regions = getWorldGuard().getRegionManager(mainWorld).getApplicableRegions(loc).getRegions();
        List<String> projects = new ArrayList<>();

        regions.forEach(region -> {
            if (region.getId().startsWith("project_")) {
                projects.add(region.getId().replace("project_", ""));
            }
        });

        Collections.sort(projects);

        return projects.get(0);
    }

    // CONSTRUCTORS


    public Project(String id) {

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

    public Project(Location location) {
        this(getProjectAt(location));
    }

    public Project(BlockVector2D location) {
        this(getProjectAt(new Location(mainWorld, location.getX(), 100, location.getZ())));
    }

    public Project(OldCountry country, Difficulty difficulty, List<BlockVector2D> points) {
        this.country = country;
        this.difficulty = difficulty;
        this.points = points;

        String rndmID = generateCode(6);

        RegionManager regions = getWorldGuard().getRegionContainer().get(mainWorld);
        while (regions.hasRegion("project_" + rndmID)) {
            rndmID = generateCode(6);
        }

        this.id = rndmID;

        // TODO SEE IMPLEMENTATION OF THIS IN PROJECT REQUEST, PROBABLY DELETE ON REJECTION
        this.config = new Configuration(Bukkit.getPluginManager().getPlugin("bteConSur"), "projects/" + id);
    }

    // CLAIMED

    public boolean isClaimed() {
        return (this.owner != null || !this.members.isEmpty() || this.pending);
    }

    // --- GETTERS ---

    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(owner);
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
        List<OfflinePlayer> allMembers = new ArrayList<>();
        allMembers.addAll(getMembers());
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



        BlockVector2D coord = new BlockVector2D((minX + maxX) / 2, (minZ + maxZ) / 2);
        if (getWorldGuard().getRegionManager(mainWorld).getRegion("project_" + id).contains(coord)) {
            return coord;
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

    public void addPoint(BlockVector2D point) {
        this.points.add(point);
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

    }

    public void transfer(OfflinePlayer target) {
        members.add(owner);
        owner = target.getUniqueId();
    }

    // UPLOAD PROJECT

    public void save() {
        Configuration project = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "projects/" + id);

        project.set("difficulty", difficulty.toString().toLowerCase());

        project.set("pending", pending);
        if (pending) {
            Configuration pending = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "pending_projects/pending");
            List<String> pendingProjects = (pending.contains(country.getName()) ? pending.getStringList(country.getName()) : new ArrayList<>());
            pendingProjects.add(id);
            pending.set(country.getName(), pendingProjects);
            pending.save();
        }

        project.set("country", country);

        project.set("name", name);

        project.set("owner", owner);

        project.set("members", (members.isEmpty() ? null : members));

        project.set("tag", tag.toString().toLowerCase());

        project.save();

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

            if (pending) {
                region.setMembers(null);
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

            if (!pending) {
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
                    country.remove(id);
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
