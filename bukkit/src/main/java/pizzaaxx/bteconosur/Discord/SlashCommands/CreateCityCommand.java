package pizzaaxx.bteconosur.Discord.SlashCommands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldedit.BlockVector2D;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.Actions.CityActionException;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Player.Managers.DiscordManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreateCityCommand extends ListenerAdapter implements SlashCommandContainer {

    private final BTEConoSur plugin;

    public CreateCityCommand(@NotNull BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void checkCommand() {
        plugin.getBot().retrieveCommands().queue(
                commands -> {
                    boolean found = false;
                    for (Command command : commands) {
                        if (command.getName().equals("createcity")) {
                            found = true;
                            break;
                        }
                    }

                    if (!found){
                        plugin.getBot().upsertCommand(
                                "createcity",
                                "Crea una ciudad desde Discord"
                        ).queue();
                    }
                }
        );
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getChannelType() != ChannelType.TEXT) {
            return;
        }

        if (event.getName().equals("createcity")) {
            if (plugin.getLinksRegistry().isLinked(event.getUser().getId())) {
                ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(plugin.getLinksRegistry().get(event.getUser().getId()));

                if (serverPlayer.getSecondaryRoles().contains(ServerPlayer.SecondaryRoles.ADMIN)) {

                    assert event.getGuild() != null;
                    Country country = plugin.getCountryManager().guilds.get(event.getGuild().getId());

                    Modal modal = Modal.create(
                            "createCity",
                            "Crear ciudad en " + country.getDisplayName()
                    )
                            .addActionRow(
                                    TextInput.create(
                                            "createCityName",
                                            "Nombre (ID)",
                                            TextInputStyle.SHORT
                                    ).setRequired(true).build()
                            )
                            .addActionRow(
                                    TextInput.create(
                                            "createCityDisplay",
                                            "Nombre a mostrar",
                                            TextInputStyle.SHORT
                                    ).setRequired(true).build()
                            )
                            .addActionRow(
                                    TextInput.create(
                                            "createCityRegion",
                                            "Coordenadas",
                                            TextInputStyle.PARAGRAPH
                                    ).setRequired(true).setPlaceholder(
                                            "Formato: [ [ XX.XXXXX, YY.YYYYY ], ... ]"
                                    ).build()
                            )
                            .build();

                    event.replyModal(modal).queue();

                } else {
                    DiscordUtils.respondError(event, "No puedes hacer esto.");
                }
            } else {
                DiscordUtils.respondError(event, "Conecta tus cuentas para usar este comando.");
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if (event.getModalId().equals("createCity")) {

            assert event.getGuild() != null;
            Country country = plugin.getCountryManager().guilds.get(event.getGuild().getId());

            ModalMapping nameMapping = event.getValue("createCityName");
            assert nameMapping != null;
            String name = nameMapping.getAsString();

            if (!name.matches("[a-z]{1,32}")) {
                DiscordUtils.respondError(event, "El nombre solo puede contener minúsculas, sin espacios ni guiones.");
                return;
            }

            if (plugin.getCityManager().exists(name)) {
                DiscordUtils.respondError(event, "Ya existe una ciudad con el nombre \"" + name + "\".");
                return;
            }

            ModalMapping displayMapping = event.getValue("createCityDisplay");
            assert displayMapping != null;
            String display = displayMapping.getAsString();

            ModalMapping regionMapping = event.getValue("createCityRegion");
            assert regionMapping != null;
            String region = regionMapping.getAsString().replace(" ", "").replace("\n", "");

            if (!region.matches("\\[(\\[-?\\d{1,2}(\\.\\d{1,})?,-?\\d{1,2}(\\.\\d{1,})?\\],?){3,}\\]")) {
                DiscordUtils.respondError(event, "Error en el formato de las coordenadas.");
                return;
            }

            List<Object> arrayRaw;
            try {
                arrayRaw = plugin.getJSONMapper().readValue(region, ArrayList.class);
            } catch (JsonProcessingException e) {
                DiscordUtils.respondError(event, "Ha ocurrido un error en las coordenadas. Revisa el formato.");
                return;
            }

            List<BlockVector2D> coords = new ArrayList<>();
            for (Object object : arrayRaw) {
                ArrayList<Double> coordsArray = (ArrayList<Double>) object;

                double lon = coordsArray.get(0);
                double lat = coordsArray.get(1);

                while (lat > 90 || lat < -90) {
                    lat /= 10;
                }

                while (lon > 180 || lon < -180) {
                    lon /= 10;
                }

                Coords2D coords2D = new Coords2D(plugin, lat, lon);

                coords.add(coords2D.toBlockVector2D());

            }

            try {
                plugin.getCityManager().createCity(
                        name,
                        display,
                        country,
                        coords
                ).execute();

                DiscordUtils.respondSuccess(event, "Ciudad " + display + " en " + country.getDisplayName() + " creada con éxito.", 120);
            } catch (CityActionException | JsonProcessingException | SQLException e) {
                e.printStackTrace();
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
            }
        }
    }
}
