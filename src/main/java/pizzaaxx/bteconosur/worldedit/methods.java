package pizzaaxx.bteconosur.worldedit;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import com.sk89q.worldedit.regions.selector.RegionSelectorType;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static pizzaaxx.bteconosur.bteConoSur.mainWorld;

public class methods {
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

}
