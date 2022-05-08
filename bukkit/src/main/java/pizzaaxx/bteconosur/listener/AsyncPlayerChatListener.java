package pizzaaxx.bteconosur.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;

public class AsyncPlayerChatListener implements Listener {

    private final PlayerRegistry playerRegistry;

    public AsyncPlayerChatListener(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

    }

}
