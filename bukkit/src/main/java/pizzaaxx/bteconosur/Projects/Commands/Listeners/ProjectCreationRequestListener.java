package pizzaaxx.bteconosur.Projects.Commands.Listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectCreationRequestListener extends ListenerAdapter implements Prefixable {

    private final BTEConoSur plugin;

    public ProjectCreationRequestListener(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {

        StringSelectMenu menu = event.getSelectMenu();

        String id = menu.getId();

        if (id == null) {
            return;
        }

        if (id.startsWith("projectCreationRequest")) {

            Message message = event.getMessage();
            MessageEmbed embed = message.getEmbeds().get(0);

            MessageEmbed.Footer footer = embed.getFooter();

            if (footer != null) {
                String footerText = footer.getText();
                if (footerText != null) {
                    String userID = footerText.replace("En revisión por ", "");
                    if (!userID.equals(event.getUser().getId())) {
                        plugin.getBot().retrieveUserById(userID).queue(
                                user -> event.replyEmbeds(
                                                DiscordUtils.fastEmbed(
                                                        Color.RED,
                                                        "Esta solicitud ya esta siendo revisada por " + user.getId() + "#" + user.getDiscriminator() + "."
                                                )
                                        ).setEphemeral(true).queue()
                        );
                        return;
                    }
                }
            }

            if (event.getGuild() == null) {
                return;
            }

            Country country = plugin.getCountryManager().getCountryByGuild(event.getGuild().getId());

            if (country == null) {
                return;
            }

            if (id.equals("projectCreationRequestTypeMenu")) {

                String label = event.getSelectedOptions().get(0).getLabel();

                ProjectType type = country.getType(label);

                StringSelectMenu.Builder pointsMenu = StringSelectMenu.create("projectCreationRequestPointsMenu");
                pointsMenu.setPlaceholder("Selecciona un puntaje");

                for (Integer points : type.getPointsOptions()){
                    pointsMenu.addOption(points.toString(), points.toString());
                }

                event.editComponents(
                        message.getComponents().get(0),
                        ActionRow.of(
                                pointsMenu.build()
                        ),
                        message.getComponents().get(2)
                ).queue();
            }

            if (id.equals("projectCreationRequestPointsMenu")) {

                net.dv8tion.jda.api.interactions.components.buttons.Button acceptButton = Button.of(
                        ButtonStyle.SUCCESS,
                        "projectCreationRequestAccept",
                        "Aceptar",
                        Emoji.fromCustom(
                                "approve",
                                959984723868913714L,
                                false
                        )
                );

                event.editComponents(
                        message.getComponents().get(0),
                        message.getComponents().get(1),
                        ActionRow.of(
                                acceptButton,
                                message.getComponents().get(2).getComponents().get(1)
                        )
                ).queue();

            }
        }


    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        Button button = event.getButton();

        String id = button.getId();

        if (id == null) {
            return;
        }

        if (id.startsWith("projectCreationRequest")) {

            Message message = event.getMessage();
            MessageEmbed embed = message.getEmbeds().get(0);

            MessageEmbed.Footer footer = embed.getFooter();

            if (footer != null) {
                String footerText = footer.getText();
                if (footerText != null) {
                    String userID = footerText.replace("En revisión por ", "");
                    if (!userID.equals(event.getUser().getId())) {
                        plugin.getBot().retrieveUserById(userID).queue(
                                user -> event.replyEmbeds(
                                        DiscordUtils.fastEmbed(
                                                Color.RED,
                                                "Esta solicitud ya esta siendo revisada por " + user.getId() + "#" + user.getDiscriminator() + "."
                                        )
                                ).setEphemeral(true).queue()
                        );
                        return;
                    }
                }
            }

            try {



                ResultSet set = plugin.getSqlManager().select(
                        "project_requests",
                        new SQLColumnSet(
                                "*"
                        ),
                        new SQLConditionSet(
                                new SQLOperatorCondition(
                                        "message_id", "=", event.getMessage().getId()
                                )
                        )
                ).retrieve();

                if (set.next()) {

                    if (event.getGuild() == null) {
                        return;
                    }

                    Country country = plugin.getCountryManager().getCountryByGuild(event.getGuild().getId());

                    if (country == null) {
                        return;
                    }

                    ServerPlayer s = plugin.getPlayerRegistry().get(plugin.getSqlManager().getUUID(set, "owner"));

                    if (id.equals("projectCreationRequestAccept")) {



                    }

                    if (id.equals("projectCreationRequestReject")) {

                        plugin.getSqlManager().delete(
                                "project_requests",
                                new SQLConditionSet(
                                        new SQLOperatorCondition(
                                                "message_id", "=", event.getMessage().getId()
                                        )
                                )
                        ).execute();

                        s.sendNotification(
                                this.getPrefix() + "Tu solicitud de proyecto en §a" + country.getDisplayName() + "§f ha sido rechazada.",
                                "**[PROYECTOS]** » Tu solicitud de proyecto en **" + country.getDisplayName() + "** ha sido rechazada."
                        );

                        event.getMessage().delete().queue();

                        event.replyEmbeds(
                                DiscordUtils.fastEmbed(
                                        Color.GREEN,
                                        "Solicitud rechazada."
                                )
                        );
                    }
                } else {
                    event.getMessage().delete().queue();
                    event.replyEmbeds(
                            DiscordUtils.fastEmbed(
                                    Color.RED,
                                    "Ha ocurrido un error en la base de datos."
                            )
                    ).queue();
                }

            } catch (SQLException | IOException e) {
                event.replyEmbeds(
                        DiscordUtils.fastEmbed(
                                Color.RED,
                                "Ha ocurrido un error en la base de datos."
                        )
                ).queue();
            }

        }

    }

    @Override
    public String getPrefix() {
        return "§f[§dPROYECTO§f] §7>> §f";
    }
}
