package pizzaaxx.bteconosur.worldguard;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;

import java.util.HashSet;
import java.util.Set;

public class WorldGuardHelper {

    private final BteConoSur plugin;

    public WorldGuardHelper(BteConoSur plugin) {
        this.plugin = plugin;
    }

    public @NotNull Set<String> getRegionNamesAt(Location loc) {

        Set<String> names = new HashSet<>();

        RegionManager manager = plugin.getWorldGuard().getRegionManager(plugin.getWorld());
        Set<ProtectedRegion> regions = manager.getApplicableRegions(loc).getRegions();

        for (ProtectedRegion region : regions) {
            names.add(region.getId());
        }
        return names;
    }
}
