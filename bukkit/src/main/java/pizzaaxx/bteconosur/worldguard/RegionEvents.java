package pizzaaxx.bteconosur.worldguard;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;

import java.util.HashSet;
import java.util.Set;

public class RegionEvents {

    private final BteConoSur plugin;

    public RegionEvents(BteConoSur plugin) {
        this.plugin = plugin;
    }

    public @NotNull Set<ProtectedRegion> getLeftRegions(Location from, Location to) {
        Set<ProtectedRegion> regions = new HashSet<>();

        RegionManager manager = plugin.getWorldGuard().getRegionContainer().get(plugin.getWorld());

        ApplicableRegionSet regionSet1 = manager.getApplicableRegions(from);
        Set<ProtectedRegion> regionListFrom = regionSet1.getRegions();

        ApplicableRegionSet regionSet2 = manager.getApplicableRegions(to);
        Set<ProtectedRegion> regionListTo = regionSet2.getRegions();

        for (ProtectedRegion r : regionListFrom) {
            if (!(regionListTo.contains(r))) {
                regions.add(r);
            }
        }
        return regions;
    }

    public @NotNull Set<ProtectedRegion> getEnteredRegions(Location from, Location to) {
        Set<ProtectedRegion> regions = new HashSet<>();

        RegionManager manager = plugin.getWorldGuard().getRegionContainer().get(plugin.getWorld());

        ApplicableRegionSet regionSet1 = manager.getApplicableRegions(from);
        Set<ProtectedRegion> regionListFrom = regionSet1.getRegions();

        ApplicableRegionSet regionSet2 = manager.getApplicableRegions(to);
        Set<ProtectedRegion> regionListTo = regionSet2.getRegions();

        for (ProtectedRegion r : regionListTo) {
            if (!(regionListFrom.contains(r))) {
                regions.add(r);
            }
        }
        return regions;
    }
}
