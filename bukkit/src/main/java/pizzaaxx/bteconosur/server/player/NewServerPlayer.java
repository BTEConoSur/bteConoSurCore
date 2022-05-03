package pizzaaxx.bteconosur.server.player;

import java.util.UUID;

public class NewServerPlayer {

    private final UUID identifier;
    private final String discriminator;
    private final int points;

    public NewServerPlayer(UUID identifier,
                           String discriminator,
                           int points) {
        this.identifier = identifier;
        this.discriminator = discriminator;
        this.points = points;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public int getPoints() {
        return points;
    }

    public String getDiscriminator() {
        return discriminator;
    }

}
