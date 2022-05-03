package pizzaaxx.bteconosur.server.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class NewServerPlayer {

    private final UUID identifier;
    private final String discriminator;
    private final int points;
    private final List<String> notifications;

    public NewServerPlayer(UUID identifier,
                           String discriminator,
                           int points,
                           String notifications) {
        this.identifier = identifier;
        this.discriminator = discriminator;
        this.points = points;
        this.notifications = notifications;
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

    public Player getPlayer() {
        return Bukkit.getPlayer(identifier);
    }

    public List<String> getNotifications() {
        return notifications;
    }

}
