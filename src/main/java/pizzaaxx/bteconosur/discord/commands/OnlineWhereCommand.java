package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.Country;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static pizzaaxx.bteconosur.BteConoSur.key;

public class OnlineWhereCommand implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getMessage().getContentRaw().startsWith("/")) {
                String fullCommand = e.getMessage().getContentRaw();
                String[] args = fullCommand.replaceFirst("/", "").split(" ");
                if (args.length > 0) {
                    if (args[0].equals("online")) {
                        if (Bukkit.getOnlinePlayers().size() > 0) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(new Color(0, 255, 42));
                            List<String> names = new ArrayList<>();
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                names.add(p.getName().replace("_", "\\_"));
                            }
                            Collections.sort(names);
                            embed.addField("Hay " + Bukkit.getOnlinePlayers().size() + " jugador" + (Bukkit.getOnlinePlayers().size() == 1 ? "" : "es") + " online:", String.join(", ", names), false);
                            e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                        } else {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(new Color(255,0,0));
                            embed.setAuthor("No hay jugadores online.");
                            e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                        }
                    }
                    
                    if (args[0].equals("where")) {
                        if (Bukkit.getOnlinePlayers().size() > 0) {
                            CompletableFuture.runAsync(() -> {

                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setColor(new Color(0, 255, 42));
                                List<String> xis = new ArrayList<>();
                                for (String c : "argentina bolivia chile paraguay peru uruguay".split(" ")) {
                                    Country country = new Country(c);
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
                                e.getTextChannel().sendFile(file, "map.png").setEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                            });

                        } else {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(new Color(255,0,0));
                            embed.setAuthor("No hay jugadores online.");
                            e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                        }
                    }
                }
            }
        }
    }
}
