package pizzaaxx.bteconosur.projects;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
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
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.playerData.PlayerData;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.io.*;
import java.util.*;

import static pizzaaxx.bteconosur.bteConoSur.mainWorld;
import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.methods.codeGenerator.generateCode;
import static pizzaaxx.bteconosur.worldguard.worldguard.getWorldGuard;
import static pizzaaxx.bteconosur.yaml.YamlManager.getYamlData;

public class Project {
    private String country = null;
    private String difficulty = null;
    private OfflinePlayer owner = null;
    private Set<OfflinePlayer> members = null;
    private String name = null;
    private Boolean pending = false;
    private List<BlockVector2D> points = null;
    private String id = null;
    private String tag = null;
    private String oldTag = null;

    // CONSTRUCTORS

    public Project(String id) throws Exception {
        File file = new File(pluginFolder, "projects/project_" + id + ".yml");

        if (file.isFile()) {
            this.id = id;
            Map<String, Object> projectData = getYamlData(file, "");

            if (projectData.containsKey("name")) {
                this.name = (String) projectData.get("name");
            }
            if (projectData.containsKey("pending")) {
                this.pending = (Boolean) projectData.get("pending");
            }
            if (projectData.containsKey("difficulty")) {
                this.difficulty = (String) projectData.get("difficulty");
            }
            if (projectData.containsKey("owner")) {
                this.owner = Bukkit.getOfflinePlayer(UUID.fromString((String) projectData.get("owner")));
            }
            if (projectData.containsKey("country")) {
                this.country = (String) projectData.get("country");
            }
            if (projectData.containsKey("tag")) {
                this.tag = (String) projectData.get("tag");
            }

            if (projectData.containsKey("members")) {
                this.members = new HashSet<>();
                for (String uuid : (List<String>) projectData.get("members")) {
                    this.members.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
                }
            }

            RegionManager manager = getWorldGuard().getRegionContainer().get(mainWorld);
            if (manager.hasRegion("project_" + id)) {
                this.points = manager.getRegion("project_" + id).getPoints();
            }
        } else {
            throw new Exception();
        }
    }

    public Project(String country, String difficulty, List<BlockVector2D> points) {
        this.country = country;
        this.generateID();
        this.points = points;
        this.difficulty = difficulty;
    }

    public Project(Location loc) throws Exception {
        RegionManager regions = getWorldGuard().getRegionContainer().get(mainWorld);
        ApplicableRegionSet regionSet = regions.getApplicableRegions(loc);
        Set<ProtectedRegion> regionList = regionSet.getRegions();
        String id = null;
        for (ProtectedRegion r : regionList) {
            if (r.getId().startsWith("project_")) {
                id = r.getId().replace("project_", "");
                break;
            }
        }
        if (id != null) {
            File file = new File(Bukkit.getServer().getPluginManager().getPlugin("bteConoSur").getDataFolder(), "projects/project_" + id + ".yml");

            this.id = id;

            Map<String, Object> projectData = getYamlData(file, "");
            if (projectData.containsKey("name")) {
                this.name = (String) projectData.get("name");
            }
            if (projectData.containsKey("pending")) {
                this.pending = (Boolean) projectData.get("pending");
            }
            if (projectData.containsKey("difficulty")) {
                this.difficulty = (String) projectData.get("difficulty");
            }
            if (projectData.containsKey("owner")) {
                this.owner = Bukkit.getOfflinePlayer((UUID.fromString((String) projectData.get("owner"))));
            }
            if (projectData.containsKey("country")) {
                this.country = (String) projectData.get("country");
            }
            if (projectData.containsKey("tag")) {
                this.tag = (String) projectData.get("tag");
            }

            if (projectData.containsKey("members")) {
                this.members = new HashSet<>();
                for (String uuid : (List<String>) projectData.get("members")) {
                    this.members.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
                }
            }

            RegionManager manager = getWorldGuard().getRegionContainer().get(mainWorld);
            if (manager.hasRegion("project_" + id)) {
                this.points = manager.getRegion("project_" + id).getPoints();

            }
        } else {
            throw new Exception();
        }
    }

    // CLAIMED

    public boolean isClaimed() {
        return (this.owner != null || this.members != null || this.pending);
    }


    // --- GETTERS ---

