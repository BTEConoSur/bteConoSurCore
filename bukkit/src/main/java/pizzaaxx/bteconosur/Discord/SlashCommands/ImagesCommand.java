package pizzaaxx.bteconosur.Discord.SlashCommands;

import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.Pair;
import pizzaaxx.bteconosur.Utils.Trio;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagesCommand extends ListenerAdapter implements SlashCommandContainer {

    private final BTEConoSur plugin;
    private final Map<String, List<String>> queryCache = new HashMap<>();

    public ImagesCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("images")) {

            String subcommandName = event.getSubcommandName();
            assert subcommandName != null;
            switch (subcommandName) {
                case "city":
                    break;
                case "address":
                    break;
            }
        }
    }

    public void respondCity() {

    }

    public void respondCitySelector() {

    }

    public void respondCityImage() {

    }

    public void respondAddress(@NotNull IReplyCallback event, @NotNull String address) throws IOException {
        event.deferReply().queue();

        String dir = String.join("+", address.split(" "));

        URL url = new URL("https://nominatim.openstreetmap.org/search?q=" + dir + "&format=json");

        JsonNode responseNode = plugin.getJSONMapper().readTree(url);

        List<Pair<Pair<String, String>, Pair<Country, City>>> applicableNodes = new ArrayList<>();

        for (JsonNode optionNode : responseNode) {
            Location loc = new Coords2D(plugin, optionNode.path("lat").asDouble(), optionNode.path("lon").asDouble()).toHighestLocation();
            Country country = plugin.getCountryManager().getCountryAt(loc);

            if (country == null) {
                continue;
            }

            City city = plugin.getCityManager().getCityAt(loc);

            applicableNodes.add(
                    new Pair<>(
                            new Pair<>(
                                    optionNode.path("name").asText(),
                                    optionNode.path("osm_id").asText()
                            ),
                            new Pair<>(
                                    country,
                                    city
                            )
                    )
            );
        }

        if (applicableNodes.isEmpty()) {
            DiscordUtils.respondErrorNonEphemeral(event, "No se han encontrado resultados dentro del Cono Sur.");
        } else if (applicableNodes.size() == 1) {
            respondAddressImage(event, applicableNodes.get(0).getKey().getValue(), 0);
        } else {
            respondAddressSelector(event, applicableNodes);
        }
    }

    public void respondAddressSelector(IReplyCallback event, @NotNull List<Pair<Pair<String, String>, Pair<Country, City>>> options) {
        if (event instanceof SlashCommandInteractionEvent) {
            event.deferReply().queue();
        } else if (event instanceof IMessageEditCallback) {
            ((IMessageEditCallback) event).deferEdit().queue();
        }

        StringSelectMenu.Builder menu = StringSelectMenu.create("imageCommandAddressSelector");

        for (Pair<Pair<String, String>, Pair<Country, City>> option : options) {
            String name = option.getKey().getKey();
            String osmID = option.getKey().getValue();
            Country country = option.getValue().getKey();
            City city = option.getValue().getValue();

            if (city != null) {
                menu.addOption(
                        name,
                        osmID,
                        city.getDisplayName(),
                        country.getEmoji()
                );
            } else {
                menu.addOption(
                        name,
                        osmID,
                        country.getEmoji()
                );
            }
        }

        menu.setPlaceholder("Se han encontrado " + menu.getOptions().size() + " opciones:");

        if (event instanceof SlashCommandInteractionEvent) {
            event.replyComponents(
                    ActionRow.of(
                            menu.build()
                    )
            ).queue();
        } else if (event instanceof IMessageEditCallback) {
            IMessageEditCallback editCallback = (IMessageEditCallback) event;
            editCallback.editComponents(
                    ActionRow.of(
                            menu.build()
                    )
            ).setReplace(true).queue();
        }
    }

    public void respondAddressImage(IReplyCallback event, String osmID, int index) throws IOException {
        if (event instanceof SlashCommandInteractionEvent) {
            event.deferReply().queue();
        } else if (event instanceof IMessageEditCallback) {
            ((IMessageEditCallback) event).deferEdit().queue();
        }

        String id;
        if (queryCache.containsKey(osmID)) {
            id = queryCache.get(osmID).get(index);
        } else {
            URL url = new URL("https://nominatim.openstreetmap.org/details.php?osmtype=N&osmid=" + osmID + "&format=json");

            JsonNode responseNode = plugin.getJSONMapper().readTree(url);

            JsonNode coordinatesNode = responseNode.path("centroid").path("coordinates");
            double lon = coordinatesNode.get(0).asDouble();
            double lat = coordinatesNode.get(1).asDouble();

            Location loc = new Coords2D(plugin, lat, lon).toHighestLocation();
            List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(loc);

            List<String> finishedProjectIDs = new ArrayList<>();

            plugin.getFinishedProjectsRegistry()

            List<String> eventIDs = new ArrayList<>();
        }

    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[] {
                Commands.slash("images", "Encuentra imágenes de diferentes lugares.")
                        .setNameLocalization(DiscordLocale.SPANISH, "imágenes")
                        .addSubcommands(
                                new SubcommandData("city", "Ve todas las imágenes de una ciudad.")
                                        .setNameLocalization(DiscordLocale.SPANISH, "ciudad")
                                        .addOptions(
                                                new OptionData(
                                                        OptionType.STRING,
                                                        "ciudad",
                                                        "Nombre de la ciudad.",
                                                        true
                                                ).setNameLocalization(
                                                        DiscordLocale.SPANISH, "ciudad"
                                                )
                                        ),
                                new SubcommandData("address", "Ve todas las imágenes cercanas a una dirección.")
                                        .setNameLocalization(DiscordLocale.SPANISH, "dirección")
                                        .addOptions(
                                                new OptionData(
                                                        OptionType.STRING,
                                                        "address",
                                                        "Dirección a buscar.",
                                                        true
                                                ).setNameLocalization(
                                                        DiscordLocale.SPANISH, "dirección"
                                                )
                                        )
                        ),
                Commands.message("Establecer proyecto").setGuildOnly(true)
        };
    }

    @Override
    public JDA getJDA() {
        return plugin.getBot();
    }
}
