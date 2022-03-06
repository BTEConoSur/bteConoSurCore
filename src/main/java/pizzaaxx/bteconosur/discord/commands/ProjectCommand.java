package pizzaaxx.bteconosur.discord.commands;

import com.sk89q.worldedit.BlockVector2D;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.misc.Misc;
import pizzaaxx.bteconosur.player.data.PlayerData;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static pizzaaxx.bteconosur.BteConoSur.*;

public class ProjectCommand implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getMessage().getContentRaw().startsWith("/")) {
                String fullCommand = e.getMessage().getContentRaw();
                String[] args = fullCommand.replaceFirst("/", "").split(" ");
                if (args.length > 0) {
                    if (args[0].equals("project")) {
                        if (args.length > 1 && args[1].matches("[a-z]{6}")) {
                            try {
                                Project project = new Project(args[1]);

                                // DIFICULTAD

                                CompletableFuture.runAsync(() -> {

                                    EmbedBuilder embed = new EmbedBuilder();

                                    if (project.getDifficulty().equals("dificil")) {
                                        embed.setColor(new Color(255, 0, 0));
                                        embed.addField(":tools: Dificultad: ", ":red_circle: Difícil", false);
                                    } else if (project.getDifficulty().equals("intermedio")) {
                                        embed.setColor(new Color(255, 220, 0));
                                        embed.addField(":tools: Dificultad: ", ":yellow_circle: Intermedio", false);
                                    } else {
                                        embed.setColor(new Color(0, 255, 42));
                                        embed.addField(":tools: Dificultad: ", ":green_circle: Fácil", false);
                                    }

                                    // THUMBNAIL

                                    if (project.getOwnerOld() != null) {
                                        embed.setThumbnail("https://mc-heads.net/head/" + project.getOwnerOld().getUniqueId().toString());
                                    }

                                    // NOMBRE

                                    if (!Objects.equals(project.getName(), project.getId())) {
                                        embed.setTitle("Proyecto \"" + project.getName() + "\" (ID: " + project.getId().toUpperCase() + ")");
                                    } else {
                                        embed.setTitle("Proyecto " + project.getId().toUpperCase());
                                    }

                                    // PAIS

                                    embed.addField(":globe_with_meridians: País:", ":flag_" + Misc.getCountryPrefix(project.getOldCountry()) + ": " + WordUtils.capitalize(project.getOldCountry()), false);

                                    // ETIQUETA

                                    if (project.getTag() != null) {
                                        embed.addField(":label: Etiqueta:", project.getTag()
                                                .replace("edificios", ":cityscape: Edificios")
                                                .replace("casas", ":house_with_garden: Casas")
                                                .replace("departamentos", ":hotel: Departamentos")
                                                .replace("centros_comerciales", ":shpping_bags: Centros Comerciales")
                                                .replace("establecimientos", ":hospital: Establecimientos")
                                                .replace("parques", ":deciduous_tree: Parques")
                                                .replace("carreteras", ":motorway: Carreteras"), false);
                                    }

                                    // GOOGLE MAPS

                                    double minX = project.getPoints().get(0).getX();
                                    double maxX = project.getPoints().get(0).getX();
                                    double minZ = project.getPoints().get(0).getZ();
                                    double maxZ = project.getPoints().get(0).getZ();

                                    for (BlockVector2D point : project.getPoints()) {
                                        if (point.getX() > maxX) {
                                            maxX = point.getX();
                                        }
                                        if (point.getX() < minX) {
                                            minX = point.getX();
                                        }
                                        if (point.getZ() > maxZ) {
                                            maxZ = point.getZ();
                                        }
                                        if (point.getZ() < minZ) {
                                            minZ = point.getZ();
                                        }
                                    }

                                    Coords2D geoCoord = new Coords2D(new Location(mainWorld, (minX + maxX) / 2, 100, (minZ + maxZ) / 2));

                                    embed.addField(":round_pushpin: Coordenadas:", ("> " + geoCoord.getX() + " " + geoCoord.getHighestY() + " " + geoCoord.getZ()).replace(".5", "").replace(".0", ""), false);

                                    embed.addField(":map: Google Maps:", "https://www.google.com/maps/@" + geoCoord.getLat() + "," + geoCoord.getLon() + ",19z", false);


                                    // LIDER

                                    if (project.getOwnerOld() != null) {
                                        embed.addField(":crown: Líder:", ((String) new PlayerData(project.getOwnerOld()).getData("name")).replace("_", "\\_"), false);
                                    }

                                    // MIEMBROS

                                    if (project.getMembersOld() != null) {
                                        List<String> members = new ArrayList<>();
                                        for (OfflinePlayer player : project.getMembersOld()) {
                                            members.add(((String) new PlayerData(player).getData("name")).replace("_", "\\_"));
                                        }
                                        embed.addField(":busts_in_silhouette: Miembros:", String.join(", ", members), false);
                                    }

                                    // IMAGE

                                    InputStream file;
                                    try {
                                        file = new URL(project.getImageUrl()).openStream();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                        return;
                                    }

                                    embed.setImage("attachment://map.png");
                                    e.getTextChannel().sendFile(file, "map.png").setEmbeds(embed.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                                });

                            } catch (Exception exception) {
                                exception.printStackTrace();
                                EmbedBuilder error = new EmbedBuilder();
                                error.setColor(new Color(255, 0, 0));
                                error.setAuthor("Este proyecto no existe.");

                                e.getMessage().replyEmbeds(error.build()).mentionRepliedUser(false).queue();
                            }
                        } else {
                            EmbedBuilder error = new EmbedBuilder();
                            error.setColor(new Color(255, 0, 0));
                            error.setAuthor("Introduce un proyecto válido.");

                            e.getMessage().replyEmbeds(error.build()).mentionRepliedUser(false).queue();
                        }
                    }

                    if (args[0].equals("pending")) {
                        if (YamlManager.getYamlData(pluginFolder, "pending_projects/pending.yml").size() != 0) {
                            EmbedBuilder pending = new EmbedBuilder();
                            pending.setColor(new Color(0, 255, 42));

                            List<String> lines = new ArrayList<>();

                            List<String> projects = (List<String>) YamlManager.getYamlData(pluginFolder, "pending_projects/pending.yml").get("pending");

                            for (String str : projects) {
                                try {
                                    Project project = new Project(str);

                                    String line = "• :flag_" +
                                            Misc.getCountryPrefix(project.getOldCountry()) +
                                            ": " +
                                            project.getId();

                                    if (!Objects.equals(project.getName(), project.getId())) {
                                        line = line + " - " + project.getName();
                                    }

                                    lines.add(line);

                                } catch (Exception ignored) {
                                }
                            }

                            Collections.sort(lines);
                            String value = String.join("\n", lines);

                            pending.addField("Proyectos pendientes de revisión:", value, false);

                            e.getTextChannel().sendMessageEmbeds(pending.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                        } else {
                            EmbedBuilder noPending = new EmbedBuilder();
                            noPending.setColor(new Color(255,0,0));
                            noPending.setAuthor("No hay proyectos pendientes de revisión.");
                            e.getTextChannel().sendMessageEmbeds(noPending.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                        }
                    }
                }
            }
        }
    }
}
