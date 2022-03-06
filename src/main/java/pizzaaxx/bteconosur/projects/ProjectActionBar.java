package pizzaaxx.bteconosur.projects;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;

import java.util.Set;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class ProjectActionBar implements Listener {

    public void actionBar(Location from, Location to, Player player) {
        RegionManager regions = getWorldGuard().getRegionContainer().get(mainWorld);

        ApplicableRegionSet regionSet1 = regions.getApplicableRegions(from);
        Set<ProtectedRegion> regionList1 = regionSet1.getRegions();

        ApplicableRegionSet regionSet2 = regions.getApplicableRegions(to);
        Set<ProtectedRegion> regionList2 = regionSet2.getRegions();

        for (ProtectedRegion region : regionList2) {
            if (!(regionList1.contains(region))) {
                if (region.getId().startsWith("project_")) {
                    try {
                        Project project = new Project(to);

                        ChatColor color;
                        if (project.getDifficulty().equals("facil")) {
                            color = ChatColor.GREEN;
                        } else if (project.getDifficulty().equals("intermedio")) {
                            color = ChatColor.YELLOW;
                        } else {
                            color = ChatColor.RED;
                        }

                        if (project.getOwnerOld() != null) {
                            player.sendActionBar(color + project.getName() + "§7 - " + new ServerPlayer(project.getOwnerOld()).getName());
                        }

                        break;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        actionBar(e.getFrom(), e.getTo(), e.getPlayer());
    }

    @EventHandler
    public void onRegionEnter(PlayerMoveEvent e) {
        actionBar(e.getFrom(), e.getTo(), e.getPlayer());
    }
}
