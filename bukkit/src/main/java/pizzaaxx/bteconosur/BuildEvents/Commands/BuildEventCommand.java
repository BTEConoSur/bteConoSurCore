package pizzaaxx.bteconosur.BuildEvents.Commands;

import com.sk89q.worldguard.util.net.HttpRequest;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.BuildEvents.BuildEvent;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Discord.SlashCommands.SlashCommandContainer;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLORConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.StringUtils;
import xyz.upperlevel.spigot.book.BookUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BuildEventCommand extends ListenerAdapter implements SlashCommandContainer, CommandExecutor {

    private final BTEConoSur plugin;

    public BuildEventCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    private final Set<String> ids = new HashSet<>();

    public void init() throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "build_events",
                new SQLColumnSet(
                        "id"
                ),
                new SQLANDConditionSet()
        ).retrieve();

        while (set.next()) {
            ids.add(set.getString("id"));
        }
    }

    // DISCORD
    // /buildevent edit name
    // /buildevent edit image
    // /buildevent edit description
    // /buildevent edit datefrom*
    // /buildevent edit minimumunlocked*
    // /buildevent post

    @Override
    public CommandData getCommandData() {
        return Commands.slash(
                "buildevent",
                "Maneja eventos de construcción."
        )
                .addSubcommandGroups(
                        new SubcommandGroupData(
                                "edit",
                                "Edita datos de un evento"
                        )
                                .addSubcommands(
                                        new SubcommandData(
                                                "name",
                                                "Cambia el nombre de un evento."
                                        ).setNameLocalization(
                                                DiscordLocale.SPANISH, "nombre"
                                        ),
                                        new SubcommandData(
                                                "image",
                                                "Cambia la imagen de un evento."
                                        ).addOption(
                                                OptionType.ATTACHMENT,
                                                "imagen",
                                                "La nueva imagen del evento."
                                        ).setNameLocalization(
                                                DiscordLocale.SPANISH, "imagen"
                                        ),
                                        new SubcommandData(
                                                "description",
                                                "Cambia la descripción del evento."
                                        ).setNameLocalization(
                                                DiscordLocale.SPANISH, "descripción"
                                        ),
                                        new SubcommandData(
                                                "dates",
                                                "Cambia las fechas de inicio y término del evento."
                                        ).setNameLocalization(
                                                DiscordLocale.SPANISH, "fechas"
                                        ),
                                        new SubcommandData(
                                                "minimumunlocked",
                                                "Cambia el tipo de proyecto mínimo que debe un jugador tener desbloqueado para unirse."
                                        ).setNameLocalization(
                                                DiscordLocale.SPANISH, "mínimodesbloqueado"
                                        )
                                ).setNameLocalization(
                                        DiscordLocale.SPANISH, "editar"
                                ),
                        new SubcommandGroupData(
                                "post",
                                "Activa un evento de construcción."
                        ).setNameLocalization(
                                DiscordLocale.SPANISH, "publicar"
                        )
                ).setGuildOnly(true);
    }

    @Override
    public JDA getJDA() {
        return plugin.getBot();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("buildevent")) {

            if (!plugin.getLinksRegistry().isLinked(event.getUser().getId())) {
                DiscordUtils.respondError(event, "Solo administradores pueden usar este comando.");
                return;
            }

            ServerPlayer s = plugin.getPlayerRegistry().get(plugin.getLinksRegistry().get(event.getUser().getId()));

            if (!s.getSecondaryRoles().contains(ServerPlayer.SecondaryRoles.ADMIN)) {
                DiscordUtils.respondError(event, "Solo administradores pueden usar este comando.");
                return;
            }

            assert event.getGuild() != null;
            Country country = plugin.getCountryManager().guilds.get(event.getGuild().getId());

            ResultSet set;
            BuildEvent.Status status;
            String id;
            String eventID;
            try {
                set = plugin.getSqlManager().select(
                        "build_events",
                        new SQLColumnSet("*"),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "country", "=", country.getName()
                                ),
                                new SQLORConditionSet(
                                        new SQLOperatorCondition(
                                                "status", "=", "EDITED"
                                        ),
                                        new SQLOperatorCondition(
                                                "status", "=", "POSTED"
                                        )
                                )
                        )
                ).retrieve();

                if (!set.next()) {
                    DiscordUtils.respondError(event, "El evento ya está activo por lo que no puede ser modificado.");
                    return;
                }
                status = BuildEvent.Status.valueOf(set.getString("status"));
                id = set.getString("id");
                eventID = set.getString("scheduled_event_id");
            } catch (SQLException e) {
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                return;
            }

            assert event.getSubcommandGroup() != null;
            try {
                switch (event.getSubcommandGroup()) {
                    case "edit": {

                        assert event.getSubcommandName() != null;
                        switch (event.getSubcommandName()) {
                            case "name": {

                                Modal modal = Modal.create(
                                        "buildEventEditName?country=" + country.getName() + "&id=" + id,
                                        "Editar nombre"
                                ).addActionRows(
                                        ActionRow.of(
                                                TextInput.create(
                                                                "name",
                                                                "Nombre",
                                                                TextInputStyle.SHORT
                                                        )
                                                        .setRequired(true)
                                                        .setMaxLength(64)
                                                        .setValue(set.getString("name"))
                                                        .setPlaceholder("Introduce el nombre del evento.")
                                                        .build()
                                        )
                                ).build();

                                event.replyModal(modal).queue();

                                break;
                            }
                            case "description": {

                                Modal modal = Modal.create(
                                        "buildEventEditDescription?country=" + country.getName() + "&id=" + id,
                                        "Editar descripción"
                                ).addActionRows(
                                        ActionRow.of(
                                                TextInput.create(
                                                                "description",
                                                                "Descripción",
                                                                TextInputStyle.SHORT
                                                        )
                                                        .setRequired(true)
                                                        .setMaxLength(1000)
                                                        .setValue(set.getString("description"))
                                                        .setPlaceholder("Introduce la descripción del evento.")
                                                        .build()
                                        )
                                ).build();

                                event.replyModal(modal).queue();

                                break;
                            }
                            case "image": {

                                OptionMapping imageMapping = event.getOption("imagen");
                                if (imageMapping != null) {
                                    Message.Attachment image = imageMapping.getAsAttachment();
                                    assert image.getFileExtension() != null;
                                    if (!(image.getFileExtension().equals("png") || image.getFileExtension().equals("jpg") || image.getFileExtension().equals("jpeg"))) {
                                        DiscordUtils.respondError(event, "Introduce un archivo de imagen válido.");
                                        return;
                                    }

                                    try {
                                        InputStream is = HttpRequest.get(new URL(image.getUrl())).execute().getInputStream();
                                        File target = new File(plugin.getDataFolder(), "buildEvents/" + country.getName() + ".png");
                                        FileUtils.copyInputStreamToFile(is, target);

                                        if (status == BuildEvent.Status.POSTED) {
                                            country.getGuild().retrieveScheduledEventById(eventID).queue(
                                                    scheduledEvent -> {
                                                        try {
                                                            scheduledEvent.getManager().setImage(Icon.from(target)).queue();
                                                        } catch (IOException e) {
                                                            DiscordUtils.respondError(event, "Ha ocurrido un error de I/O.");
                                                        }
                                                    }
                                            );
                                        }

                                        DiscordUtils.respondSuccessEphemeral(event, "Imagen del evento modificada correctamente.");
                                    } catch (IOException e) {
                                        DiscordUtils.respondError(event, "Ha ocurrido un error de I/O.");
                                        return;
                                    }
                                } else {
                                    File target = new File(plugin.getDataFolder(), "buildEvents/" + country.getName() + ".png");
                                    target.delete();

                                    if (status == BuildEvent.Status.POSTED) {
                                        country.getGuild().retrieveScheduledEventById(eventID).queue(
                                                scheduledEvent -> {
                                                    scheduledEvent.getManager().setImage(null).queue();
                                                }
                                        );
                                    }

                                    DiscordUtils.respondSuccessEphemeral(event, "Imagen del evento eliminada correctamente.");
                                }

                                break;
                            }
                            case "dates": {

                                if (status != BuildEvent.Status.EDITED) {
                                    DiscordUtils.respondError(event, "No se pueden cambiar las fechas una vez el evento fue publicado.");
                                    return;
                                }

                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                                Date start = null;
                                if (set.getTimestamp("dateFrom") != null) {
                                    start = new Date(set.getTimestamp("dateFrom").getTime());
                                }

                                Date end = null;
                                if (set.getTimestamp("dateTo") != null) {
                                    end = new Date(set.getTimestamp("dateTo").getTime());
                                }

                                Modal modal = Modal.create(
                                        "buildEventEditDates?country=" + country.getName() + "&id=" + id,
                                        "Editar fechas"
                                ).addActionRows(
                                        ActionRow.of(
                                                TextInput.create(
                                                                "start",
                                                                "Fecha de inicio (Formato: dd/MM/yyyy HH:mm:ss)",
                                                                TextInputStyle.SHORT
                                                        )
                                                        .setRequired(true)
                                                        .setMaxLength(1000)
                                                        .setValue((start != null ? format.format(start) : null))
                                                        .setPlaceholder("Introduce la fecha de inicio del evento.")
                                                        .build()
                                        ),
                                        ActionRow.of(
                                                TextInput.create(
                                                                "end",
                                                                "Fecha de término (Formato: dd/MM/yyyy HH:mm:ss)",
                                                                TextInputStyle.SHORT
                                                        )
                                                        .setRequired(true)
                                                        .setMaxLength(1000)
                                                        .setValue((end != null ? format.format(end) : null))
                                                        .setPlaceholder("Introduce la fecha de término del evento.")
                                                        .build()
                                        )
                                ).build();

                                event.replyModal(modal).queue();

                                break;
                            }
                            case "minimumunlocked": {

                                if (status != BuildEvent.Status.EDITED) {
                                    DiscordUtils.respondError(event, "No se puede cambiar el tipo mínimo desbloqueado una vez el evento fue publicado.");
                                    return;
                                }

                                StringSelectMenu.Builder typeMenu = StringSelectMenu.create("buildEventEditMinimumType?country=" + country.getName() + "&id=" + id);
                                for (ProjectType type : country.getProjectTypes()) {
                                    typeMenu.addOption(
                                            type.getDisplayName(),
                                            type.getName()
                                    );
                                }
                                typeMenu.setPlaceholder("Selecciona un tipo de proyecto mínimo desbloqueado.");
                                if (set.getString("minimum_type") != null) {
                                    typeMenu.setDefaultValues(set.getString("minimum_type"));
                                }

                                event.replyComponents(
                                        ActionRow.of(
                                                typeMenu.build()
                                        )
                                ).queue();

                                break;
                            }
                        }

                        break;
                    }
                    case "post": {

                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().startsWith("buildEventEdit")) {
            Map<String, String> query = StringUtils.getQuery(event.getModalId().split("\\?")[1]);

            Country country = plugin.getCountryManager().get(query.get("country"));
            String id = query.get("id");

            try {

                ResultSet set = plugin.getSqlManager().select(
                        "build_events",
                        new SQLColumnSet("scheduled_event_id"),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "id", "=", id
                                )
                        )
                ).retrieve();

                set.next();

                String eventID = set.getString("scheduled_event_id");

                switch (event.getModalId().replace("buildEventEdit", "").split("\\?")[0].toLowerCase()) {
                    case "name": {

                        ModalMapping nameMapping = event.getValue("name");
                        assert nameMapping != null;
                        String name = nameMapping.getAsString();

                        plugin.getSqlManager().update(
                                "build_events",
                                new SQLValuesSet(
                                        new SQLValue(
                                                "name", name
                                        )
                                ),
                                new SQLANDConditionSet(
                                        new SQLOperatorCondition(
                                                "id", "=", id
                                        )
                                )
                        ).execute();

                        if (eventID != null) {
                            country.getGuild().retrieveScheduledEventById(eventID).queue(
                                    scheduledEvent -> scheduledEvent.getManager().setName(name).queue()
                            );
                        }

                        DiscordUtils.respondSuccessEphemeral(event, "Nombre del evento editado correctamente.");
                        break;
                    }
                    case "description": {

                        ModalMapping descriptionMapping = event.getValue("description");
                        assert descriptionMapping != null;
                        String description = descriptionMapping.getAsString();

                        plugin.getSqlManager().update(
                                "build_events",
                                new SQLValuesSet(
                                        new SQLValue(
                                                "description", description
                                        )
                                ),
                                new SQLANDConditionSet(
                                        new SQLOperatorCondition(
                                                "id", "=", id
                                        )
                                )
                        ).execute();

                        if (eventID != null) {
                            country.getGuild().retrieveScheduledEventById(eventID).queue(
                                    scheduledEvent -> scheduledEvent.getManager().setDescription(description).queue()
                            );
                        }

                        DiscordUtils.respondSuccessEphemeral(event, "Descripción del evento editada correctamente.");

                        break;
                    }
                    case "dates": {

                        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        ModalMapping fromMapping = event.getValue("start");
                        assert fromMapping != null;
                        Date from;
                        try {
                            from = df2.parse(fromMapping.getAsString());
                        } catch (ParseException e) {
                            DiscordUtils.respondError(event, "Error de formato.");
                            return;
                        }

                        ModalMapping toMapping = event.getValue("end");
                        assert toMapping != null;
                        Date to;
                        try {
                            to = df2.parse(toMapping.getAsString());
                        } catch (ParseException e) {
                            DiscordUtils.respondError(event, "Error de formato.");
                            return;
                        }

                        if (to.before(from) || to.equals(from) || to.getTime() - from.getTime() < 3600000) {
                            DiscordUtils.respondError(event, "La duración mínima de un evento es de una hora.");
                            return;
                        }

                        plugin.getSqlManager().update(
                                "build_events",
                                new SQLValuesSet(
                                        new SQLValue(
                                                "date_from", from
                                        ),
                                        new SQLValue(
                                                "date_to", to
                                        )
                                ),
                                new SQLANDConditionSet(
                                        new SQLOperatorCondition(
                                                "id", "=", id
                                        )
                                )
                        ).execute();

                        DiscordUtils.respondSuccessEphemeral(event, "Fechas del evento editadas correctamente.");

                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
            }
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {

    }

    // /buildevent region
    // /buildevent spawn
    // /buildevent join
    // /buildevent leave

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
