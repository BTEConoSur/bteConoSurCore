package pizzaaxx.bteconosur.Chat;

import java.util.UUID;

public interface ChatHolder {

    String getChatID();

    String getChatEmoji();

    String getChatDisplayName();

    /**
     * Move a player to a new chat.
     * @param uuid The UUID of the player.
     * @param newHolder The new chat.
     */
    void moveToChat(UUID uuid, ChatHolder newHolder);

    /**
     * Remove a player from this chat.
     * @param uuid The UUID of the player.
     */
    void removeFromChat(UUID uuid, boolean disableDiscord);

    /**
     * Add a player to this chat.
     * @param uuid The UUID of the player.
     */
    void addToChat(UUID uuid, boolean disableDiscord);

    void sendMessage(UUID uuid, ChatMessage message);

    void broadcast(ChatMessage message);

    void broadcast(ChatMessage message, boolean ignoreHidden);

}
