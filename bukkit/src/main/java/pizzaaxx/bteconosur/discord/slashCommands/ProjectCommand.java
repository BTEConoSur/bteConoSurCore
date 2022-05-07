package pizzaaxx.bteconosur.discord.slashCommands;

import com.sk89q.worldedit.BlockVector2D;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class ProjectCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("project")) {
            String id = event.getOption("id").getAsString().toLowerCase();

            if (id.matches("[a-z]{6}")) {
                try {
                    Project project = new Project(id);

                    // DIFICULTAD

                    CompletableFuture.runAsync(() -> {

                        EmbedBuilder embed = new EmbedBuilder();

                        if (project.getDifficulty() ==  Project.Difficulty.DIFICIL) {
                            embed.setColor(new Color(255, 0, 0));
                            embed.addField("Dificultad: ", ":red_circle: Difícil", false);
                        } else if (project.getDifficulty() == Project.Difficulty.INTERMEDIO) {
                            embed.setColor(new Color(255, 220, 0));
                            embed.addField("Dificultad: ", ":yellow_circle: Intermedio", false);
                        } else {
                            embed.setColor(new Color(0, 255, 42));
                            embed.addField("Dificultad: ", ":green_circle: Fácil", false);
                        }

                        // THUMBNAIL

                        if (project.getOwner() != null) {
                            embed.setThumbnail("https://mc-heads.net/head/" + project.getOwner().getUniqueId().toString());
                        }

                        // NOMBRE

                        if (!Objects.equals(project.getName(), project.getId())) {
                            embed.setTitle("Proyecto \"" + project.getName() + "\" (ID: " + project.getId().toUpperCase() + ")");
                        } else {
                            embed.setTitle("Proyecto " + project.getId().toUpperCase());
                        }

                        // PAIS

                        embed.addField("País:", ":flag_" + project.getCountry().getAbbreviation() + ": " + StringUtils.capitalize(project.getCountry().getName()), false);

                        // ETIQUETA

                        if (project.getTag() != null) {
                            embed.addField(":label: Etiqueta:", project.getTag().toString().toLowerCase()
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

                        embed.addField("Coordenadas:", (":round_pushpin: " + geoCoord.getX() + " " + geoCoord.getHighestY() + " " + geoCoord.getZ()).replace(".5", "").replace(".0", ""), false);

                        embed.addField("Google Maps:", "https://www.google.com/maps/@" + geoCoord.getLat() + "," + geoCoord.getLon() + ",19z", false);


                        // LIDER

                        if (project.getOwner() != null) {
                            embed.addField("Líder:", new ServerPlayer(project.getOwner()).getName(), false);
                        }

                        // MIEMBROS

                        if (!project.getMembers().isEmpty()) {
                            List<String> members = new ArrayList<>();
                            for (OfflinePlayer player : project.getMembers()) {
                                members.add(new ServerPlayer(player).getName().replace("_", "\\_"));
                            }
                            embed.addField("Miembros:", String.join(", ", members), false);
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
                        event.replyFile(file, "map.png").addEmbeds(embed.build()).queue(
                                msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                        );
                    });

                } catch (Exception exception) {
                    exception.printStackTrace();
                    EmbedBuilder error = new EmbedBuilder();
                    error.setColor(new Color(255, 0, 0));
                    error.setAuthor("Este proyecto no existe.");

                    event.replyEmbeds(error.build()).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                }
            } else {
                EmbedBuilder error = new EmbedBuilder();
                error.setColor(new Color(255, 0, 0));
                error.setAuthor("Introduce un proyecto válido.");

                event.replyEmbeds(error.build()).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                );
            }
        }
    }
}