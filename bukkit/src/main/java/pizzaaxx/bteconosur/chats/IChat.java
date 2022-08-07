package pizzaaxx.bteconosur.chats;

import java.util.Set;
import java.util.UUID;

public interface IChat {

    String getDisplayName();

    String getDiscordEmoji();

    Set<UUID> getMembers();

    void sendMessage(String message, UUID member);

    void broadcast(String message);

    void receiveMember(UUID uuid);

    void sendMember(IChat chat);

}
