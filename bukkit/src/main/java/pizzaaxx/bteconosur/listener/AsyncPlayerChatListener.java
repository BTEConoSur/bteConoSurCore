package pizzaaxx.bteconosur.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pizzaaxx.bteconosur.server.player.NewServerPlayer;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;
import pizzaaxx.bteconosur.yaml.Configuration;

public class AsyncPlayerChatListener implements Listener {

    private final PlayerRegistry playerRegistry;
    private final Configuration chatsConfiguration;

    public AsyncPlayerChatListener(PlayerRegistry playerRegistry,
                                   Configuration chatsConfiguration) {
        this.playerRegistry = playerRegistry;
        this.chatsConfiguration = chatsConfiguration;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();

        NewServerPlayer newServerPlayer =
                playerRegistry.get(player.getUniqueId());

        String channelChat = newServerPlayer.getChannelChat();
        event.setCancelled(true);

        String format = chatsConfiguration.getString(channelChat + ".format");
        String permission = chatsConfiguration.getStringWithoutColors(channelChat + ".permission");

        format = format.replace("%player%", player.getName())
                .replace("%message%", message);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (permission != null && onlinePlayer.hasPermission(permission)) {
                onlinePlayer.sendMessage(
                        format
                );
            }
        }

    }

}
