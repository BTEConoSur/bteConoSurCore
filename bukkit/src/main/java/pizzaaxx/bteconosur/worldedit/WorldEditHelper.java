package pizzaaxx.bteconosur.worldedit;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class WorldEditHelper {

    public static final String WORLD_EDIT_PREFIX = "§f[§5WORLDEDIT§f] §7>>§r ";
    public static final WorldEdit WORLD_EDIT_INSTANCE = WorldEdit.getInstance();
    public static final WorldEditPlugin WORLD_EDIT_PLUGIN = (WorldEditPlugin) Bukkit.getPluginManager()
            .getPlugin("WorldEdit");

    public static com.sk89q.worldedit.entity.Player transform(Player player) {
        return new BukkitPlayer(WORLD_EDIT_PLUGIN, WORLD_EDIT_PLUGIN.getServerInterface(), player);
    }

    public static Region getSelection(Player player) throws IncompleteRegionException {
        com.sk89q.worldedit.entity.Player actor =
                transform(player);

        SessionManager manager = WORLD_EDIT_INSTANCE.getSessionManager();
        LocalSession localSession = manager.get(actor);
        World selectionWorld = localSession.getSelectionWorld();

        localSession.getRegionSelector((World) new BukkitWorld(mainWorld)).getIncompleteRegion();

        return localSession.getSelection(selectionWorld);

    }

    public static Region getIncompleteSelection(Player player) {

        com.sk89q.worldedit.entity.Player actor =
                transform(player);

        SessionManager manager = WORLD_EDIT_INSTANCE.getSessionManager();
        LocalSession localSession = manager.get(actor);
        World selectionWorld = localSession.getSelectionWorld();

        return localSession.getRegionSelector(selectionWorld).getIncompleteRegion();
    }

    public static Polygonal2DRegion polyRegion(Region region) throws IllegalArgumentException, IncompleteRegionException {
        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion polygonal2DRegion = (Polygonal2DRegion) region;

            if (polygonal2DRegion.getPoints().size() < 3) {
                throw new IncompleteRegionException();
            }

            return (Polygonal2DRegion) region;
        } else if (region instanceof CuboidRegion) {
            CuboidRegion cuboidRegion = (CuboidRegion) region;
            Vector first = cuboidRegion.getPos1();
            Vector second = cuboidRegion.getPos2();

            List<BlockVector2D> points = new ArrayList<>();

            points.add(new BlockVector2D(first.getX(), first.getZ()));
            points.add(new BlockVector2D(second.getX(), first.getZ()));
            points.add(new BlockVector2D(second.getX(), second.getZ()));
            points.add(new BlockVector2D(first.getX(), second.getZ()));

            return new Polygonal2DRegion((World) new BukkitWorld(mainWorld), points, cuboidRegion.getMinimumY(), cuboidRegion.getMaximumY());
        }
        throw new IllegalArgumentException();
    }

    public static void setSelection(Player player, Polygonal2DRegionSelector selector) {
        com.sk89q.worldedit.entity.Player actor = transform(player);

        LocalSession localSession = getLocalSession(player);
        localSession.setRegionSelector((World) new BukkitWorld(mainWorld), selector);
        localSession.dispatchCUISelection(actor);
    }

    public static LocalSession getLocalSession(Player player) {
        com.sk89q.worldedit.entity.Player actor = transform(player);
        return WORLD_EDIT_INSTANCE.getSessionManager().get(actor);
    }

    public static EditSession getEditSession(Player player) {
        LocalSession localSession = getLocalSession(player);

        return WORLD_EDIT_INSTANCE
                .getEditSessionFactory()
                .getEditSession((World) new BukkitWorld(mainWorld), localSession.getBlockChangeLimit());
    }

    public static List<Vector> getBlocksInLine(Vector pos1, Vector pos2) {
        Set<Vector> vset = new HashSet<>();
        boolean notdrawn = true;

        int x1 = pos1.getBlockX(), y1 = pos1.getBlockY(), z1 = pos1.getBlockZ();
        int x2 = pos2.getBlockX(), y2 = pos2.getBlockY(), z2 = pos2.getBlockZ();
        int tipx = x1, tipy = y1, tipz = z1;
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1), dz = Math.abs(z2 - z1);

        if (dx + dy + dz == 0) {
            vset.add(new Vector(tipx, tipy, tipz));
            notdrawn = false;
        }

        int max = Math.max(Math.max(dx, dy), dz);
        if (max == dx && notdrawn) {
            for (int domstep = 0; domstep <= dx; domstep++) {
                tipx = x1 + domstep * (x2 - x1 > 0 ? 1 : -1);
                tipy = (int) Math.round(y1 + domstep * ((double) dy) / ((double) dx) * (y2 - y1 > 0 ? 1 : -1));
                tipz = (int) Math.round(z1 + domstep * ((double) dz) / ((double) dx) * (z2 - z1 > 0 ? 1 : -1));

                vset.add(new Vector(tipx, tipy, tipz));
            }
            notdrawn = false;
        }

        if (max == dy && notdrawn) {
            for (int domstep = 0; domstep <= dy; domstep++) {
                tipy = y1 + domstep * (y2 - y1 > 0 ? 1 : -1);
                tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dy) * (x2 - x1 > 0 ? 1 : -1));
                tipz = (int) Math.round(z1 + domstep * ((double) dz) / ((double) dy) * (z2 - z1 > 0 ? 1 : -1));

                vset.add(new Vector(tipx, tipy, tipz));
            }
            notdrawn = false;
        }

        if (max == dz && notdrawn) {
            for (int domstep = 0; domstep <= dz; domstep++) {
                tipz = z1 + domstep * (z2 - z1 > 0 ? 1 : -1);
                tipy = (int) Math.round(y1 + domstep * ((double) dy) / ((double) dz) * (y2 - y1 > 0 ? 1 : -1));
                tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dz) * (x2 - x1 > 0 ? 1 : -1));

                vset.add(new Vector(tipx, tipy, tipz));
            }
        }

        List<Vector> vectors = new ArrayList<>();
        for (Vector point : vset) {
            vectors.add(point);
        }

        return vectors;
    }

    public static EditSession setBlocksInLine(Player p, EditSession editSession, Pattern pattern, Mask mask, Vector pos1, Vector pos2) {


        Set<Vector> vset = new HashSet<>();
        boolean notdrawn = true;

        int x1 = pos1.getBlockX(), y1 = pos1.getBlockY(), z1 = pos1.getBlockZ();
        int x2 = pos2.getBlockX(), y2 = pos2.getBlockY(), z2 = pos2.getBlockZ();
        int tipx = x1, tipy = y1, tipz = z1;
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1), dz = Math.abs(z2 - z1);

        if (dx + dy + dz == 0) {
            vset.add(new Vector(tipx, tipy, tipz));
            notdrawn = false;
        }

        int max = Math.max(Math.max(dx, dy), dz);
        if (max == dx && notdrawn) {
            for (int domstep = 0; domstep <= dx; domstep++) {
                tipx = x1 + domstep * (x2 - x1 > 0 ? 1 : -1);
                tipy = (int) Math.round(y1 + domstep * ((double) dy) / ((double) dx) * (y2 - y1 > 0 ? 1 : -1));
                tipz = (int) Math.round(z1 + domstep * ((double) dz) / ((double) dx) * (z2 - z1 > 0 ? 1 : -1));

                vset.add(new Vector(tipx, tipy, tipz));
            }
            notdrawn = false;
        }

        if (max == dy && notdrawn) {
            for (int domstep = 0; domstep <= dy; domstep++) {
                tipy = y1 + domstep * (y2 - y1 > 0 ? 1 : -1);
                tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dy) * (x2 - x1 > 0 ? 1 : -1));
                tipz = (int) Math.round(z1 + domstep * ((double) dz) / ((double) dy) * (z2 - z1 > 0 ? 1 : -1));

                vset.add(new Vector(tipx, tipy, tipz));
            }
            notdrawn = false;
        }

        if (max == dz && notdrawn) {
            for (int domstep = 0; domstep <= dz; domstep++) {
                tipz = z1 + domstep * (z2 - z1 > 0 ? 1 : -1);
                tipy = (int) Math.round(y1 + domstep * ((double) dy) / ((double) dz) * (y2 - y1 > 0 ? 1 : -1));
                tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dz) * (x2 - x1 > 0 ? 1 : -1));

                vset.add(new Vector(tipx, tipy, tipz));
            }
        }

        for (Vector point : vset) {
            if (mask != null && !(mask.test(point))) {
                continue;
            }
            if (getWorldGuard().canBuild(p, mainWorld.getBlockAt(new Location(mainWorld, point.getX(), point.getY(), point.getZ())))) {
                try {
                    editSession.setBlock(point, pattern.apply(point));
                } catch (MaxChangedBlocksException e) {
                    p.sendMessage(WORLD_EDIT_PREFIX + "Límite de bloques alcanzado.");
                    break;
                }
            }
        }

        return editSession;

    }
}
