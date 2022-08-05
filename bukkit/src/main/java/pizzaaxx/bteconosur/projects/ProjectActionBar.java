package pizzaaxx.bteconosur.projects;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import static pizzaaxx.bteconosur.worldguard.RegionEvents.getEnteredRegions;

public class ProjectActionBar implements Listener {

    public void actionBar(Location from, Location to, Player player) {

        for (ProtectedRegion region : getEnteredRegions(from, to)) {
            if (region.getId().startsWith("project_")) {
                String id = region.getId().replace("project_", "");
                if (OldProject.projectExists(id)) {
                    OldProject project = new OldProject(id);
                    ChatColor color;
                    if (project.getDifficulty().toString().equalsIgnoreCase("facil")) {
                        color = ChatColor.GREEN;
                    } else if (project.getDifficulty().toString().equalsIgnoreCase("intermedio")) {
                        color = ChatColor.YELLOW;
                    } else {
                        color = ChatColor.RED;
                    }

                    if (project.getOwner() != null) {
                        player.sendActionBar(color + project.getName() + "§7 - " + new ServerPlayer(project.getOwner()).getName());
                    } else {
                        player.sendActionBar(color + project.getName() + "§7 - Sin reclamar");
                    }

                    break;

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