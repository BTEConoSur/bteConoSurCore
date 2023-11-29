package pizzaaxx.bteconosur.player;

import java.util.UUID;

public class PlayerNotOnlineException extends Exception {

    private final UUID uuid;

    public PlayerNotOnlineException(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
