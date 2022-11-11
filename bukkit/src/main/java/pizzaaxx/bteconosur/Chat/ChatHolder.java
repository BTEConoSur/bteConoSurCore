package pizzaaxx.bteconosur.Chat;

import java.util.UUID;

public interface ChatHolder {

    String getChatID();

    String getChatEmoji();

    String getChatDisplayName();

    void move(UUID uuid, ChatHolder newHolder);

    void remove(UUID uuid);

    void add(UUID uuid);

    void sendMessage(UUID uuid, ChatMessage message);

    void broadcast(ChatMessage message);

    void broadcast(ChatMessage message, boolean ignoreHidden);

}
