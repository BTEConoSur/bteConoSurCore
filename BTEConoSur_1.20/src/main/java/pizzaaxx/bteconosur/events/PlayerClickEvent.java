package pizzaaxx.bteconosur.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.protection.WorldProtector;
import pizzaaxx.bteconosur.utils.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PlayerClickEvent implements Listener {

    private final BTEConoSurPlugin plugin;
    private final Map<UUID, WorldProtector> protectors = new HashMap<>();

    public PlayerClickEvent(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerProtector(UUID uuid) {
        protectors.put(uuid, new WorldProtector(plugin, uuid));
    }

    public void unregisterProtector(UUID uuid) {
        protectors.remove(uuid);
    }

    private final Map<Integer, Pair<Predicate<PlayerInteractEvent>, Consumer<PlayerInteractEvent>>> blockingConditions = new HashMap<>();
    private final Map<Predicate<PlayerInteractEvent>, Consumer<PlayerInteractEvent>> nonBlockingConditions = new HashMap<>();

    public void registerBlockingCondition(int priority, Predicate<PlayerInteractEvent> condition, Consumer<PlayerInteractEvent> action) {
        blockingConditions.put(priority, new Pair<>(condition, action));
    }

    public void registerNonBlockingCondition(Predicate<PlayerInteractEvent> condition, Consumer<PlayerInteractEvent> action) {
        nonBlockingConditions.put(condition, action);
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {

        // run all non-blocking conditions
        nonBlockingConditions.forEach((predicate, consumer) -> {
            if (predicate.test(event)) {
                consumer.accept(event);
            }
        });

        // run all blocking conditions
        // the map key is the priority, if a condition returns true, the loop breaks
        // sort the map by key (priority)
        blockingConditions.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(pair -> pair.getKey().test(event))
                .findFirst()
                .ifPresentOrElse(
                        pair -> pair.getValue().accept(event),
                        () -> {
                            if (protectors.containsKey(event.getPlayer().getUniqueId())) {
                                protectors.get(event.getPlayer().getUniqueId()).onInteract(event);
                            }
                        }
                );
    }
}
