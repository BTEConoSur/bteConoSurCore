package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.CountryPlayer;
import pizzaaxx.bteconosur.helper.DataTime;
import pizzaaxx.bteconosur.helper.DateHelper;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static pizzaaxx.bteconosur.BteConoSur.key;
import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.discord.Bot.conoSurBot;

public class PlayerCommand extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "/";
    private static final String COMMAND = "player";

    private static final Color ERROR_COLOR = new Color(255, 0, 0);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        CompletableFuture.runAsync(() -> {
            Message message = event.getMessage();
            TextChannel textChannel = message.getTextChannel();
            String contentRaw = message.getContentRaw();

            if (contentRaw.startsWith(COMMAND_PREFIX)) {
                String[] args = contentRaw.replaceFirst("/", "")
                        .split(" ");

                if (args.length > 0 && args[0].equals(COMMAND)) {

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    ServerPlayer serverPlayer = null;

                    if (offlinePlayer.hasPlayedBefore()) {
                        serverPlayer = new ServerPlayer(offlinePlayer);
                    } else {
                        String id;

                        if (args[1].startsWith("<@!")) {
                            id = args[1].replace("<@!", "")
                                    .replace(">", "");
                        } else {
                            id = args[1];
                        }

                        User user = conoSurBot.retrieveUserById(id)
                                .complete();

                        if (user != null) {
                            try {
                                serverPlayer = new ServerPlayer(user);
                            } catch (Exception e) {
                                textChannel.sendMessageEmbeds(
                                                new EmbedBuilder()
                                                        .setColor(ERROR_COLOR)
                                                        .setAuthor("El usuario introducido no tiene una cuenta de Minecraft conectada.")
                                                        .build())
                                        .reference(message)
                                        .mentionRepliedUser(false).queue();
                            }
                        }

                    }

                    final boolean[] hasFile = {false};
                    final InputStream[] file = {null};
                    EmbedBuilder embedBuilder = new EmbedBuilder();

                    if (serverPlayer != null) {
                        embedBuilder.setTitle(serverPlayer.getName());
                        offlinePlayer = serverPlayer.getPlayer();

                        if (offlinePlayer.isOnline()) {
                            org.bukkit.entity.Player player = offlinePlayer.getPlayer();
                            Country country = new Country(player.getLocation());
                            Coords2D coords = new Coords2D(player.getLocation());

                            embedBuilder
                                    .setColor(new Color(0, 255, 42))
                                    .addField("Status:", ":green_circle: Online", false)
                                    .addField("Coordenadas:", (country.getCountry().equals("global") ? ":globe_with_meridians" : ":flag_" + country.getAbbreviation() + ":") + " " + coords.getX() + ", " + coords.getZ(), false)
                                    .addField("Coordenadas geográficas:", (country.getCountry().equals("global") ? ":globe_with_meridians" : ":flag_" + country.getAbbreviation() + ":") + " [" + coords.getLat() + ", " + coords.getLon() + "](" + "https://www.google.com/maps/@" + coords.getLat() + "," + coords.getLon() + ",19z" + ")", false);

                            String chatType;
                            String chatName = serverPlayer.getChat().getName();

                            if (chatName.startsWith("project_")) {
                                chatType = ":tools:";
                            } else {
                                chatType = (chatName.equals("global") ? ":earth_americas:" : ":flag_" + new Country(chatName).getAbbreviation() + ":");
                            }
                            embedBuilder.addField("Chat:", chatType + " " + serverPlayer.getChat().getFormattedName(), false);

                            try {
                                file[0] = new URL("https://open.mapquestapi.com/staticmap/v4/getmap?key=" + key + "&size=1280,720&type=sat&scalebar=false&imagetype=png&center=" + coords.getLat() + "," + coords.getLon() + "&zoom=18&xis=https://cravatar.eu/helmavatar/" + player.getName() + "/64.png,1,c," + coords.getLat() + "," + coords.getLon()).openStream();
                                hasFile[0] = true;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                return;
                            }
                            embedBuilder.setImage("attachment://map.png");

                        } else {

                            embedBuilder.setColor(ERROR_COLOR)
                                    .addField("Status:", ":red_circle: Offline", false);

                            DataTime time = DateHelper.differenceToData(
                                            new Date(offlinePlayer.getLastPlayed()),
                                            new Date()
                                    );

                            embedBuilder.addField("Ultima vez conectado: ",
                                    (time.get(DateHelper.Type.MINUTE) > 0 ? "Hace " + (time.get(DateHelper.Type.HOUR) > 0 ? time.get(DateHelper.Type.HOUR) + " hora(s) y " : "") + time.get(DateHelper.Type.MINUTE) + " minuto(s)" : "Recién"), false);

                        }

                        if (serverPlayer.hasDiscordUser()) {
                            embedBuilder.addField("Discord:", serverPlayer.getDiscordUser().getAsMention(), false);
                        } else {
                            embedBuilder.addField("Discord:", "No conectado.", false);
                        }

                        embedBuilder.addField("Rango:",
                                new YamlManager(pluginFolder, "discord/groupEmojis.yml").getValue(serverPlayer.getPrimaryGroup()) + " " + serverPlayer.getPrimaryGroup().replace("default", "visita").toUpperCase(), false);

                        if (serverPlayer.getSecondaryGroups().size() > 0) {
                            List<String> ranks = new ArrayList<>();
                            for (String rank : serverPlayer.getSecondaryGroups()) {
                                ranks.add(new YamlManager(pluginFolder, "discord/groupEmojis.yml").getValue(rank) + " " + rank.replace("donator", "donador").toUpperCase());
                            }
                            embedBuilder.addField("Rangos secundarios:", String.join("\n", ranks), false);
                        }

                        if (serverPlayer.getMaxPoints() > 0) {
                            List<String> points = new ArrayList<>();
                            List<CountryPlayer> list = new ArrayList<>();
                            for (String c : "bolivia chile paraguay peru uruguay".split(" ")) {
                                if (serverPlayer.getPoints(new Country(c)) > 0) {
                                    list.add(new CountryPlayer(serverPlayer, new Country(c)));
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
                                points.add("• :flag_" + countryPlayer.getCountry().getAbbreviation() + ": " + bRank + " " + StringUtils.capitalize(countryPlayer.getCountry().getCountry().replace("peru", "perú")) + ": " + serverPlayer.getPoints(countryPlayer.getCountry()));
                            }
                            embedBuilder.addField("Puntos:", String.join("\n", points), false);
                        }

                        embedBuilder.addField("Proyectos terminados:", Integer.toString(serverPlayer.getTotalFinishedProjects()), false);

                        if (serverPlayer.getProjects().size() > 0) {
                            List<String> projects = new ArrayList<>();
                            for (Project project : serverPlayer.getOwnedProjects()) {
                                projects.add("• :flag_" + new Country(project.getCountry()).getAbbreviation() + ": " + project.getDifficulty().replace("facil", ":green_circle:").replace("intermedio", ":yellow_circle:").replace("dificil", ":red_circle:") + " :crown: `" + project.getId() + "`" + ((!project.getName().equals(project.getId())) ? " - " + project.getName() : ""));
                            }

                            for (Project project : serverPlayer.getProjects()) {
                                if (project.getOwner() != serverPlayer.getPlayer()) {
                                    projects.add("• :flag_" + new Country(project.getCountry()).getAbbreviation() + ": " + project.getDifficulty().replace("facil", ":green_circle:").replace("intermedio", ":yellow_circle:").replace("dificil", ":red_circle:") + " `" + project.getId() + "`" + ((!project.getName().equals(project.getId())) ? " - " + project.getName() : ""));
                                }
                                if (projects.size() >= 15) {
                                    projects.add("y " + (serverPlayer.getProjects().size() - 15) + " más...");
                                    break;
                                }
                            }
                            embedBuilder.addField("Proyectos activos (Total: " + serverPlayer.getProjects().size() + "):", String.join("\n", projects), false);
                        }

                        embedBuilder.setThumbnail("https://mc-heads.net/head/" + serverPlayer.getPlayer().getUniqueId());

                        if (hasFile[0]) {
                            textChannel.sendFile(file[0], "map.png").setEmbeds(embedBuilder.build()).reference(message).mentionRepliedUser(false).queue();
                        } else {
                            textChannel.sendMessageEmbeds(embedBuilder.build()).reference(message).mentionRepliedUser(false).queue();
                        }

                    } else {
                        errorInsertAPlayer(textChannel, message);
                    }

                    return;
                }
                errorInsertAPlayer(textChannel, message);
            }
        });
    }

    public void errorInsertAPlayer(TextChannel textChannel, Message message) {
        textChannel.sendMessageEmbeds(
                        new EmbedBuilder()
                                .setColor(ERROR_COLOR)
                                .setAuthor("Introduce un jugador, menciona a un usuario o introduce su ID.")
                                .build()
                ).reference(message.getReferencedMessage())
                .mentionRepliedUser(false).queue();
    }

}
