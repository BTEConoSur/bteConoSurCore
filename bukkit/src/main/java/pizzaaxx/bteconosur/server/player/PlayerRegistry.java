package pizzaaxx.bteconosur.server.player;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRegistry {

    private final Map<UUID, ServerPlayer> registry = new HashMap<>();
    private final Map<UUID, Long> deletionRegistry = new HashMap<>();

    public void add(@NotNull ServerPlayer serverPlayer) {
        serverPlayer.loadManagers();
        registry.put(serverPlayer.getId(), serverPlayer);
    }

    public void remove(UUID id) {
        registry.remove(id);
    }

    public boolean exists(UUID id) {
        return registry.containsKey(id);
    }

    public ServerPlayer get(UUID id) {
        return registry.get(id);
    }



}
