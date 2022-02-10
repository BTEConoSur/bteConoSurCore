package pizzaaxx.bteconosur.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class WorldGuardProvider {

    public static WorldGuardPlugin getWorldGuard() {
        return WorldGuardPlugin.inst();
    }
}
