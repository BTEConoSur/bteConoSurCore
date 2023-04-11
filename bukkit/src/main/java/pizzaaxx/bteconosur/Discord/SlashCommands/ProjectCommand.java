package pizzaaxx.bteconosur.Discord.SlashCommands;

import com.sk89q.worldedit.regions.Polygonal2DRegion;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Posts.Post;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProjectCommand extends ListenerAdapter implements SlashCommandContainer {

    private final BTEConoSur plugin;

    public ProjectCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("project")) {

            OptionMapping idMapping = event.getOption("id");
            assert idMapping != null;
            String id = idMapping.getAsString();

            if (!plugin.getProjectRegistry().exists(id)) {
                DiscordUtils.respondError(event, "La ID introducida no existe.");
                return;
            }

            this.projectEmbed(event, id);

        }


    }

    public void projectEmbed(IReplyCallback event, String id) {

        Project project = plugin.getProjectRegistry().get(id);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(project.getType().getColor());
        builder.setTitle("Proyecto " + project.getDisplayName());
        ServerPlayer owner = plugin.getPlayerRegistry().get(project.getOwner());
        if (project.getOwner() != null) {
            builder.setThumbnail("https://mc-heads.net/head/" + owner.getUUID());
        }

        // TIPO PAIS ETIQUETA
        // COORDENADAS
        // LIDER MIEMBROS

        builder.addField(
                "País",
                ":flag_" + project.getCountry().getAbbreviation() + ": " + project.getCountry().getDisplayName(),
                true
        );
        builder.addField(
                ":game_die: Tipo:",
                project.getType().getDisplayName() + " (" + project.getPoints() + " puntos)",
                true
        );
        if (project.getTag() != null) {
            builder.addField(
                    ":label: Etiqueta:",
                    StringUtils.capitalize(project.getTag().toString().toLowerCase()),
                    true
            );
        } else {
            builder.addBlankField(true);
        }
        Location loc = project.getTeleportLocation();
        Coords2D coords = new Coords2D(plugin, loc);
        builder.addField(
                ":round_pushpin: Coordenadas:",
                "[" + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + "](https://www.google.com/maps/@" + coords.getLat() + "," + coords.getLon() + ",19z" + ")",
                false
        );
        builder.addField(
                ":city_sunset: Ciudad(es):",
                project.getCitiesResolved().stream().map(City::getDisplayName).collect(Collectors.joining(", ")),
                true
        );
        NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMAN);
        builder.addField(
                ":straight_ruler: Área:",
                format.format(Math.abs(project.getTotalArea())) + "m²",
                true
        );
        if (project.getOwner() != null) {
            builder.addField(
                    ":crown: Líder:",
                    owner.getName(),
                    true
            );
            builder.addField(
                    ":busts_in_silhouette: Miembros:",
                    (project.getMembers().isEmpty() ? "Sin miembros." : String.join(", ", plugin.getPlayerRegistry().getNames(project.getMembers()))),
                    true
            );
        }

        if (project.hasPost()) {
            Post post = project.getPost();
            builder.setTitle(post.getName());
            post.getMessage().queue(
                    msg -> {
                        Message.Attachment attachment = msg.getAttachments().get(0);
                        builder.setImage(attachment.getUrl());
                        builder.setDescription(post.getDescription());
                        event.replyEmbeds(builder.build())
                                .addComponents(
                                        ActionRow.of(
                                                Button.of(
                                                        ButtonStyle.LINK,
                                                        post.getChannel().getJumpUrl(),
                                                        "Ver publicación",
                                                        Emoji.fromUnicode("U+1F4F8")
                                                ),
                                                plugin.getDiscordHandler().getDeleteButton(event.getUser())
                                        )
                                ).queue();
                    }
            );
        } else {
            builder.setImage("attachment://map.png");
            event.replyEmbeds(builder.build())
                    .setFiles(
                            FileUpload.fromData(new File(plugin.getDataFolder(), "projects/images/" + id + ".png"), "map.png")
                    )
                    .setComponents(
                            ActionRow.of(
                                    plugin.getDiscordHandler().getDeleteButton(event.getUser())
                            )
                    ).queue();
        }
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(
                "project",
                "Obtén información sobre un proyecto."
        )
                .addOption(
                        OptionType.STRING,
                        "id",
                        "La ID del proyecto.",
                        true
                )
                .setNameLocalization(DiscordLocale.SPANISH, "proyecto");
    }

    @Override
    public JDA getJDA() {
        return plugin.getBot();
    }
}
