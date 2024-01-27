package pizzaaxx.bteconosur.building.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.util.HashSet;
import java.util.Set;

public class WorldEditConnector {

    private final BTEConoSurPlugin plugin;

    public WorldEditConnector(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    public Player getWEPlayer(org.bukkit.entity.Player player) {
        return new BukkitPlayer(plugin.getWorldEdit(), player);
    }

    public Region getSelection(org.bukkit.entity.Player player) throws IncompleteRegionException {
        Player actor = this.getWEPlayer(player);
        return WorldEdit.getInstance().getSessionManager().get(actor).getSelection(actor.getWorld());
    }

    public LocalSession getLocalSession(org.bukkit.entity.Player player) {
        Player actor = this.getWEPlayer(player);
        return WorldEdit.getInstance().getSessionManager().get(actor);
    }

    public EditSession getEditSession(org.bukkit.entity.Player player) {
        Player actor = this.getWEPlayer(player);
        return WorldEdit.getInstance().getSessionManager().get(actor).createEditSession(actor);
    }

    public Set<BlockVector3> getBlocksInLine(@NotNull BlockVector3 pos1, @NotNull BlockVector3 pos2) {
        Set<BlockVector3> vset = new HashSet<>();
        boolean notdrawn = true;

        int x1 = pos1.getBlockX(), y1 = pos1.getBlockY(), z1 = pos1.getBlockZ();
        int x2 = pos2.getBlockX(), y2 = pos2.getBlockY(), z2 = pos2.getBlockZ();
        int tipx = x1, tipy = y1, tipz = z1;
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1), dz = Math.abs(z2 - z1);

        if (dx + dy + dz == 0) {
            vset.add(BlockVector3.at(tipx, tipy, tipz));
            notdrawn = false;
        }

        int max = Math.max(Math.max(dx, dy), dz);
        if (max == dx && notdrawn) {
            for (int domstep = 0; domstep <= dx; domstep++) {
                tipx = x1 + domstep * (x2 - x1 > 0 ? 1 : -1);
                tipy = (int) Math.round(y1 + domstep * ((double) dy) / ((double) dx) * (y2 - y1 > 0 ? 1 : -1));
                tipz = (int) Math.round(z1 + domstep * ((double) dz) / ((double) dx) * (z2 - z1 > 0 ? 1 : -1));

                vset.add(BlockVector3.at(tipx, tipy, tipz));
            }
            notdrawn = false;
        }

        if (max == dy && notdrawn) {
            for (int domstep = 0; domstep <= dy; domstep++) {
                tipy = y1 + domstep * (y2 - y1 > 0 ? 1 : -1);
                tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dy) * (x2 - x1 > 0 ? 1 : -1));
                tipz = (int) Math.round(z1 + domstep * ((double) dz) / ((double) dy) * (z2 - z1 > 0 ? 1 : -1));

                vset.add(BlockVector3.at(tipx, tipy, tipz));
            }
            notdrawn = false;
        }

        if (max == dz && notdrawn) {
            for (int domstep = 0; domstep <= dz; domstep++) {
                tipz = z1 + domstep * (z2 - z1 > 0 ? 1 : -1);
                tipy = (int) Math.round(y1 + domstep * ((double) dy) / ((double) dz) * (y2 - y1 > 0 ? 1 : -1));
                tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dz) * (x2 - x1 > 0 ? 1 : -1));

                vset.add(BlockVector3.at(tipx, tipy, tipz));
            }
        }
        return vset;
    }

    public Pattern getPattern(org.bukkit.entity.Player player, String input) throws InputParseException {
        ParserContext context = new ParserContext();
        com.sk89q.worldedit.entity.Player actor = this.getWEPlayer(player);
        context.setActor(actor);
        Extent extent = actor.getExtent();
        if (extent instanceof World) {
            context.setWorld((World) extent);
        }
        context.setSession(this.getLocalSession(player));

        return plugin.getWorldEdit().getWorldEdit().getPatternFactory().parseFromInput(input, context);
    }

}
