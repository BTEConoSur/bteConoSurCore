package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.OldCountry;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.BteConoSur.key;

public class WhereCommand  extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (Bukkit.getOnlinePlayers().size() > 0) {
            CompletableFuture.runAsync(() -> {

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(0, 255, 42));
                java.util.List<String> xis = new ArrayList<>();
                for (String c : "argentina bolivia chile paraguay peru uruguay".split(" ")) {
                    OldCountry country = new OldCountry(c);
                    List<String> names = new ArrayList<>();
                    for (Player p : country.getPlayers()) {
                        names.add(p.getName().replace("_", "\\_"));
                        Coords2D coords = new Coords2D(p.getLocation());
                        xis.add("https://cravatar.eu/helmavatar/" + p.getName() + "/32.png,1,c," + coords.getLat() + "," + coords.getLon());
                    }
                    Collections.sort(names);
                    embed.addField(":flag_" + country.getAbbreviation() + ": " + StringUtils.capitalize(c.replace("peru", "perú")) + ": " + names.size(), (names.size() > 0 ? String.join(", ", names) : "N/A"), true);
                }

                embed.setThumbnail("https://media.discordapp.net/attachments/807694452214333482/845857288609988628/conosur.png?width=471&height=473");

                InputStream file;
                try {
                    file = new URL("https://open.mapquestapi.com/staticmap/v4/getmap?key=" + key + "&scalebar=false&size=1280,720&type=sat&imagetype=png&center=-33.43957706920842,-66.86130716417696&zoom=4&xis=" + String.join(",", xis)).openStream();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }

                embed.setImage("attachment://map.png");
                event.replyFile(file, "map.png").addEmbeds(embed.build()).queue(
                        msg -> msg.deleteOriginal().queueAfter(5, TimeUnit.MINUTES)
                );
            });

        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(255,0,0));
            embed.setAuthor("No hay jugadores online.");
            event.replyEmbeds(embed.build()).queue(
                    msg -> msg.deleteOriginal().queueAfter(1, TimeUnit.MINUTES)
            );
        }

    }
}
