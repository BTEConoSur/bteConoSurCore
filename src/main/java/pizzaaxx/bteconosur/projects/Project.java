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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.Config;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.player.data.PlayerData;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;
import pizzaaxx.bteconosur.yaml.Configuration;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.io.File;
import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.*;
import static pizzaaxx.bteconosur.methods.CodeGenerator.generateCode;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class Project {
    private final Country country;
    private Difficulty difficulty;
    private Boolean pending;
    private final String id;
    private final Configuration config;
    private final Set<UUID> members = new HashSet<>();
    private UUID owner = null;
    private String name = null;
    private List<BlockVector2D> points = null;
    private Tag tag = null;

    public enum Difficulty {
        FACIL, INTERMEDIO, DIFICIL;

        public int getPoints() {
            return Config.points.get(this);
        }
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

    // CONSTRUCTORS

    public Project(String id) {

        if (new File(pluginFolder, "projects/" + id + ".yml").exists()) {

            this.id = id;

            config = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "projects/" + id);

            country = new Country(config.getString("country"));

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

    public Project(Location location) {
        Set<ProtectedRegion> regions = getWorldGuard().getRegionManager(mainWorld).getApplicableRegions(location).getRegions();
        List<String> projects = new ArrayList<>();

        regions.forEach(region -> {
            if (region.getId().startsWith("project_")) {
                projects.add(region.getId().replace("project_", ""));
            }
        });
        Collections.sort(projects);

        this(projects.get(0));
    }

    public Project(BlockVector2D location) {
        this(new Location(mainWorld, location.getX(), 100, location.getZ()));
    }

    public Project(Country country, Difficulty difficulty, List<BlockVector2D> points) {
        this.country = country;
        this.difficulty = difficulty;

        String rndmID = generateCode(6);
        RegionManager regions = getWorldGuard().getRegionContainer().get(mainWorld);

        while (regions.hasRegion("project_" + rndmID)) {
            rndmID = generateCode(6);
        }
        this.id = rndmID;

        this.points = points;
    }

    // CLAIMED

    public boolean isClaimed() {
        return (this.owner != null || !this.members.isEmpty() || this.pending);
    }


    // --- GETTERS ---

    public Country getCountry() {
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
        members.forEach(uuid -> allMembers.add(Bukkit.getOfflinePlayer(uuid)));
        if (owner != null) {
            allMembers.add(Bukkit.getOfflinePlayer(owner));
        }
        if (allMembers.isEmpty()) {
            return null;
        }
        return allMembers;
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

    // --- ADDERS ---
    public void addMember(OfflinePlayer player) {
        members.add(player.getUniqueId());
    }

    public void addPoint(BlockVector2D point) {
        this.points.add(point);
    }

    // REMOVERS

    public void removeMember(OfflinePlayer player) {
        this.membersOld.remove(player);
        this.removedMembers.add(player);
        if (this.membersOld.size() == 0) {
            this.membersOld = null;
        }
    }

    // DELETE PROJECT

    public void delete() {
        if (this.id != null) {
            File file = new File(pluginFolder, "projects/project_" + this.id + ".yml");

            if (file.isFile()) {
                file.delete();
            }

            YamlManager pending = new YamlManager(pluginFolder, "pending_projects/pending.yml");

            pending.removeFromList("pending", this.id);

            pending.write();

            // TAGS
            YamlManager tags = new YamlManager(pluginFolder, "projectTags/tags.yml");

            if (oldTag != null) {
                tags.removeFromList(new Country(this.oldCountry).getAbbreviation() + "_" + this.oldTag, this.id);
            }

            if (tag != null) {
                tags.removeFromList(new Country(this.oldCountry).getAbbreviation() + "_" + this.tag, this.id);
            }

            tags.write();

            if (getAllMembers() != null) {
                for (OfflinePlayer p : getAllMembers()) {
                    PlayerData playerData = new PlayerData(p);

                    playerData.removeFromList("projects", this.id);

                    playerData.save();

                    ServerPlayer s = new ServerPlayer(p);
                    s.updateRanks();
                    if (s.getScoreboard().equals("me")) {
                        s.updateScoreboard();
                    }

                    if (s.getChat().getName().replace("project_", "").equals(this.id)) {
                        s.setChat("global");
                    }
                    if (s.getDefaultChat().getName().replace("project_", "").equals(this.id)) {
                        s.setDefaultChat("global");
                    }
                }
            }

            if (this.removedMembers != null) {
                for (OfflinePlayer member : this.removedMembers) {
                    PlayerData playerData = new PlayerData(member);

                    playerData.removeFromList("projects", this.id);

                    playerData.save();

                    ServerPlayer s = new ServerPlayer(member);
                    s.updateRanks();
                    if (s.getScoreboard().equals("me")) {
                        s.updateScoreboard();
                    }

                    if (s.getChat().getName().replace("project_", "").equals(this.id)) {
                        s.setChat("global");
                    }
                    if (s.getDefaultChat().getName().replace("project_", "").equals(this.id)) {
                        s.setDefaultChat("global");
                    }
                }
            }

            List<ServerPlayer> inside = new ArrayList<>();
            RegionManager regions = getWorldGuard().getRegionContainer().get(mainWorld);
            for (Player player : Bukkit.getOnlinePlayers()) {
                ServerPlayer p = new ServerPlayer(player);
                if (regions.getApplicableRegions(player.getLocation()).getRegions().contains(regions.getRegion("project_" + this.id)) && p.getScoreboard().equals("project")) {
                    inside.add(p);
                }
            }

            if (regions.hasRegion("project_" + this.id)) {
                regions.removeRegion("project_" + this.id);
            }

            for (ServerPlayer s : inside) {
                s.updateScoreboard();
            }
        }
    }

    // UPLOAD PROJECT

    public void save() {
        Configuration project = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "projects/" + id);

        project.set("difficulty", difficulty.toString().toLowerCase());

        project.set("pending", pending);

        project.set("country", country);

        project.set("name", name);

        project.set("owner", owner);

        project.set("members", (members.isEmpty() ? null : members));

        project.save();
    }

    public void upload() {

        YamlManager project = new YamlManager(pluginFolder, "projects/project_" + this.id + ".yml");

        if (this.difficulty == null) {
            project.deleteValue("difficulty");
        } else {
            project.setValue("difficulty", this.difficulty);
        }

        if (this.name == null) {
            project.deleteValue("name");
        } else {
            project.setValue("name", this.name);
        }


        YamlManager tags = new YamlManager(pluginFolder, "projectTags/tags.yml");

        if (this.tag == null) {
            project.deleteValue("tag");

            if (oldTag != null) {
                tags.removeFromList(new Country(this.oldCountry).getAbbreviation() + "_" + this.oldTag, this.id);
            }
        } else {
            project.setValue("tag", this.tag);

            if (oldTag != null) {
               tags.removeFromList(new Country(this.oldCountry).getAbbreviation() + "_" + this.oldTag, this.id);
            }

            tags.addToList(new Country(this.oldCountry).getAbbreviation() + "_" + this.tag, this.id, false);
        }

        tags.write();

        if (!this.pending) {
            project.setValue("pending", false);

            YamlManager pending = new YamlManager(pluginFolder, "pending_projects/pending.yml");

            pending.removeFromList("pending", this.id);

            pending.write();
        } else {
            project.setValue("pending", true);

            YamlManager pending = new YamlManager(pluginFolder, "pending_projects/pending.yml");

            pending.addToList("pending", this.id, false);

            pending.write();
        }

        if (this.ownerOld == null) {
            project.deleteValue("owner");
        } else {
            project.setValue("owner", this.ownerOld.getUniqueId().toString());
            PlayerData owner = new PlayerData(this.ownerOld);
            owner.addToList("projects", this.id, false);
            owner.save();
            ServerPlayer s = new ServerPlayer(this.ownerOld);
            s.updateRanks();
            if (s.getScoreboard().equals("me")) {
                s.updateScoreboard();
            }
        }

        if (this.membersOld == null) {
            project.deleteValue("members");
        } else {
            for (OfflinePlayer p : this.membersOld) {
                project.addToList("members", p.getUniqueId().toString(), false);
                PlayerData member = new PlayerData(p);
                member.addToList("projects", this.id, false);
                member.save();
                ServerPlayer s = new ServerPlayer(p);
                s.updateRanks();
                if (s.getScoreboard().equals("me")) {
                    s.updateScoreboard();
                }
            }
        }

        if (this.removedMembers != null) {
            for (OfflinePlayer member : this.removedMembers) {
                PlayerData playerData = new PlayerData(member);

                playerData.removeFromList("projects", this.id);

                playerData.save();

                ServerPlayer s = new ServerPlayer(member);
                s.updateRanks();
                if (s.getScoreboard().equals("me")) {
                    s.updateScoreboard();
                }

                if (s.getChat().getName().replace("project_", "").equals(this.id)) {
                    s.setChat("global");
                }
                if (s.getDefaultChat().getName().replace("project_", "").equals(this.id)) {
                    s.setDefaultChat("global");
                }
            }
        }

        if (this.oldCountry == null) {
            project.deleteValue("country");
        } else {
            project.setValue("country", this.oldCountry);
        }

        project.write();

        // UPDATE WORLDGUARD

        RegionManager regions = getWorldGuard().getRegionContainer().get(mainWorld);

        ProtectedRegion region = null;
        if (regions.hasRegion("project_" + this.id)) {
            region = regions.getRegion("project_" + this.id);
            if (region.getPoints() != this.points) {
                region = new ProtectedPolygonalRegion("project_" + this.id, this.points, -8000, 8000);
            }
        } else {
            region = new ProtectedPolygonalRegion("project_" + this.id, this.points, -8000, 8000);
        }

        region.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
        region.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.MEMBERS);
        region.setPriority(1);

        FlagRegistry registry = getWorldGuard().getFlagRegistry();

        region.setFlag((StateFlag) registry.get("worldedit"), StateFlag.State.ALLOW);
        region.setFlag(registry.get("worldedit").getRegionGroupFlag(), RegionGroup.MEMBERS);

        DefaultDomain regionMembers = new DefaultDomain();

        if (!this.pending) {
            if (getAllMembers() != null) {
                for (OfflinePlayer member : getAllMembers()) {
                    regionMembers.addPlayer(member.getUniqueId());
                }
            }
        }
        region.setMembers(regionMembers);


        if (regions.hasRegion("project_" + this.id)) {
            if (region != regions.getRegion("project_" + this.id)) {
                regions.addRegion(region);
            }
        } else {
            regions.addRegion(region);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            ServerPlayer p = new ServerPlayer(player);
            if (regions.getApplicableRegions(player.getLocation()).getRegions().contains(regions.getRegion("project_" + this.id)) && p.getScoreboard().equals("project")) {
                p.updateScoreboard();
            }
        }
    }

    // EMPTY PROJECT

    public void empty() {
        if (this.membersOld != null) {
            this.removedMembers.addAll(this.membersOld);
        }
        if (this.ownerOld != null) {
            this.removedMembers.add(this.ownerOld);
        }
        this.membersOld = null;
        this.ownerOld = null;
    }
}
