package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.CountryPlayer;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static pizzaaxx.bteconosur.bteConoSur.key;
import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.discord.bot.conoSurBot;

public class player implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getMessage().getContentRaw().startsWith("/")) {
                String fullCommand = e.getMessage().getContentRaw();
                String[] args = fullCommand.replaceFirst("/", "").split(" ");
                if (args.length > 0) {
                    if (args[0].equals("player")) {
                        if (args.length > 1) {
                            ServerPlayer sRaw = null;
                            if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                                sRaw = new ServerPlayer(Bukkit.getOfflinePlayer(args[1]));
                            } else {
                                String id;
                                if (args[1].startsWith("<@!")) {
                                    id = args[1].replace("<@!", "").replace(">", "");
                                } else {
                                    id = args[1];
                                }

                                User user = conoSurBot.retrieveUserById(id).complete();

                                if (user != null) {
                                    try {
                                        sRaw = new ServerPlayer(user);
                                    } catch (Exception exception) {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setColor(new Color(255, 0, 0));
                                        embed.setAuthor("El usuario introducido no tiene una cuenta de Minecraft conectada.");
                                        e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                                        return;
                                    }
                                }
                            }

                            EmbedBuilder embed = new EmbedBuilder();
                            final boolean[] hasFile = {false};
                            final InputStream[] file = {null};
                            if (sRaw != null) {
                                ServerPlayer s = sRaw;
                                CompletableFuture.runAsync(() -> {
                                    embed.setTitle(s.getName());
                                    if (s.getPlayer().isOnline()) {
                                        Player p = (Player) s.getPlayer();
                                        embed.setColor(new Color(0, 255, 42));
                                        embed.addField("Status:", ":green_circle: Online", false);
                                        Country country = new Country(p.getLocation());
                                        Coords2D coords = new Coords2D(p.getLocation());
                                        embed.addField("Coordenadas:", (country.getCountry().equals("global") ? ":globe_with_meridians" : ":flag_" + country.getAbbreviation() + ":") + " " + coords.getX() + ", " + coords.getZ(), false);
                                        embed.addField("Coordenadas geográficas:", (country.getCountry().equals("global") ? ":globe_with_meridians" : ":flag_" + country.getAbbreviation() + ":") + " [" + coords.getLat() + ", " + coords.getLon() + "](" + "https://www.google.com/maps/@" + coords.getLat() + "," + coords.getLon() + ",19z" + ")", false);
                                        String chat;
                                        if (s.getChat().getName().startsWith("project_")) {
                                            chat = ":tools:";
                                        } else {
                                            chat = (s.getChat().getName().equals("global") ? ":earth_americas:" : ":flag_" + new Country(s.getChat().getName()).getAbbreviation() + ":");
                                        }
                                        embed.addField("Chat:", chat + " " + s.getChat().getFormattedName(), false);

                                        try {
                                            file[0] = new URL("https://open.mapquestapi.com/staticmap/v4/getmap?key=" + key + "&size=1280,720&type=sat&scalebar=false&imagetype=png&center=" + coords.getLat() + "," + coords.getLon() + "&zoom=18&xis=https://cravatar.eu/helmavatar/" + p.getName() + "/64.png,1,c," + coords.getLat() + "," + coords.getLon()).openStream();
                                            hasFile[0] = true;
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                            return;
                                        }
                                        embed.setImage("attachment://map.png");

                                    } else {
                                        embed.setColor(new Color(255, 0, 0));
                                        embed.addField("Status:", ":red_circle: Offline", false);
                                    }

                                    if (s.hasDiscordUser()) {
                                        embed.addField("Discord:", s.getDiscordUser().getAsMention(), false);
                                    } else {
                                        embed.addField("Discord:", "No conectado.", false);
                                    }

                                    embed.addField("Rango:", new YamlManager(pluginFolder, "discord/groupEmojis.yml").getValue(s.getPrimaryGroup()) + " " + s.getPrimaryGroup().replace("default", "visita").toUpperCase(), false);

                                    if (s.getSecondaryGroups().size() > 0) {
                                        List<String> ranks = new ArrayList<>();
                                        for (String rank : s.getSecondaryGroups()) {
                                            ranks.add(new YamlManager(pluginFolder, "discord/groupEmojis.yml").getValue(rank) + " " + rank.replace("donator", "donador").toUpperCase());
                                        }
                                        embed.addField("Rangos secundarios:", String.join("\n", ranks), false);
                                    }

                                    if (s.getMaxPoints() > 0) {
                                        List<String> points = new ArrayList<>();
                                        List<CountryPlayer> list = new ArrayList<>();
                                        for (String c : "bolivia chile paraguay peru uruguay".split(" ")) {
                                            if (s.getPoints(new Country(c)) > 0) {
                                                list.add(new CountryPlayer(s, new Country(c)));
                                            }
                                        }

                                        Collections.sort(list);

                                        for (CountryPlayer countryPlayer : list) {
                                            String bRank;
                                            if (countryPlayer.getPoints() >= 1000) {
                                                bRank = ":gem:";
                                            } else if (countryPlayer.getPoints() >= 500) {
                                                bRank = ":crossed_swords:";
                                            } else if (countryPlayer.getPoints() >= 150) {
                                                bRank = ":hammer_pick:";
                                            } else {
                                                bRank = ":hammer:";
                                            }
                                            points.add("• :flag_" + countryPlayer.getCountry().getAbbreviation() + ": " + bRank + " " + StringUtils.capitalize(countryPlayer.getCountry().getCountry().replace("peru", "perú")) + ": " + s.getPoints(countryPlayer.getCountry()));
                                        }
                                        embed.addField("Puntos:", String.join("\n", points), false);
                                    }

                                    embed.addField("Proyectos terminados:", Integer.toString(s.getTotalFinishedProjects()), false);

                                    if (s.getProjects().size() > 0) {
                                        List<String> projects = new ArrayList<>();
                                        for (Project project : s.getOwnedProjects()) {
                                            projects.add("• :flag_" + new Country(project.getCountry()).getAbbreviation() + ": " + project.getDifficulty().replace("facil", ":green_circle:").replace("intermedio", ":yellow_circle:").replace("dificil", ":red_circle:") + " :crown: `" + project.getId() + "`" + ((!project.getName().equals(project.getId())) ? " - " + project.getName() : ""));
                                        }

                                        for (Project project :s.getProjects()) {
                                            if (project.getOwner() != s.getPlayer()) {
                                                projects.add("• :flag_" + new Country(project.getCountry()).getAbbreviation() + ": " + project.getDifficulty().replace("facil", ":green_circle:").replace("intermedio", ":yellow_circle:").replace("dificil", ":red_circle:") + " `" + project.getId() + "`" + ((!project.getName().equals(project.getId())) ? " - " + project.getName() : ""));
                                            }
                                            if (projects.size() >= 15) {
                                                projects.add("y " + (s.getProjects().size() - 15) + " más...");
                                                break;
                                            }
                                        }
                                        embed.addField("Proyectos activos (Total: " + s.getProjects().size() + "):", String.join("\n", projects), false);
                                    }

                                    embed.setThumbnail("https://mc-heads.net/head/" + s.getPlayer().getUniqueId().toString());

                                    if (hasFile[0]) {
                                        e.getTextChannel().sendFile(file[0], "map.png").setEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                                    } else {

                                        e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                                    }
                                });
                            } else {
                                embed.setColor(new Color(255, 0, 0));
                                embed.setAuthor("Introduce un jugador, menciona a un usuario o introduce su ID.");
                                e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                            }
                        } else {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(new Color(255, 0, 0));
                            embed.setAuthor("Introduce un jugador, menciona a un usuario o introduce su ID.");
                            e.getTextChannel().sendMessageEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                        }
                    }
                }
            }
        }
    }
}
