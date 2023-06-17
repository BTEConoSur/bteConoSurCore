package pizzaaxx.bteconosur.Discord.SlashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
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
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
            String id = idMapping.getAsString().toLowerCase();

            if (!plugin.getProjectRegistry().exists(id)) {
                DiscordUtils.respondError(event, "La ID introducida no existe.");
                return;
            }

            try {
                this.projectEmbed(event, id);
            } catch (SQLException e) {
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
            }

        }


    }

    public void projectEmbed(IReplyCallback event, String id) throws SQLException {

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

        ResultSet set = plugin.getSqlManager().select(
                "posts",
                new SQLColumnSet(
                        "channel_id", "message_id", "description"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "target_type", "=", "project"
                        ),
                        new SQLOperatorCondition("target_id", "=", project.getId())
                )
        ).retrieve();

        ThreadChannel channel = project.getCountry().getGuild().getThreadChannelById(set.getString("channel_id"));
        if (channel == null) {
            DiscordUtils.respondError(event, "Ha ocurrido un error.");
            return;
        }

        String description = set.getString("description");

        channel.retrieveMessageById(set.getString("message_id")).queue(
                msg -> {
                    Message.Attachment attachment = msg.getAttachments().get(0);
                    builder.setImage(attachment.getUrl());
                    builder.setDescription(description);
                    event.replyEmbeds(builder.build())
                            .addComponents(
                                    ActionRow.of(
                                            Button.of(
                                                    ButtonStyle.LINK,
                                                    channel.getJumpUrl(),
                                                    "Ver publicación",
                                                    Emoji.fromUnicode("U+1F4F8")
                                            ),
                                            plugin.getDiscordHandler().getDeleteButton(event.getUser())
                                    )
                            ).queue();
                }
        );
    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[] {Commands.slash(
                "project",
                "Obtén información sobre un proyecto."
        )
                .addOption(
                        OptionType.STRING,
                        "id",
                        "La ID del proyecto.",
                        true,
                        true
                )
                .setNameLocalization(DiscordLocale.SPANISH, "proyecto")};
    }

    @Override
    public JDA getJDA() {
        return plugin.getBot();
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("project")) {
            if (event.getFocusedOption().getName().equals("id")) {

                String value = event.getFocusedOption().getValue();

                if (value.length() == 0 || value.length() > 6) {
                    event.replyChoiceStrings().queue();
                    return;
                }

                List<String> response = new ArrayList<>();
                for (String id : plugin.getProjectRegistry().getIds()) {
                    if (id.startsWith(value.toLowerCase())) {
                        response.add(id);
                    }
                }
                Collections.sort(response);

                event.replyChoiceStrings(response.subList(0, Math.min(25, response.size()))).queue();

            }
        }
    }
}
