package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.bukkit.Bukkit;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.server.player.DiscordManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;

public class projectTagCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("projecttag")) {
            String id = event.getOption("id").getAsString();

            if (Project.projectExists(id)) {

                Project project = new Project(id);

                if (DiscordManager.isLinked(event.getUser().getId())) {
                    ServerPlayer s;
                    try {
                        s = new ServerPlayer(event.getUser());
                    } catch (Exception e) {
                        event.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                                msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                        );
                        return;
                    }

                    if (s.getPermissionCountries().contains(project.getCountry().getName())) {
                        Bukkit.getConsoleSender().sendMessage("b");


                        ActionRow row = ActionRow.of(
                                SelectMenu.create(project.getId())
                                        .setPlaceholder("Selecciona una etiqueta")
                                        .addOption("Edificios", "EDIFICIOS", "Edificios de oficinas, comerciales y otros.")
                                        .addOption("Departamentos", "DEPARTAMENTOS", "Edificios exclusivamente de departamentos.")
                                        .addOption("Parques", "PARQUES", "Parques, plazas, bosques, canchas de deportes, etc.")
                                        .addOption("Casas", "CASAS", "Casas, townhouses y condominios residenciales.")
                                        .addOption("Establecimientos", "ESTABLECIMIENTOS", "Colegios, hospitales, gimnasios, universidades y otros lugares públicos.")
                                        .addOption("Carreteras", "CARRETERAS", "Carreteras, autopistas, calles, pasos elevados, túneles, etc.")
                                        .addOption("Centros Comerciales", "CENTROS_COMERCIALES", "Malls, supermercados y otros centros comerciales.")
                                        .addOption("Eliminar", "delete", "Elimina la etiqueta actual.")
                                        .build()
                        );

                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.YELLOW);
                        builder.setTitle("Elige una etiqueta para el proyecto " + project.getId().toUpperCase());
                        // IMAGE
                        Bukkit.getConsoleSender().sendMessage("c");
                        URL url;
                        try {
                            url = new URL(project.getImageUrl());
                        } catch (MalformedURLException e) {
                            event.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                            );
                            return;
                        }
                        Bukkit.getConsoleSender().sendMessage("d");


                        InputStream stream;
                        try {
                            stream = url.openStream();
                        } catch (IOException e) {
                            event.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                            );
                            return;
                        }
                        Bukkit.getConsoleSender().sendMessage("e");


                        builder.setImage("attachment://map.png");
                        event.replyFile(stream, "map.png").addEmbeds(builder.build()).addActionRows(row).queue(
                                msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                        );



                    } else {
                        event.replyEmbeds(errorEmbed("No tienes permiso para manejar proyectos de este país (" + project.getCountry().getName().replace("peru", "perú").toUpperCase() + ").")).queue(
                                msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                        );
                    }

                } else {
                    event.replyEmbeds(errorEmbed("Tu cuenta de Discord no está conectada a una cuenta de Minecraft.")).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                }

            } else {
                event.replyEmbeds(errorEmbed("El proyecto introducido no existe.")).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                );
            }
        }
    }

}
