package pizzaaxx.bteconosur.projects;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.cities.City;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.discord.DiscordConnector;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.projects.ProjectsManager;
import pizzaaxx.bteconosur.utils.SQLUtils;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class ProjectCreationRequestListener extends ListenerAdapter {

    private final BTEConoSurPlugin plugin;

    public ProjectCreationRequestListener(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        String buttonID = event.getButton().getId();
        if (buttonID == null) return;
        if (!buttonID.startsWith("projectCreateRequest")) return;

        String messageID = event.getMessageId();
        try (SQLResult result = plugin.getSqlManager().select(
                "project_creation_requests",
                new SQLColumnSet("*", "ST_AsWKT(region) AS region_wkt"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "message_id", "=", messageID
                        )
                )
        ).retrieve()) {

            ResultSet set = result.getResultSet();
            if (!set.next()) {
                DiscordConnector.respondError(event, "No se ha encontrado la solicitud.");
                return;
            }

            String moderatorID = set.getString("moderator_id");
            if (moderatorID != null && !moderatorID.equals(event.getUser().getId())) {
                DiscordConnector.respondError(event, "Ya hay alguien revisando esta solicitud.");
                return;
            }

            if (buttonID.equals("projectCreateRequestAccept")) {

                Country country = plugin.getCountriesRegistry().get(set.getString("country"));
                Polygon region = SQLUtils.polygonFromWKT(
                        set.getString("region_wkt")
                );
                // get city at centroid
                Point centroid = region.getCentroid();
                City city = country.getCityAt(centroid.getX(), centroid.getY());

                if (city == null) {
                    DiscordConnector.respondError(event, "No se ha encontrado una ciudad en el centro de la región.");
                    return;
                }

                ProjectType type = country.getProjectType(set.getString("type"));
                if (type == null) {
                    DiscordConnector.respondError(event, "No se ha encontrado el tipo de proyecto.");
                    return;
                }

                String id = plugin.getProjectsRegistry().createProject(
                        country,
                        city.getID(),
                        type,
                        set.getInt("points"),
                        region
                );

                Project project = plugin.getProjectsRegistry().get(id);
                UUID owner = SQLUtils.uuidFromBytes(set.getBytes("owner"));
                project.getEditor().claim(owner);

                OfflineServerPlayer player = plugin.getPlayerRegistry().get(owner);
                player.sendNotification(
                        PREFIX + "Tu solicitud de proyecto en §a" + city.getName() + ", " + country.getName() + "§r ha sido aceptada.",
                        ":bell: Tu solicitud de proyecto en **" + city.getName() + ", " + country.getName() + "** ha sido aceptada."
                );

                event.getMessage().delete().queue();
                plugin.getSqlManager().delete(
                        "project_creation_requests",
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "message_id", "=", messageID
                                )
                        )
                ).execute();
                DiscordConnector.respondSuccessEphemeral(event, "Se ha creado el proyecto correctamente.");

            } else if (buttonID.equals("projectCreateRequestDeny")) {

                event.replyModal(
                        Modal.create("projectCreateRequestDenyReason", "Razón de rechazo")
                                .addActionRow(
                                        TextInput.create("reason", "Razón de rechazo", TextInputStyle.SHORT)
                                                .setPlaceholder("Razón de rechazo")
                                                .setRequired(false)
                                                .build()
                                )
                                .build()
                ).queue();

            } else {
                DiscordConnector.respondError(event, "Ha ocurrido un error.");
            }

        } catch (SQLException e) {
            DiscordConnector.respondError(event, "Ha ocurrido un error en la base de datos.");
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if (!event.getModalId().equals("projectCreateRequestDenyReason")) return;

        String messageID = event.getMessage().getId();
        ModalMapping reasonMapping = event.getValue("reason");
        assert reasonMapping != null;
        String reason = reasonMapping.getAsString();

        try (SQLResult result = plugin.getSqlManager().select(
                "project_creation_requests",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "message_id", "=", messageID
                        )
                )
        ).retrieve()) {

            ResultSet set = result.getResultSet();
            if (!set.next()) {
                DiscordConnector.respondError(event, "No se ha encontrado la solicitud.");
                return;
            }

            UUID owner = SQLUtils.uuidFromBytes(set.getBytes("owner"));
            OfflineServerPlayer player = plugin.getPlayerRegistry().get(owner);
            Country country = plugin.getCountriesRegistry().get(set.getString("country"));
            player.sendNotification(
                    PREFIX + "Tu solicitud de proyecto en §a" + country.getDisplayName() + "§r ha sido rechazada. §7Razón: " + reason,
                    ":bell: Tu solicitud de proyecto en **" + country.getDisplayName() + "** ha sido rechazada. Razón: " + reason
            );

            event.getMessage().delete().queue();
            plugin.getSqlManager().delete(
                    "project_creation_requests",
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "message_id", "=", messageID
                            )
                    )
            ).execute();

            DiscordConnector.respondSuccessEphemeral(event, "Se ha rechazado la solicitud correctamente.");

        } catch (SQLException e) {
            DiscordConnector.respondError(event, "Ha ocurrido un error en la base de datos.");
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {

        String selectID = event.getSelectMenu().getId();
        if (selectID == null) return;
        if (!selectID.startsWith("projectCreateRequest")) return;

        ResultSet set;
        try (SQLResult result = plugin.getSqlManager().select(
                "project_creation_requests",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "message_id", "=", event.getMessageId()
                        )
                )
        ).retrieve()) {

            set = result.getResultSet();
            if (!set.next()) {
                DiscordConnector.respondError(event, "No se ha encontrado la solicitud.");
                return;
            }
            String moderatorID = set.getString("moderator_id");
            if (moderatorID != null && !moderatorID.equals(event.getUser().getId())) {
                DiscordConnector.respondError(event, "Ya hay alguien revisando esta solicitud.");
                return;
            }

            Country country = plugin.getCountriesRegistry().get(set.getString("country"));

            if (selectID.equals("projectCreateRequestType")) {

                String typeID = event.getSelectedOptions().get(0).getValue();

                plugin.getSqlManager().update(
                        "project_creation_requests",
                        new SQLValuesSet(
                                new SQLValue("type", typeID),
                                new SQLValue("moderator_id", event.getUser().getId())
                        ),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "message_id", "=", event.getMessageId()
                                )
                        )
                ).execute();

                StringSelectMenu.Builder typeMenu = event.getSelectMenu().createCopy();
                typeMenu.setDefaultValues(typeID);

                StringSelectMenu.Builder pointsMenu = StringSelectMenu.create("projectCreateRequestPoints");

                ProjectType type = country.getProjectType(typeID);
                if (type == null) {
                    DiscordConnector.respondError(event, "No se ha encontrado el tipo de proyecto.");
                    return;
                }

                for (Integer points : type.getPointOptions()) {
                    pointsMenu.addOption(
                            points.toString(),
                            points.toString()
                    );
                }

                EmbedBuilder builder = new EmbedBuilder(event.getMessage().getEmbeds().get(0));
                builder.setColor(Color.YELLOW);
                builder.setFooter("En revisión por " + event.getUser().getName(), event.getUser().getAvatarUrl());

                ActionRow buttonsRow = ActionRow.of(
                        Button.of(
                                ButtonStyle.SUCCESS,
                                "projectCreateRequestAccept",
                                "Aceptar",
                                Emoji.fromCustom("approve", 959984723868913714L, false)
                        ).withDisabled(true),
                        Button.of(
                                ButtonStyle.DANGER,
                                "projectCreateRequestDeny",
                                "Rechazar",
                                Emoji.fromCustom("reject", 959984723789250620L, false)
                        )
                );

                event.editComponents(
                        ActionRow.of(typeMenu.build()),
                        ActionRow.of(pointsMenu.build()),
                        buttonsRow
                ).setEmbeds(builder.build()).setAttachments(event.getMessage().getAttachments()).queue();

            } else if (selectID.equals("projectCreateRequestPoints")) {

                int points = Integer.parseInt(event.getSelectedOptions().get(0).getValue());

                plugin.getSqlManager().update(
                        "project_creation_requests",
                        new SQLValuesSet(
                                new SQLValue("points", points)
                        ),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "message_id", "=", event.getMessageId()
                                )
                        )
                ).execute();

                StringSelectMenu.Builder pointsMenu = event.getSelectMenu().createCopy();
                pointsMenu.setDefaultValues(String.valueOf(points));

                event.editComponents(
                        event.getMessage().getComponents().get(0),
                        ActionRow.of(pointsMenu.build()),
                        ActionRow.of(
                                Button.of(
                                        ButtonStyle.SUCCESS,
                                        "projectCreateRequestAccept",
                                        "Aceptar",
                                        Emoji.fromCustom("approve", 959984723868913714L, false)
                                ),
                                Button.of(
                                        ButtonStyle.DANGER,
                                        "projectCreateRequestDeny",
                                        "Rechazar",
                                        Emoji.fromCustom("reject", 959984723789250620L, false)
                                )
                        )
                ).setAttachments(event.getMessage().getAttachments()).queue();

            }
        } catch (SQLException e) {
            DiscordConnector.respondError(event, "Ha ocurrido un error en la base de datos.");
        }



    }
}
