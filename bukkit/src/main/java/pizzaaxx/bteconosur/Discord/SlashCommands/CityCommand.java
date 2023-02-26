package pizzaaxx.bteconosur.Discord.SlashCommands;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.WebMercatorUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CityCommand extends ListenerAdapter implements SlashCommandContainer {

    private final BTEConoSur plugin;

    public CityCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void checkCommand() {
        plugin.getBot().retrieveCommands().queue(
                commands -> {
                    boolean found = false;
                    for (Command command : commands) {
                        if (command.getName().equals("city")) {
                            found = true;
                            break;
                        }
                    }

                    if (!found){
                        plugin.getBot().upsertCommand(
                                "city",
                                "Crea una ciudad desde Discord"
                        )
                                .addOption(
                                        OptionType.STRING,
                                        "nombre",
                                        "El nombre de la ciudad a mostrar.",
                                        true
                                )
                                .setNameLocalization(DiscordLocale.SPANISH, "ciudad")
                                .queue();
                    }
                }
        );
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("city")) {

            OptionMapping nameMapping = event.getOption("nombre");
            assert nameMapping != null;
            String name = nameMapping.getAsString();

            List<String> names = plugin.getCityManager().getCloseMatches(name, 5);

            if (names.size() == 0) {
                DiscordUtils.respondError(event, "No se ha encontrado ninguna ciudad con ese nombre.");
            } else if (names.size() == 1) {

                try {
                    this.respondCityEmbed(event, names.get(0));
                } catch (SQLException | IOException e) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error.");
                }

            } else {
                StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("cityCommandSelector");
                for (String cityName : names.subList(0, Math.min(names.size(), 25))) {
                    City city = plugin.getCityManager().get(cityName);
                    menuBuilder.addOption(
                            city.getDisplayName(),
                            cityName,
                            Emoji.fromFormatted(":flag_" + city.getCountry().getAbbreviation() + ":")
                    );
                }
                menuBuilder.setPlaceholder("Elige una ciudad");

                event.replyComponents(
                        ActionRow.of(
                                menuBuilder.build()
                        )
                ).queue();
            }
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {

        if (event.getInteraction().getId().equals("cityCommandSelector")) {

            String cityName = event.getSelectedOptions().get(0).getValue();

            try {
                this.respondCityEmbed(event, cityName);
                event.getMessage().delete().queue();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                DiscordUtils.respondError(event, "Ha ocurrido un error.");
            }
        }

    }

    private void respondCityEmbed(IReplyCallback event, String cityName) throws SQLException, IOException {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);

        City city = plugin.getCityManager().get(cityName);

        builder.setTitle(city.getDisplayName() + ", " + city.getCountry().getName());
        builder.setThumbnail(city.getCountry().getIconURL());

        ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) (city.hasUrbanArea() ? city.getUrbanRegion() : city.getRegion());

        double finishedArea = city.getFinishedArea() / 1000000.0;
        double totalArea = new Polygonal2DRegion(plugin.getWorldEditWorld(), region.getPoints(), 100, 100).getArea();
        double percentage = (finishedArea / totalArea) * 100;

        builder.addField(
                ":straight_ruler: Área" + (city.hasUrbanArea() ? " urbana " : " ") + "terminada:",
                "`" + finishedArea + " km² / " + totalArea + " km² (" + percentage + "%)`",
                true
        );

        builder.addField(
                ":hammer_pick: Proy. en construcción",
                "`" + city.getProjects().size() + "`",
                true
        );

        builder.addField(
                ":white_check:mark: Proy. terminados",
                "`" + city.getFinishedProjects().size() + "`",
                true
        );

        FileUpload file;
        {

            List<BlockVector2D> points = region.getPoints();
            List<Coords2D> coords = new ArrayList<>();
            points.forEach(point -> coords.add(new Coords2D(plugin, point)));

            double maxLon = coords.get(0).getLon();
            double minLon = coords.get(0).getLon();
            double maxLat = coords.get(0).getLat();
            double minLat = coords.get(0).getLat();

            for (Coords2D coord : coords) {

                if (maxLon < coord.getLon()) {
                    maxLon = coord.getLon();
                }

                if (minLon > coord.getLon()) {
                    minLon = coord.getLon();
                }

                if (maxLat < coord.getLat()) {
                    maxLat = coord.getLat();
                }

                if (minLat > coord.getLat()) {
                    minLat = coord.getLat();
                }

            }

            int zoom = 19;

            int maxTileX = Math.floorDiv((int) WebMercatorUtils.getXFromLongitude(maxLon, zoom), 256);
            int minTileX = Math.floorDiv((int) WebMercatorUtils.getXFromLongitude(minLon, zoom), 256);
            int maxTileY = Math.floorDiv((int) WebMercatorUtils.getYFromLatitude(maxLat, zoom), 256);
            int minTileY = Math.floorDiv((int) WebMercatorUtils.getYFromLatitude(minLat, zoom), 256);

            while (maxTileX - minTileX > 10 || maxTileY - minTileY > 10) {
                zoom--;

                maxTileX = Math.floorDiv((int) WebMercatorUtils.getXFromLongitude(maxLon, zoom), 256);
                minTileX = Math.floorDiv((int) WebMercatorUtils.getXFromLongitude(minLon, zoom), 256);
                maxTileY = Math.floorDiv((int) WebMercatorUtils.getYFromLatitude(maxLat, zoom), 256);
                minTileY = Math.floorDiv((int) WebMercatorUtils.getYFromLatitude(minLat, zoom), 256);

                if (zoom == 12) {
                    break;
                }
            }

            int xDif = maxTileX - minTileX;
            int yDif = maxTileY - minTileY;

            BufferedImage image = new BufferedImage(
                    256 * (xDif + 1),
                    256 * (yDif + 1),
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g = image.createGraphics();

            for (int x = minTileX; x <= maxTileX; x++) {

                for (int y = minTileY; y <= maxTileY; y++) {

                    BufferedImage tile = ImageIO.read(plugin.getTerramapHandler().getTileStream(x, y, zoom));

                    g.drawImage(tile, x * 256, y * 256, null);

                }
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            file = FileUpload.fromData(is, "map.png");

        } // IMAGE

        event.replyEmbeds(
                builder.build()
        )
                .addFiles(file)
                .addComponents(
                        ActionRow.of(
                                Button.of(
                                        ButtonStyle.SUCCESS,
                                        "viewCityPosts?name=" + cityName,
                                        "Ver publicaciones",
                                        Emoji.fromUnicode("U+1F4F8")
                                )
                        )
                )
                .queue();

    }
}
