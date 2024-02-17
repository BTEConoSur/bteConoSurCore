package pizzaaxx.bteconosur.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;

import java.awt.*;
import java.sql.SQLException;

public class QuitEvent implements Listener {

    private final BTEConoSurPlugin plugin;

    public QuitEvent(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    // REPLACES THE CURRENT SERVERPLAYER OBJECT IN REGISTRY WITH AN OFFLINE VERSION OF ITSELF.
    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        try {
            OnlineServerPlayer s = plugin.getPlayerRegistry().get(event.getPlayer().getUniqueId()).asOnlinePlayer();
            plugin.getChatHandler().removeFromChat(event.getPlayer().getUniqueId(), s.getChatManager().getCurrentChat());
        } catch (SQLException | JsonProcessingException ignored) {}

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(
                plugin.getPlayerRegistry().get(event.getPlayer().getUniqueId()).getName() + " ha salido del servidor.",
                null,
                "https://mc-heads.net/avatar/" + event.getPlayer().getUniqueId()
        );
        builder.setColor(Color.RED);
        plugin.getCountriesRegistry().getCountries().forEach(
                country -> country.getChat().sendMessageEmbeds(builder.build()).queue()
        );

        plugin.getPlayerRegistry().quit(event.getPlayer().getUniqueId());
        plugin.getPlayerClickEvent().unregisterProtector(event.getPlayer().getUniqueId());

    }
}

