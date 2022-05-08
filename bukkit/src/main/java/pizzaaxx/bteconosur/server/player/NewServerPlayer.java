package pizzaaxx.bteconosur.server.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.server.player.notification.Notification;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NewServerPlayer {

    private final UUID identifier;
    private final String name;
    private final String discriminator;
    private final int points;
    private final List<Notification> notifications;
    private String nick;
    private int primaryGroup;
    private final Set<Integer> secondaryGroups;
    private String channelChat;

    public NewServerPlayer(UUID identifier,
                           String name,
                           String discriminator,
                           int points,
                           List<Notification> notifications,
                           String nick,
                           int primaryGroup,
                           Set<Integer> secondaryGroups,
                           String channelChat) {
        this.identifier = identifier;
        this.name = name;
        this.discriminator = discriminator;
        this.points = points;
        this.notifications = notifications;
        this.nick = nick;
        this.primaryGroup = primaryGroup;
        this.secondaryGroups = secondaryGroups;
        this.channelChat = channelChat;
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

    public String getName() {
        return name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getPrimaryGroup() {
        return primaryGroup;
    }

    public void setPrimaryGroup(int primaryGroup) {
        this.primaryGroup = primaryGroup;
    }

    public Set<Integer> getSecondaryGroups() {
        return secondaryGroups;
    }

    public void addSecondaryGroup(int secondaryGroup) {
        secondaryGroups.add(secondaryGroup);
    }

    public void removeSecondaryGroup(int secondaryGroup) {
        secondaryGroups.remove(secondaryGroup);
    }

    public void setChannelChat(String channelChat) {
        this.channelChat = channelChat;
    }

    public String getChannelChat() {
        return channelChat;
    }

}
