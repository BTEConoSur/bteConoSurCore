package pizzaaxx.bteconosur.ServerPlayer;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRegistry implements Listener {

    private final Plugin plugin;

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        load(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        scheduleDeletion(event.getPlayer().getUniqueId());
    }

    public PlayerRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, ServerPlayer> registry = new HashMap<>();
    private final Map<UUID, Long> deletionRegistry = new HashMap<>();

    public void load(UUID uuid) {
        ServerPlayer serverPlayer = new ServerPlayer(uuid);
        serverPlayer.loadManagers();
        registry.put(uuid, serverPlayer);
        scheduleDeletion(uuid);
    }

    public void remove(UUID id) {
        registry.remove(id);
        deletionRegistry.remove(id);
    }

    public boolean isLoaded(UUID id) {
        return registry.containsKey(id);
    }

    /**
     * Make sure to check whether the player exists first.
     * @param id The UUID of the player.
     * @return A ServerPlayer instance, either loaded from memory or from the disk.
     */
    public ServerPlayer get(UUID id) {
        if (!registry.containsKey(id)) {
            load(id);
        }
        scheduleDeletion(id);
        return registry.get(id);
    }

    private void scheduleDeletion(UUID id) {

        deletionRegistry.put(id, System.currentTimeMillis());

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (deletionRegistry.containsKey(id) && registry.containsKey(id) && !registry.get(id).getPlayer().isOnline()) {
                    if (System.currentTimeMillis() - deletionRegistry.get(id) > 590000) {
                        registry.remove(id);
                        deletionRegistry.remove(id);
                    }
                }
            }
        };

        runnable.runTaskLaterAsynchronously(plugin, 12000);

    }

    public boolean exists(UUID uuid) {
        return (Bukkit.getOfflinePlayer(uuid).hasPlayedBefore() && new File(plugin.getDataFolder(), "playerData/" + uuid.toString() + ".yml").exists());
    }

}
