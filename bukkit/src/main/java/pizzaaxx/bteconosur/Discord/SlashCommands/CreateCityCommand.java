package pizzaaxx.bteconosur.Discord.SlashCommands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.net.HttpRequest;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.Actions.CityActionException;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Player.Managers.DiscordManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.ListUtils;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class CreateCityCommand extends ListenerAdapter implements SlashCommandContainer {

    private final BTEConoSur plugin;

    public CreateCityCommand(@NotNull BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getChannelType() != ChannelType.TEXT) {
            return;
        }

        if (event.getName().equals("createcity")) {

            if (!plugin.getLinksRegistry().isLinked(event.getUser().getId())) {
                DiscordUtils.respondError(event, "Conecta tu cuenta para poder usar esto.");
                return;
            }

            ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(plugin.getLinksRegistry().get(event.getUser().getId()));

            if (!serverPlayer.getSecondaryRoles().contains(ServerPlayer.SecondaryRoles.ADMIN)) {
                DiscordUtils.respondError(event, "Solo jugadores con rango **ADMIN** pueden usar esto.");
                return;
            }

            OptionMapping fileMapping = event.getOption("archivo");
            assert fileMapping != null;
            Message.Attachment file = fileMapping.getAsAttachment();

            Country country = plugin.getCountryManager().guilds.get(event.getGuild().getId());

            try {
                URL url = new URL(file.getUrl());
                Map<String, Object> map = plugin.getJSONMapper().readValue(HttpRequest.get(url).execute().getInputStream(), HashMap.class);

                List<String> created = new ArrayList<>();
                List<String> modified = new ArrayList<>();
                List<String> noChanges = new ArrayList<>();

                for (String name : map.keySet()) {

                    Map<String, Object> cityMap = (Map<String, Object>) map.get(name);
                    String displayName = cityMap.get("displayName").toString();
                    List<Object> coordinates = (List<Object>) cityMap.get("region");

                    List<BlockVector2D> vectors = new ArrayList<>();
                    for (Object obj : coordinates) {
                        List<Double> coords = (List<Double>) obj;

                        vectors.add(new Coords2D(plugin, coords.get(1), coords.get(0)).toBlockVector2D());
                    }

                    List<Object> urbanCoordinates = (List<Object>) cityMap.get("region");

                    List<BlockVector2D> urbanVectors;
                    if (urbanCoordinates == null) {
                        urbanVectors = null;
                    } else {
                        urbanVectors = new ArrayList<>();
                        for (Object obj : urbanCoordinates) {
                            List<Double> coords = (List<Double>) obj;

                            urbanVectors.add(new Coords2D(plugin, coords.get(1), coords.get(0)).toBlockVector2D());
                        }
                    }


                    if (plugin.getCityManager().exists(name)) {

                        boolean noChange = true;

                        City city = plugin.getCityManager().get(name);
                        if (!city.getDisplayName().equals(displayName)) {
                            noChange = false;
                            city.setDisplayName(displayName).execute();
                        }

                        ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) city.getRegion();

                        if (!ListUtils.equals(region.getPoints(), vectors)) {
                            noChange = false;
                            ProtectedRegion r = new ProtectedPolygonalRegion(
                                    "city_" + city.getName(),
                                    vectors,
                                    -100,
                                    8000
                            );
                            plugin.getRegionManager().addRegion(r);
                        }

                        if (urbanVectors != null) {
                            if (!city.hasUrbanArea() || !ListUtils.equals(city.getUrbanRegion().getPoints(), urbanVectors)) {
                                noChange = false;
                                city.setUrbanArea(urbanVectors).execute();
                            }
                        } else {
                            if (city.hasUrbanArea()) {
                                noChange = false;
                                city.deleteUrbanArea().execute();
                            }
                        }

                        if (noChange) {
                            noChanges.add(displayName);
                        } else {
                            modified.add(displayName);
                        }

                    } else {

                        plugin.getCityManager().createCity(
                                name,
                                displayName,
                                country,
                                vectors
                        ).execute();

                        if (urbanCoordinates != null) {
                            City city = plugin.getCityManager().get(name);
                            city.setUrbanArea(urbanVectors).execute();
                        }

                        created.add(displayName);

                    }

                }

                event.replyEmbeds(
                        DiscordUtils.fastEmbed(
                                Color.GREEN,
                                "Ciudades creadas",
                                ":pushpin: **Ciudades creadas:** " + (created.isEmpty() ? "Ninguna." : String.join(", ", created)) +
                                        "\n:pencil2: **Ciudaded editadas:** " + (modified.isEmpty() ? "Ninguna." : String.join(", ", modified)) +
                                        "\n:book: **Ciudades sin modificaciones:** " + (noChanges.isEmpty() ? "Ninguna." : String.join(", ", noChanges))
                        )
                ).setEphemeral(true).queue();

            } catch (IOException | CityActionException | SQLException e) {
                e.printStackTrace();
                DiscordUtils.respondError(event, "Ha ocurrido un error.");
            }
        }
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(
                "createcity",
                "Crea ciudades desde Discord"
        ).addOption(
                OptionType.ATTACHMENT,
                "archivo",
                "Un archivo JSON válido.",
                true
        );
    }

    @Override
    public JDA getJDA() {
        return plugin.getBot();
    }
}
