package pizzaaxx.bteconosur.Player.Managers;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.ChatMessage;
import pizzaaxx.bteconosur.Chat.Components.ChatMessageComponent;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Player.ServerPlayer;

import java.sql.ResultSet;

public class ChatManager {

    private final BTEConoSur plugin;
    private final ServerPlayer serverPlayer;

    public ChatManager(ServerPlayer serverPlayer, BTEConoSur plugin, ResultSet set) {
        this.plugin = plugin;
        this.serverPlayer = serverPlayer;
    }

    public void sendMessage(@NotNull Prefixable prefixable, @NotNull ChatMessage message) {
        ChatMessage newMessage = new ChatMessage(prefixable.getPrefix());
        for (ChatMessageComponent component : message.getChatComponents()) {
            newMessage.append(component);
        }
        Bukkit.getPlayer(serverPlayer.getUuid()).sendMessage(newMessage.getBaseComponents());
    }

    public void sendMessage(@NotNull ChatMessage message) {
        Bukkit.getPlayer(serverPlayer.getUuid()).sendMessage(message.getBaseComponents());
    }

}
