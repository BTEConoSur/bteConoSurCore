package pizzaaxx.bteconosur.server.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRegistry {

    private final Map<UUID, NewServerPlayer> players = new HashMap<>();

    public void add(NewServerPlayer newServerPlayer) {
        players.put(newServerPlayer.getIdentifier(), newServerPlayer);
    }

    public void remove(UUID id) {
        players.remove(id);
    }

    public boolean exists(UUID id) {
        return players.containsKey(id);
    }

    public NewServerPlayer get(UUID id) {
        return players.get(id);
    }

    public Collection<NewServerPlayer> values() {
        return players.values();
    }

}
