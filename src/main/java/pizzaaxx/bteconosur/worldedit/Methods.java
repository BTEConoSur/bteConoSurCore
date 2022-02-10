package pizzaaxx.bteconosur.worldedit;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class Methods {
    public static String wePrefix = "§f[§5WORLDEDIT§f] §7>>§r ";

    public static Region getSelection(Player p) throws IncompleteRegionException{
        com.sk89q.worldedit.entity.Player actor = new BukkitPlayer((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"), ((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")).getServerInterface(), p);

        WorldEdit worldEdit = WorldEdit.getInstance();
        SessionManager manager = worldEdit.getSessionManager();
        LocalSession localSession = manager.get(actor);
        Region region;
        World selectionWorld = localSession.getSelectionWorld();
        region = localSession.getSelection(selectionWorld);

        return region;

    }

    public static void setSelection(Player p, Polygonal2DRegionSelector selector) {
        com.sk89q.worldedit.entity.Player actor = new BukkitPlayer((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"), ((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")).getServerInterface(), p);

        WorldEdit worldEdit = WorldEdit.getInstance();
        SessionManager manager = worldEdit.getSessionManager();
        LocalSession localSession = manager.get(actor);
        localSession.setRegionSelector((World) new BukkitWorld(mainWorld), selector);
        localSession.dispatchCUISelection(actor);
    }

    public static LocalSession getLocalSession(Player p) {
        com.sk89q.worldedit.entity.Player actor = new BukkitPlayer((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"), ((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")).getServerInterface(), p);
        return WorldEdit.getInstance().getSessionManager().get(actor);
    }

    public static EditSession getEditSession(Player p) {
        return WorldEdit.getInstance().getEditSessionFactory().getEditSession((World) new BukkitWorld(mainWorld), getLocalSession(p).getBlockChangeLimit());
    }

    public static void setBlocksInLine(Player p, Actor actor, EditSession editSession, Pattern pattern, Mask mask, Vector pos1, Vector pos2) {

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
                tipy = (int) Math.round(y1 + domstep * ((double) dy) / ((double) dz) * (y2-y1>0 ? 1 : -1));
                tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dz) * (x2-x1>0 ? 1 : -1));

                vset.add(new Vector(tipx, tipy, tipz));
            }
            notdrawn = false;
        }

        for (Vector point : vset) {
            if (mask != null && !(mask.test(point))) {
                continue;
            }
            if (getWorldGuard().canBuild(p, mainWorld.getBlockAt(new Location(mainWorld, point.getX(), point.getY(), point.getZ())))) {
                try {
                    editSession.setBlock(point, pattern.apply(point));
                } catch (MaxChangedBlocksException e) {
                    p.sendMessage(wePrefix + "Límite de bloques alcanzado.");
                }
            }
        }

        WorldEdit.getInstance().getSessionManager().get(actor).remember(editSession);
    }
}
