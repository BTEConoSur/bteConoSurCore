package pizzaaxx.bteconosur.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ChatProvider {

    Map<String, ChatProvider> CHAT_PROVIDERS = new HashMap<>();

    @Nullable
    Chat getChat(String id);

    @NotNull
    String getProviderId();

    @NotNull
    List<? extends Chat> getAvailableForPlayer(Player player);

}
