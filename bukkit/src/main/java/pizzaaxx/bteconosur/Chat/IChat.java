package pizzaaxx.bteconosur.Chat;

import java.util.Set;
import java.util.UUID;

public interface IChat {

    String CHAT_PREFIX = "§f[§aCHAT§f] §7>>§r ";

    String getId();

    String getDisplayName();

    String getDiscordEmoji();

    Set<UUID> getMembers();

    void sendMessage(String message, UUID member);

    void broadcast(String message);

    void broadcast(String message, boolean ignoreHidden);

    void receiveMember(UUID uuid);

    void sendMember(UUID uuid, IChat chat);

    void quitMember(UUID uuid);

}
