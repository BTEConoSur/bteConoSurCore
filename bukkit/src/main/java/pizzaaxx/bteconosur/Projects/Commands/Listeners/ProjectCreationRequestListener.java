package pizzaaxx.bteconosur.Projects.Commands.Listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectCreationRequestListener extends ListenerAdapter implements Prefixable {

    private final BTEConoSur plugin;

    public ProjectCreationRequestListener(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    // ACCEPT (Button) -> projectCreationRequestAccept
    // REJECT (Button) -> projectCreationRequestReject
    // TYPE (Menu) -> projectCreationRequestTypeMenu
    // POINTS (Menu) -> projectCreationRequestPointsMenu

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if (event.getSelectMenu().getId().startsWith("projectCreationRequest")) {
            Message message = event.getMessage();

            try {
                ResultSet set = plugin.getSqlManager().select(
                        "project_requests",
                        new SQLColumnSet(
                                "*"
                        ),
                        new SQLConditionSet(
                                new SQLOperatorCondition(
                                        "message_id", "=", message.getId()
                                )
                        )
                ).retrieve();

                if (set.next()) {

                    String moderatorID = set.getString("moderator_id");
                    if (moderatorID != null && !moderatorID.equals(event.getUser().getId())) {
                        plugin.getBot().retrieveUserById(moderatorID).queue(
                                user -> event.replyEmbeds(
                                        DiscordUtils.fastEmbed(
                                                Color.RED,
                                                "Esta solicitud ya está siendo revisada por " + user.getName() + "#" + user.getDiscriminator() + "."
                                        )
                                ).setEphemeral(true).queue()
                        );
                        return;
                    }

                    Country country = plugin.getCountryManager().get(set.getString("country"));

                    if (event.getSelectMenu().getId().equals("projectCreationRequestTypeMenu")) {
                        String typeName = event.getInteraction().getSelectedOptions().get(0).getValue();
                        ProjectType type = country.getProjectType(typeName);

                        StringSelectMenu.Builder pointsMenuBuilder = StringSelectMenu.create("projectCreationRequestPointsMenu");
                        for (Integer points : type.getPointsOptions()) {
                            pointsMenuBuilder.addOption(String.valueOf(points), String.valueOf(points));
                        }
                        pointsMenuBuilder.setPlaceholder("Selecciona un puntaje");

                        EmbedBuilder embedBuilder = new EmbedBuilder(message.getEmbeds().get(0));
                        embedBuilder.setColor(Color.YELLOW);
                        embedBuilder.setFooter("En revisión por " + event.getUser().getName() + "#" + event.getUser().getDiscriminator());

                        plugin.getSqlManager().update(
                                "project_requests",
                                new SQLValuesSet(
                                        new SQLValue(
                                                "type", type.getName()
                                        ),
                                        new SQLValue(
                                                "points", null
                                        ),
                                        new SQLValue(
                                                "moderator_id", event.getUser().getId()
                                        )
                                ),
                                new SQLConditionSet(
                                        new SQLOperatorCondition(
                                                "message_id", "=", message.getId()
                                        )
                                )
                        ).execute();

                        event.editComponents(
                                ActionRow.of(
                                        event.getInteraction().getSelectMenu().createCopy().setDefaultValues(typeName).build()
                                ),
                                ActionRow.of(pointsMenuBuilder.build()),
                                ActionRow.of(
                                        Button.of(
                                                ButtonStyle.SUCCESS,
                                                "projectCreationRequestAccept",
                                                "Aceptar",
                                                Emoji.fromCustom(
                                                        "approve",
                                                        959984723868913714L,
                                                        false
                                                )
                                        ).withDisabled(true),
                                        Button.of(
                                                ButtonStyle.DANGER,
                                                "projectCreationRequestReject",
                                                "Rechazar",
                                                Emoji.fromCustom(
                                                        "reject",
                                                        959984723789250620L,
                                                        false
                                                )
                                        )
                                )
                        ).setEmbeds(embedBuilder.build()).setAttachments(message.getAttachments()).queue();
                    }

                    if (event.getSelectMenu().getId().equals("projectCreationRequestPointsMenu")) {

                        int points = Integer.parseInt(event.getInteraction().getSelectedOptions().get(0).getValue());

                        plugin.getSqlManager().update(
                                "project_requests",
                                new SQLValuesSet(
                                        new SQLValue(
                                                "points", points
                                        )
                                ),
                                new SQLConditionSet(
                                        new SQLOperatorCondition(
                                                "message_id", "=", message.getId()
                                        )
                                )
                        ).execute();

                        event.editComponents(
                                message.getComponents().get(0),
                                ActionRow.of(
                                        event.getInteraction().getSelectMenu().createCopy().setDefaultValues(String.valueOf(points)).build()
                                ),
                                ActionRow.of(
                                        Button.of(
                                                ButtonStyle.SUCCESS,
                                                "projectCreationRequestAccept",
                                                "Aceptar",
                                                Emoji.fromCustom(
                                                        "approve",
                                                        959984723868913714L,
                                                        false
                                                )
                                        ),
                                        Button.of(
                                                ButtonStyle.DANGER,
                                                "projectCreationRequestReject",
                                                "Rechazar",
                                                Emoji.fromCustom(
                                                        "reject",
                                                        959984723789250620L,
                                                        false
                                                )
                                        )
                                )
                        ).queue();

                    }

                } else {
                    message.delete().queue();
                    event.replyEmbeds(
                            DiscordUtils.fastEmbed(
                                    Color.RED,
                                    "Ha ocurrido un error en la base de datos."
                            )
                    ).setEphemeral(true).queue();
                }
            } catch (SQLException e) {
                event.replyEmbeds(
                        DiscordUtils.fastEmbed(
                                Color.RED,
                                "Ha ocurrido un error en la base de datos."
                        )
                ).setEphemeral(true).queue();
            }

        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {}

    @Override
    public String getPrefix() {
        return "§f[§dPROYECTO§f] §7>> §f";
    }
}
