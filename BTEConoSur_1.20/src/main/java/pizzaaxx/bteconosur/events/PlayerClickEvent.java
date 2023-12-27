package pizzaaxx.bteconosur.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.protection.WorldProtector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (protectors.containsKey(event.getPlayer().getUniqueId())) {
            protectors.get(event.getPlayer().getUniqueId()).onInteract(event);
        }
    }
}
