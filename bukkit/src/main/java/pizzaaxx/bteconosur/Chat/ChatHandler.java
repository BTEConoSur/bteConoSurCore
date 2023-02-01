package pizzaaxx.bteconosur.Chat;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;

import java.util.HashMap;
import java.util.Map;

public class ChatHandler {

    private final BTEConoSur plugin;
    private final Map<String, Chat> chats = new HashMap<>();


    public ChatHandler(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public boolean isLoaded(String name) {
        return chats.containsKey(name);
    }

    public void registerChat(Chat chat) {
        chats.put(chat.getID(), chat);
    }

    public void tryUnregister(@NotNull Chat chat) {
        if (chat.isUnloadable()) {
            if (chat.getPlayers().isEmpty()) {
                chats.remove(chat.getID());
            }
        }
    }

    public Chat getChat(String name) {
        return chats.get(name);
    }

}
