package pizzaaxx.bteconosur.worldguard;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.country.OldCountry;

import javax.naming.Name;
import java.util.HashSet;
import java.util.Set;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class WorldGuardProvider {

    public static WorldGuardPlugin getWorldGuard() {
        return WorldGuardPlugin.inst();
    }

    public static Set<Player> getPlayersInRegion(String id) {
        Set<Player> players = new HashSet<>();
        RegionManager manager = getWorldGuard().getRegionManager(mainWorld);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (manager.getApplicableRegions(p.getLocation()).getRegions().contains(manager.getRegion(id))) {
                players.add(p);
            }
        }
        return players;
    }

    public static Set<Player> getPlayersInCountry(OldCountry country) {
        if (!country.getName().equals("chile")) {
            return getPlayersInRegion(country.getName());
        } else {
            Set<Player> players = new HashSet<>();
            players.addAll(getPlayersInRegion("chile_cont"));
            players.addAll(getPlayersInRegion("chile_idp"));
            return players;
        }
    }

    public static Set<String> getRegionNamesAt(BlockVector2D vector) {

        Location loc = new Location(mainWorld, vector.getX(), 100, vector.getZ());

        return getRegionNamesAt(loc);

    }

    public static Set<String> getRegionNamesAt(Location loc) {

        Set<String> names = new HashSet<>();

        RegionManager manager = getWorldGuard().getRegionContainer().get(mainWorld);

        Set<ProtectedRegion> regions = manager.getApplicableRegions(loc).getRegions();

        for (ProtectedRegion region : regions) {

            names.add(region.getId());

        }

        return names;

    }
}
