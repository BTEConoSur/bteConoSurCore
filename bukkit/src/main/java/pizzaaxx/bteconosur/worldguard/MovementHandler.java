package pizzaaxx.bteconosur.worldguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.server.player.DataManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.util.Set;
import java.util.stream.Collectors;

public class MovementHandler implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortal(PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWaterOrLava(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && (event.getItem().getType() == Material.LAVA_BUCKET || event.getItem().getType() == Material.WATER_BUCKET)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        exec(event.getFrom(), event.getTo(), event.getPlayer());
    }

    @EventHandler
    public void onTeleport(@NotNull PlayerTeleportEvent event) {
        exec(event.getFrom(), event.getTo(), event.getPlayer());
    }

    private void exec(Location from, Location to, Player p) {
        final Set<ProtectedRegion> regionsLeft = RegionEvents.getLeftRegions(from, to);
        final Set<ProtectedRegion> regionsEntered = RegionEvents.getEnteredRegions(from, to);
        final Set<String> regionsLeftNames = regionsLeft.stream().map(ProtectedRegion::getId).collect(Collectors.toSet());
        final Set<String> regionsEnteredNames = regionsEntered.stream().map(ProtectedRegion::getId).collect(Collectors.toSet());

        // LOBBY LEAVE

        if (regionsLeftNames.contains("lobby")) {
            p.setGameMode(GameMode.CREATIVE);
            DataManager data = new ServerPlayer(p).getDataManager();
            if (data.contains("isFirst")) {
                data.set("isFirst", null);
                data.save();
            }
        }

        if (regionsEnteredNames.contains("lobby")) {
            p.setGameMode(GameMode.SURVIVAL);
        }
    }

}
