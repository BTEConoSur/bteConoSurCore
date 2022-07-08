package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.server.player.DiscordManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;

public class ProjectTagCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
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


                        ActionRow row = ActionRow.of(
                                SelectMenu.create("changeTag~" + project.getId())
                                        .setPlaceholder("Selecciona una etiqueta")
                                        .addOption("Edificios", "EDIFICIOS", "Edificios de oficinas, comerciales y otros.", Emoji.fromUnicode("U+1F3EC"))
                                        .addOption("Departamentos", "DEPARTAMENTOS", "Edificios exclusivamente de departamentos.", Emoji.fromUnicode("U+1F3E8"))
                                        .addOption("Casas", "CASAS", "Casas, townhouses y condominios residenciales.", Emoji.fromUnicode("U+1F3D8"))
                                        .addOption("Parques", "PARQUES", "Parques, plazas, bosques, canchas de deportes, etc.", Emoji.fromUnicode("U+1F333"))
                                        .addOption("Establecimientos", "ESTABLECIMIENTOS", "Colegios, hospitales, gimnasios, universidades y otros lugares públicos.", Emoji.fromUnicode("U+1F3EB"))
                                        .addOption("Carreteras", "CARRETERAS", "Carreteras, autopistas, calles, pasos elevados, túneles, etc.", Emoji.fromUnicode("U+1F6E3"))
                                        .addOption("Centros Comerciales", "CENTROS_COMERCIALES", "Malls, supermercados y otros centros comerciales.", Emoji.fromUnicode("U+1F6CD"))
                                        .addOption("Eliminar", "delete", "Elimina la etiqueta actual.", Emoji.fromUnicode("U+1F5D1"))
                                        .build()
                        );

                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.YELLOW);
                        builder.setTitle("Elige una etiqueta para el proyecto " + project.getId().toUpperCase());
                        // IMAGE
                        URL url;
                        try {
                            url = new URL(project.getImageUrl());
                        } catch (MalformedURLException e) {
                            event.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                            );
                            return;
                        }


                        InputStream stream;
                        try {
                            stream = url.openStream();
                        } catch (IOException e) {
                            event.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                            );
                            return;
                        }


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

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        if (event.getComponentId().startsWith("changeTag~")) {

            if (event.getMessage().getInteraction() != null && event.getMessage().getInteraction().getUser().getId().equals(event.getUser().getId())) {

                String projectId = event.getComponentId().replace("changeTag~", "");

                if (Project.projectExists(projectId)) {

                    Project project = new Project(projectId);

                    String option = event.getSelectedOptions().get(0).getValue();

                    if (option.equals("delete")) {
                        project.setTag(null);

                        event.editMessageEmbeds(
                                new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setTitle("Se ha eliminado la etiqueta del proyecto " + projectId.toUpperCase() + ".")
                                        .build()
                        ).setActionRows().retainFiles(new ArrayList<>()).queue();
                        project.getCountry().getLogs().sendMessage(":label: **" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** ha eliminado la etiqueta del proyecto `" + project.getId() + "`.").queue();

                    } else {
                        project.setTag(Project.Tag.valueOf(option));

                        event.editMessageEmbeds(
                                new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setTitle("Se ha establecido la etiqueta del proyecto " + projectId.toUpperCase() + " en " + option + ".")
                                        .build()
                        ).setActionRows().retainFiles(new ArrayList<>()).queue();
                        project.getCountry().getLogs().sendMessage(":label: **" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** ha establecido la etiqueta del proyecto `" + project.getId() + "` en **" + option.replace("_", " ").toUpperCase() + "**.").queue();

                    }
                    project.save();


                } else {
                    event.editMessageEmbeds(errorEmbed("El proyecto ha sido eliminado.")).retainFiles(new ArrayList<>()).setActionRows().queue();
                }
            } else {

                event.replyEmbeds(errorEmbed("Solo el usuario que usó el comando puede usar el menú.")).setEphemeral(true).queue();

            }

        }
    }

}
