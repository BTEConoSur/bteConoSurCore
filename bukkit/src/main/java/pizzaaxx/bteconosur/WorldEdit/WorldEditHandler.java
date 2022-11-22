package pizzaaxx.bteconosur.WorldEdit;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Prefixable;

import java.util.ArrayList;
import java.util.List;

public class WorldEditHandler implements Prefixable {

    private final BTEConoSur plugin;
    private final WorldEdit worldEdit = WorldEdit.getInstance();
    private final WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

    public WorldEditHandler(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPrefix() {
        return "§f[§bWorldEdit§f] §7>> §f";
    }

    public LocalSession getLocalSession(Player player) {
        com.sk89q.worldedit.entity.Player actor = this.getWEPlayer(player);

        SessionManager manager = worldEdit.getSessionManager();
        return manager.get(actor);
    }

    public com.sk89q.worldedit.entity.Player getWEPlayer(Player player) {
        return new BukkitPlayer(worldEditPlugin, worldEditPlugin.getServerInterface(), player);
    }

    public Region getSelection(Player player) throws IncompleteRegionException {
        com.sk89q.worldedit.entity.Player actor = this.getWEPlayer(player);

        SessionManager manager = worldEdit.getSessionManager();
        LocalSession localSession = manager.get(actor);
        World selectionWorld = localSession.getSelectionWorld();

        return localSession.getSelection(selectionWorld);

    }

    public List<BlockVector2D> getSelectionPoints(Player player) throws IncompleteRegionException {
        Region selection = this.getSelection(player);

        if (selection instanceof Polygonal2DRegion) {
            Polygonal2DRegion polyRegion = (Polygonal2DRegion) selection;
            return polyRegion.getPoints();
        } else if (selection instanceof CuboidRegion) {
            CuboidRegion cuboidRegion = (CuboidRegion) selection;
            double maxX = cuboidRegion.getMaximumPoint().getX();
            double maxZ = cuboidRegion.getMaximumPoint().getZ();
            double minX = cuboidRegion.getMinimumPoint().getX();
            double minZ = cuboidRegion.getMinimumPoint().getZ();

            List<BlockVector2D> result = new ArrayList<>();

            result.add(new BlockVector2D(maxX, maxZ));
            result.add(new BlockVector2D(maxX, minZ));
            result.add(new BlockVector2D(minX, minZ));
            result.add(new BlockVector2D(minX, maxZ));

            return result;
        }
        throw new IncompleteRegionException();
    }
}
