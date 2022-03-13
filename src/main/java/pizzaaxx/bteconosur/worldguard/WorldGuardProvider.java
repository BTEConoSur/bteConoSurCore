package pizzaaxx.bteconosur.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
}
