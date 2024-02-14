package pizzaaxx.bteconosur.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;

public interface Chat {

    String getChatId();

    void sendMessage(Chat origin, OnlineServerPlayer player, Component message);

    String getProviderId();

}
