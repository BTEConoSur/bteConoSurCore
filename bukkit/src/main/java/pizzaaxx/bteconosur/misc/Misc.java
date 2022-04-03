package pizzaaxx.bteconosur.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.helper.Pair;

import java.lang.reflect.Field;
import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.*;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class Misc {

    public static final Map<String, String> COUNTRIES = new HashMap<>();

    static  {
        COUNTRIES.put("argentina" , "arg");
        COUNTRIES.put("bolivia", "bo");
        COUNTRIES.put("chile", "cl");
        COUNTRIES.put("paraguay", "pa");
        COUNTRIES.put("peru", "pe");
        COUNTRIES.put("uruguay", "uy");
    }

    public static OldCountry getCountryAtLocation(BlockVector2D loc) {
        return getCountryAtLocation(new Location(mainWorld, loc.getX(), 100.0, loc.getZ()));
    }

    public static OldCountry getCountryAtLocation(Location loc) {
        RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
        Set<ProtectedRegion> regions = regionManager.getApplicableRegions(loc).getRegions();
        if (regions.contains(regionManager.getRegion("argentina"))) {
            return new OldCountry("argentina");
        }
        if (regions.contains(regionManager.getRegion("bolivia"))) {
            return new OldCountry("bolivia");
        }
        if (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp"))) {
            return new OldCountry("chile");
        }
        if (regions.contains(regionManager.getRegion("paraguay"))) {
            return new OldCountry("paraguay");
        }
        if (regions.contains(regionManager.getRegion("peru"))) {
            return new OldCountry("peru");
        }
        if (regions.contains(regionManager.getRegion("uruguay"))) {
            return new OldCountry("uruguay");
        }
        return null;

    }

    public static String getCountryPrefix(String country) {
        return COUNTRIES.get(country);
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

    public static String getSimplePrefix(String name, String color) {
        return "§f[§" + color + name.toUpperCase() + "§f] §7>>§r ";
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

    @SafeVarargs
    public static String getMapURL(Pair<List<BlockVector2D>, String>... polygons) {
        List<String> shapes = new ArrayList<>();
        for (Pair<List<BlockVector2D>, String> polygon : polygons) {
            List<BlockVector2D> points = polygon.getKey();
            if (points != null && points.size() > 1) {
                List<String> coords = new ArrayList<>();
                for (BlockVector2D point : points) {
                    coords.add(new Coords2D(point).getLat() + "," + new Coords2D(point).getLon());
                }
                coords.add(new Coords2D(points.get(0)).getLat() + "," + new Coords2D(points.get(0)).getLon());
                shapes.add("&shape=" + String.join("|", coords) + "|fill:" + polygon.getValue() + "50|border:" + polygon.getValue());
            }
        }
        return "https://open.mapquestapi.com/staticmap/v5/map?key=" + key + "&type=sat&size=1920,1080&imagetype=png" + String.join("", shapes);
    }
}