    public String getCountry() {
        return country;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public OfflinePlayer getOwner() {
        return this.owner;
    }

    public Set<OfflinePlayer> getMembers() {
        return this.members;
    }

    public String getTag() {
        return this.tag;
    }

    public String getName() {
        if (this.name != null) {
            return this.name;
        } else if (this.id != null) {
            return this.id;
        }
        return null;

    }

    public String getName(boolean formatted) {
        if (this.name != null) {
            return this.name;
        } else if (this.id != null) {
            if (formatted) {
                return this.id.toUpperCase();
            }
            return this.id;
        }
        return null;

    }

    public Boolean isPending() {
        return this.pending;
    }

    public List<BlockVector2D> getPoints() {
        return this.points;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        if (this.points != null && this.points.size() > 1) {
            List<String> coords = new ArrayList<>();
            for (BlockVector2D point : this.points) {
                coords.add(new Coords2D(point).getLat() + "," + new Coords2D(point).getLon());
            }
            coords.add(new Coords2D(this.points.get(0)).getLat() + "," + new Coords2D(this.points.get(0)).getLon());
            return "https://open.mapquestapi.com/staticmap/v5/map?key=iZIDpeEGELwG16q3zZGOMPEPsbM6uqxi&shape=" + String.join("|", coords) + "|fill:6382DC50&size=1280,720&imagetype=png";
        }
        return null;
    }

    public List<OfflinePlayer> getAllMembers() {
        List<OfflinePlayer> allMembers;
        if (this.members != null) {
            allMembers = new ArrayList<>(this.members);
        } else {
            allMembers = new ArrayList<>();
        }

        if (this.owner != null) {
            allMembers.add(this.owner);
        }
        if (allMembers.size() == 0) {
            return null;
        }
        return allMembers;

    }

    public BlockVector2D getAverageCoordinate() {
        Double minX = this.points.get(0).getX();
        Double maxX = this.points.get(0).getX();
        Double minZ = this.points.get(0).getZ();
        Double maxZ = this.points.get(0).getZ();

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

        return new BlockVector2D((minX + maxX) / 2, (minZ + maxZ) / 2);
    }

    // --- SETTERS ---

    public void setTag(String tag) {
        if (this.tag != tag) {
            this.oldTag = this.tag;
            this.tag = tag;
        }
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setOwner(OfflinePlayer owner) {
        this.owner = owner;
    }

    public void setMembers(Set<OfflinePlayer> members) {
        this.members = members;
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

    public void generateID() {
        if (this.id == null) {
            String rndmID = generateCode(6);
            RegionManager regions = getWorldGuard().getRegionContainer().get(mainWorld);

            while (regions.hasRegion("project_" + rndmID)) {
                rndmID = generateCode(6);
            }
            this.id = rndmID;
        }
    }

    // --- ADDERS ---
    public void addMember(OfflinePlayer player) {
        this.members.add(player);
    }

    public void addPoint(BlockVector2D point) {
        this.points.add(point);
    }

    // REMOVERS

    public void removeMember(OfflinePlayer player) {
        this.members.remove(player);
        if (this.members.size() == 0) {
            this.members = null;
        }
    }

    public void removePoint(BlockVector2D point) {
        this.points.remove(point);
        if (this.points.size() == 0) {
            this.points = null;
        }
    }

    // --- PUTTERS ---

    public void putPoint(int index, BlockVector2D point) {
        this.points.add(index, point);
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
                tags.removeFromList(new Country(this.country).getAbbreviation() + "_" + this.oldTag, this.id);
            }

            if (tag != null) {
                tags.removeFromList(new Country(this.country).getAbbreviation() + "_" + this.tag, this.id);
            }

            tags.write();

            if (getAllMembers() != null) {
                for (OfflinePlayer p : getAllMembers()) {
                    PlayerData playerData = new PlayerData(p);

                    playerData.removeFromList("projects", this.id);

                    playerData.save();
                }
            }

            RegionManager regions = getWorldGuard().getRegionContainer().get(mainWorld);
            if (regions.hasRegion("project_" + this.id)) {
                regions.removeRegion("project_" + this.id);
            }
        }
    }

    // UPLOAD PROJECT

    public void upload() {
        if (this.id == null) {
            this.generateID();
        }

        YamlManager project = new YamlManager(pluginFolder, "projects/project_" + this.id + ".yml");

        if (this.difficulty == null) {
            project.deleteValue("difficulty");
        } else {
            project.setValue("difficulty", this.difficulty);
        }

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
                tags.removeFromList(new Country(this.country).getAbbreviation() + "_" + this.oldTag, this.id);
            }
        } else {
            project.setValue("tag", this.tag);

            if (oldTag != null) {
               tags.removeFromList(new Country(this.country).getAbbreviation() + "_" + this.oldTag, this.id);
            }

            tags.addToList(new Country(this.country).getAbbreviation() + "_" + this.tag, this.id, false);
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

        if (this.owner == null) {
            project.deleteValue("owner");
        } else {
            project.setValue("owner", this.owner.getUniqueId().toString());
        }

        if (this.members == null) {
            project.deleteValue("members");
        } else {
            for (OfflinePlayer p : this.members) {
                project.addToList("members", p.getUniqueId().toString(), false);
            }
        }

        if (this.country == null) {
            project.deleteValue("country");
        } else {
            project.setValue("country", this.country);
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

        FlagRegistry registry = getWorldGuard().getFlagRegistry();

        region.setFlag((StateFlag) registry.get("worldedit"), StateFlag.State.ALLOW);
        region.setFlag(registry.get("worldedit").getRegionGroupFlag(), RegionGroup.MEMBERS);

        DefaultDomain regionMembers = new DefaultDomain();

        if (this.pending) {
            for (OfflinePlayer member : getAllMembers()) {
                PlayerData playerData = new PlayerData(member);

                playerData.removeFromList("projects", this.id);

                playerData.save();
            }
        } else {
            if (this.members != null) {
                for (OfflinePlayer p : this.members) {
                    regionMembers.addPlayer(p.getUniqueId());

                    PlayerData playerData = new PlayerData(p);

                    playerData.addToList("projects", this.id, false);

                    playerData.save();
                }

                region.setMembers(regionMembers);
            }


            if (this.owner != null) {
                regionMembers.addPlayer(this.owner.getUniqueId());

                PlayerData playerData = new PlayerData(this.owner);

                playerData.addToList("projects", this.id, false);

                playerData.save();
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
    }

    // EMPTY PROJECT

    public void empty() {
        this.members = null;
        this.owner = null;
    }
}
