package pizzaaxx.bteconosur.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class RegionListener implements Listener {

    private final Map<String, Geometry> regions = new HashMap<>();
    public final Map<UUID, Set<String>> lastVisitedRegions = new HashMap<>();
    private final Map<Predicate<String>, BiConsumer<String, UUID>> regionEnterListeners = new HashMap<>();
    private final Map<Predicate<String>, BiConsumer<String, UUID>> regionLeaveListeners = new HashMap<>();

    // check on move and on teleport
    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        this.onMove(event.getPlayer().getUniqueId(), event.getFrom(), event.getTo());
    }

    // check on teleport
    @EventHandler
    public void onTeleport(@NotNull PlayerTeleportEvent event) {
        this.onMove(event.getPlayer().getUniqueId(), event.getFrom(), event.getTo());
    }

    private void onMove(UUID uuid, Location from, Location to) {

        Set<String> before;
        if (lastVisitedRegions.containsKey(uuid)) {
            before = lastVisitedRegions.get(uuid);
        } else {
            before = this.getRegionsAt(from);
        }

        Set<String> after = this.getRegionsAt(to);

        // check regions entered
        applyCheck(uuid, after, before, regionEnterListeners);

        //check regions left
        applyCheck(uuid, before, after, regionLeaveListeners);

        lastVisitedRegions.put(uuid, after);

    }

    private void applyCheck(UUID uuid, @NotNull Set<String> before, Set<String> after, Map<Predicate<String>, BiConsumer<String, UUID>> listeners) {
        for (String region : before) {
            if (!after.contains(region)) {
                for (Map.Entry<Predicate<String>, BiConsumer<String, UUID>> entry : listeners.entrySet()) {
                    if (entry.getKey().test(region)) {
                        entry.getValue().accept(region, uuid);
                    }
                }
            }
        }
    }

    private @NotNull Set<String> getRegionsAt(Location location) {
        Set<String> regions = new HashSet<>();
        for (Map.Entry<String, Geometry> entry : this.regions.entrySet()) {
            if (entry.getValue().contains(new GeometryFactory().createPoint(new Coordinate(location.getX(), location.getZ())))) {
                regions.add(entry.getKey());
            }
        }
        return regions;
    }

    public void registerRegion(String id, Geometry region) {
        regions.put(id, region);
    }

    public void unregisterRegion(String id) {
        regions.remove(id);
    }

    public void registerRegionEnterListener(Predicate<String> predicate, BiConsumer<String, UUID> consumer) {
        regionEnterListeners.put(predicate, consumer);
    }

    public void registerRegionLeaveListener(Predicate<String> predicate, BiConsumer<String, UUID> consumer) {
        regionLeaveListeners.put(predicate, consumer);
    }

    public Set<String> getRegionsAt(double x, double z) {
        Set<String> regions = new HashSet<>();
        for (Map.Entry<String, Geometry> entry : this.regions.entrySet()) {
            if (entry.getValue().contains(new GeometryFactory().createPoint(new Coordinate(x, z)))) {
                regions.add(entry.getKey());
            }
        }
        return regions;
    }

}
