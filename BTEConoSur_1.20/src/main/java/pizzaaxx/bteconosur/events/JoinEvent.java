package pizzaaxx.bteconosur.events;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.chat.ChatManager;

import java.awt.*;

import static pizzaaxx.bteconosur.utilities.BackCommand.BACK_LOCATIONS;

public class JoinEvent implements Listener {

    private final BTEConoSurPlugin plugin;

    public JoinEvent(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        OnlineServerPlayer serverPlayer = (OnlineServerPlayer) plugin.getPlayerRegistry().get(event.getPlayer().getUniqueId());
        serverPlayer.getScoreboardManager().startBoard();
        plugin.getPlayerClickEvent().registerProtector(event.getPlayer().getUniqueId());
        BACK_LOCATIONS.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(
                plugin.getPlayerRegistry().get(event.getPlayer().getUniqueId()).getName() + " ha entrado al servidor.",
                null,
                "https://mc-heads.net/avatar/" + event.getPlayer().getUniqueId()
        );
        builder.setColor(Color.GREEN);
        plugin.getCountriesRegistry().getCountries().forEach(
                country -> country.getChat().sendMessageEmbeds(builder.build()).queue()
        );

        plugin.getChatHandler().addToChat(serverPlayer.getUUID(), plugin);
        serverPlayer.getChatManager().setCurrentChat(plugin);

    }

}
