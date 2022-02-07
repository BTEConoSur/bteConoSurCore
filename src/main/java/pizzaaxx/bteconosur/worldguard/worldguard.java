package pizzaaxx.bteconosur.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class worldguard {

    public static WorldGuardPlugin getWorldGuard() {
        return WorldGuardPlugin.inst();
    }
}
