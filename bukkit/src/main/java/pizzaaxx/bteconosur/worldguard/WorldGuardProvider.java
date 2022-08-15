package pizzaaxx.bteconosur.worldguard;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.OldCountry;

import javax.naming.Name;
import java.util.HashSet;
import java.util.Set;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class WorldGuardProvider {

    public static @NotNull Set<Player> getPlayersInRegion(String id, @NotNull BteConoSur plugin) {
        Set<Player> players = new HashSet<>();
        RegionManager manager = plugin.getWorldGuard().getRegionManager(plugin.getWorld());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (manager.getApplicableRegions(p.getLocation()).getRegions().contains(manager.getRegion(id))) {
                players.add(p);
            }
        }
        return players;
    }

    public static @NotNull Set<String> getRegionNamesAt(@NotNull BlockVector2D vector, @NotNull BteConoSur plugin) {

        Location loc = new Location(plugin.getWorld(), vector.getX(), 100, vector.getZ());

        return getRegionNamesAt(loc);

    }

    public static @NotNull Set<String> getRegionNamesAt(Location loc, @NotNull BteConoSur plugin) {

        Set<String> names = new HashSet<>();

        RegionManager manager = plugin.getWorldGuard().getRegionManager(plugin.getWorld());
        Set<ProtectedRegion> regions = manager.getApplicableRegions(loc).getRegions();

        for (ProtectedRegion region : regions) {
            names.add(region.getId());
        }
        return names;
    }
}
