package pizzaaxx.bteconosur.server.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.server.player.notification.Notification;

import java.util.List;
import java.util.UUID;

public class NewServerPlayer {

    private final UUID identifier;
    private final String discriminator;
    private final int points;
    private final List<Notification> notifications;
    private String nick;

    public NewServerPlayer(UUID identifier,
                           String discriminator,
                           int points,
                           List<Notification> notifications,
                           String nick) {
        this.identifier = identifier;
        this.discriminator = discriminator;
        this.points = points;
        this.notifications = notifications;
        this.nick = nick;
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

    public List<Notification> getNotifications() {
        return notifications;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

}
