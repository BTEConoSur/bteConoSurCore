package pizzaaxx.bteconosur.Chat.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.ChatMessage;

public class ChatEventsListener implements Listener {

    private final BTEConoSur plugin;

    public ChatEventsListener(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        ChatMessage message = new ChatMessage(event.getMessage());
        plugin.sendMessage(event.getPlayer().getUniqueId(), message);
    }

}
