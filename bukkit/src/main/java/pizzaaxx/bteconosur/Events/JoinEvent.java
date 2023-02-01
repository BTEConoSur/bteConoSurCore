package pizzaaxx.bteconosur.Events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;

import java.awt.*;

public class JoinEvent implements Listener {

    private final BTEConoSur plugin;

    public JoinEvent(BTEConoSur bteConoSur) {
        this.plugin = bteConoSur;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        plugin.getPlayerRegistry().load(event.getPlayer().getUniqueId());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setAuthor(plugin.getPlayerRegistry().get(event.getPlayer().getUniqueId()).getName() + " ha entrado al servidor.", null,
                "https://cravatar.eu/helmavatar/" + event.getPlayer().getUniqueId() + "/190.png");
        MessageEmbed embed = embedBuilder.build();
        for (Country country : plugin.getCountryManager().getAllCountries()) {
            country.getGlobalChatChannel().sendMessageEmbeds(embed).queue();
            country.getCountryChatChannel().sendMessageEmbeds(embed).queue();
        }
    }
}
