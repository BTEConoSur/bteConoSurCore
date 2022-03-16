package pizzaaxx.bteconosur.chats;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class ChatRegistry {
    private final Map<String, Chat> registry = new HashMap<>();

    public boolean exists(@NotNull String name) {
        return (registry.containsKey(name));
    }

    private Chat forceGet(String name) {
        if (!exists(name)) {
            register(name);
        }
        return get(name);
    }

    public void register(@NotNull String name) {
        if (!registry.containsKey(name)) {
            registry.put(name, new Chat(name));
        }
    }

    public void register(@NotNull Chat chat) {
        String name = chat.getName();
        if (!registry.containsKey(name)) {
            registry.put(name, chat);
        }
    }

    public Chat get(@NotNull String name) {
        return registry.get(name);
    }

    public void movePlayer(ServerPlayer player, String name) {
        Chat oldChat = player.getChatManager().getChat();
        Chat newChat = forceGet(name);
        if (!oldChat.getName().equals(newChat.getName())) {
            oldChat.removeMember((Player) player.getPlayer());
            newChat.addMember((Player) player.getPlayer());
        }
    }

    public boolean contains(@NotNull String name) {
        return registry.containsKey(name);
    }
}
