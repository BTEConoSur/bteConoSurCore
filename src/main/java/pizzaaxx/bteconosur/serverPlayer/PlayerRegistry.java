package pizzaaxx.bteconosur.serverPlayer;

import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRegistry {

    private final Map<UUID, ServerPlayer> players = new HashMap<>();

    public void add(ServerPlayer serverPlayer) {
        players.put(serverPlayer.getId(), serverPlayer);
    }

    public void remove(UUID id) {
        players.remove(id);
    }

    public boolean exists(UUID id) {
        return players.containsKey(id);
    }

    public ServerPlayer get(UUID id) {
        return players.get(id);
    }

    public Collection<ServerPlayer> values() {
        return players.values();
    }

}
