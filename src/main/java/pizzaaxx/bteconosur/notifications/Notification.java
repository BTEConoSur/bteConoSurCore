package pizzaaxx.bteconosur.notifications;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.playerData.PlayerData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Notification {

    public static String notificationPrefix = "§f[§9NOTIFICAIÓN§f] §7>>§r ";

    private OfflinePlayer player;
    private String message;
    private File image = null;

    public Notification(OfflinePlayer player, String message) {
        this.player = player;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
