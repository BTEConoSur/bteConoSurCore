package pizzaaxx.bteconosur.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static pizzaaxx.bteconosur.Config.*;
import static pizzaaxx.bteconosur.Config.requestsPe;
import static pizzaaxx.bteconosur.bteConoSur.mainWorld;
import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.worldguard.worldguard.getWorldGuard;

public class misc {
    public static String getCountryAtLocation(BlockVector2D loc) {
        Location location = new Location(mainWorld, loc.getX(), 100.0, loc.getZ());
        RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
        Set<ProtectedRegion> regions = regionManager.getApplicableRegions(location).getRegions();
        if (regions.contains(regionManager.getRegion("argentina"))) {
            return "argentina";
        }
        if (regions.contains(regionManager.getRegion("bolivia"))) {
            return "bolivia";
        }
        if (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp"))) {
            return "chile";
        }
        if (regions.contains(regionManager.getRegion("paraguay"))) {
            return "paraguay";
        }
        if (regions.contains(regionManager.getRegion("peru"))) {
            return "peru";
        }
        if (regions.contains(regionManager.getRegion("uruguay"))) {
            return "uruguay";
        }
        return null;

    }

    public static String getCountryAtLocation(Location loc) {
        RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
        Set<ProtectedRegion> regions = regionManager.getApplicableRegions(loc).getRegions();
        if (regions.contains(regionManager.getRegion("argentina"))) {
            return "argentina";
        }
        if (regions.contains(regionManager.getRegion("bolivia"))) {
            return "bolivia";
        }
        if (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp"))) {
            return "chile";
        }
        if (regions.contains(regionManager.getRegion("paraguay"))) {
            return "paraguay";
        }
        if (regions.contains(regionManager.getRegion("peru"))) {
            return "peru";
        }
        if (regions.contains(regionManager.getRegion("uruguay"))) {
            return "uruguay";
        }
        return null;

    }

    public static TextChannel getLogsChannel(String country) {
        if (country.equals("argentina")) {
            return logsAr;
        }
        if (country.equals("bolivia")) {
            return logsBo;
        }
        if (country.equals("chile")) {
            return logsCl;
        }
        if (country.equals("peru")) {
            return logsPe;
        }
        return null;
    }

    public static String getCountryPrefix(String country) {
        if (country.equals("argentina")) {
            return "ar";
        }
        if (country.equals("bolivia")) {
            return "bo";
        }
        if (country.equals("chile")) {
            return "cl";
        }
        if (country.equals("paraguay")) {
            return "py";
        }
        if (country.equals("peru")) {
            return "pe";
        }
        if (country.equals("uruguay")) {
            return "uy";
        }
        return null;
    }

    public static ItemStack itemBuilder(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack itemBuilder(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getCustomHead(String name, String lore, String value) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM,1,(byte) SkullType.PLAYER.ordinal());
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        headMeta.setDisplayName(name);

        if (lore != null) {
            List<String> lines = new ArrayList<>();
            lines.add(lore);
            headMeta.setLore(lines);
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", value));
        Field field;
        try {
            field = headMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException x) {
            x.printStackTrace();
        }

        head.setItemMeta(headMeta);

        return head;
    }

    public static List<Project> getAvailableProjectsWithTag(String tag, String country, String difficulty) {
        List<Project> projects = new ArrayList<>();

        YamlManager tags = new YamlManager(pluginFolder, "projectTags/tags.yml");

        List<String> candidates = (List<String>) tags.getList(new Country(country).getAbbreviation() + "_" + tag);

        for (String candidate : candidates) {
            try {
                Project project = new Project(candidate);

                if (!(project.isClaimed()) && project.getDifficulty().equals(difficulty)) {
                    projects.add(project);
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("No se ha podido encontrar el proyecto §a" + candidate + "§f.");
            }
        }
        return projects;
    }
}
