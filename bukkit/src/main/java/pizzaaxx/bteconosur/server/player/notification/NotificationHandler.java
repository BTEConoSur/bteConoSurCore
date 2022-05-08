package pizzaaxx.bteconosur.server.player.notification;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.server.player.ServerPlayer;
import pizzaaxx.bteconosur.storage.ObjectRepository;

import java.util.UUID;

public class NotificationHandler {

    private final ObjectRepository<ServerPlayer> objectRepository;

    public NotificationHandler(ObjectRepository<ServerPlayer> objectRepository) {
        this.objectRepository = objectRepository;
    }

    public void show(Player player, Notification notification) {
        player.sendMessage(notification.getMessage());
    }

    public void send(UUID identifier, Notification notification) {
        Player player = Bukkit.getPlayer(identifier);

        if (player != null) {
            show(player, notification);
        }

        objectRepository.loadAsync(identifier.toString())
                .whenComplete((serverPlayer, throwable) -> {

                });
    }

}
