package pizzaaxx.bteconosur.notifications;

import org.bukkit.OfflinePlayer;

import java.io.File;

public class Notification {

    private static final String NOTIFICATION_PREFIX = "§f[§9NOTIFICAIÓN§f] §7>>§r ";

    private final OfflinePlayer player;
    private final String message;
    private final File image = null;

    public Notification(OfflinePlayer player, String message) {
        this.player = player;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

}
