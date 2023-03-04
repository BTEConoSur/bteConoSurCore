package pizzaaxx.bteconosur.Discord.SlashCommands;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLJSONArrayCondition;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderExpression;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderSet;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.NumberUtils;
import pizzaaxx.bteconosur.Utils.StringUtils;
import pizzaaxx.bteconosur.Utils.WebMercatorUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pizzaaxx.bteconosur.SQL.Ordering.SQLOrderExpression.Order.ASC;

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
                                "Obtén información sobre una ciudad."
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

            try {
                this.respond(event, name);
            } catch (SQLException | IOException e) {
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
            }
        }
    }

    public void respond(IReplyCallback event, String input) throws SQLException, IOException {

        ResultSet set = plugin.getSqlManager().select(
                "cities",
                new SQLColumnSet("name", "levenshtein('" + input + "',display_name) AS distance"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "levenshtein('" + input + "',display_name)", "<=", 5
                        )
                ),
                new SQLOrderSet(
                        new SQLOrderExpression(
                                "distance", ASC
                        )
                )
        ).addText(" LIMIT 25").retrieve();

        List<String> names = new ArrayList<>();
        List<String> zeros = new ArrayList<>();
        while (set.next()) {
            if (set.getInt("distance") == 0) {
                zeros.add(set.getString("name"));
            }

            names.add(set.getString("name"));
        }
        if (names.isEmpty()) {
            DiscordUtils.respondError(event, "No se ha encontrado ninguna ciudad con ese nombre.");
        } else if (names.size() == 1) {
            this.respondCityEmbed(event, names.get(0));
        } else {
            if (zeros.isEmpty()) {
                this.respondSelector(event, names);
            } else if (zeros.size() == 1) {
                this.respondCityEmbed(event, zeros.get(0));
            } else {
                this.respondSelector(event, zeros);
            }
        }
    }

    public void respondSelector(@NotNull IReplyCallback event, @NotNull List<String> names) {

        StringSelectMenu.Builder builder = StringSelectMenu.create("cityCommandSelector?user=" + event.getUser().getId());
        builder.setPlaceholder("Selecciona una ciudad");
        for (String name : names) {
            City city = plugin.getCityManager().get(name);
            builder.addOption(
                    city.getDisplayName(),
                    city.getName(),
                    city.getCountry().getEmoji()
            );
        }

        if (event instanceof SlashCommandInteractionEvent) {
            event.reply("Se han encontrado **" + names.size() + "** opciones:").setComponents(
                    ActionRow.of(
                            builder.build()
                    ),
                    ActionRow.of(
                            plugin.getDiscordHandler().getDeleteButton(event.getUser())
                    )
            ).queue();
        } else if (event instanceof IMessageEditCallback) {
            IMessageEditCallback editCallback = (IMessageEditCallback) event;
            editCallback.editComponents(
                    ActionRow.of(
                            builder.build()
                    ),
                    ActionRow.of(
                            plugin.getDiscordHandler().getDeleteButton(event.getUser())
                    )
            ).setEmbeds().setContent("Se han encontrado **" + names.size() + "** opciones:").setFiles().queue();
        }
    }

    private void respondCityEmbed(IReplyCallback event, String cityName) throws SQLException, IOException {

        if (event instanceof SlashCommandInteractionEvent) {
            event.deferReply().queue();
        } else if (event instanceof IMessageEditCallback) {
            IMessageEditCallback editCallback = (IMessageEditCallback) event;
            editCallback.deferEdit().queue();
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);

        City city = plugin.getCityManager().get(cityName);

        builder.setTitle(city.getDisplayName() + ", " + city.getCountry().getDisplayName());

        ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) (city.hasUrbanArea() ? city.getUrbanRegion() : city.getRegion());

        double finishedArea = city.getFinishedArea() / 1000000.0;
        double totalArea = new Polygonal2DRegion(plugin.getWorldEditWorld(), region.getPoints(), 100, 100).getArea() / 1000000.0;
        double percentage = (finishedArea / totalArea) * 100;

        DecimalFormat format = new DecimalFormat("#.##");

        builder.addField(
                ":straight_ruler: Área" + (city.hasUrbanArea() ? " urbana " : " ") + "terminada:",
                "`" + format.format(finishedArea) + " km² (" + format.format(Math.min(percentage, 100)) + "%)`",
                true
        );

        builder.addField(
                ":hammer_pick: Proy. en construcción",
                "`" + city.getProjects().size() + "`",
                true
        );

        builder.addField(
                ":white_check_mark: Proy. terminados",
                "`" + city.getFinishedProjects().size() + "`",
                true
        );

        builder.addField(
                ":art: Colores:",
                "```\uD83D\uDFE2 Disponible```\n```\uD83D\uDFE1 En construcción```\n```\uD83D\uDD35 Terminado```",
                false
        );

        FileUpload file;
         try {
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

             while (Math.abs(maxTileX - minTileX) > 10 || Math.abs(maxTileY - minTileY) > 10) {
                 zoom--;

                 maxTileX = Math.floorDiv((int) WebMercatorUtils.getXFromLongitude(maxLon, zoom), 256);
                 minTileX = Math.floorDiv((int) WebMercatorUtils.getXFromLongitude(minLon, zoom), 256);
                 maxTileY = Math.floorDiv((int) WebMercatorUtils.getYFromLatitude(minLat, zoom), 256);
                 minTileY = Math.floorDiv((int) WebMercatorUtils.getYFromLatitude(maxLat, zoom), 256);

                 if (zoom == 12) {
                     break;
                 }
             }

             int xDif = Math.abs(maxTileX - minTileX);
             int yDif = Math.abs(maxTileY - minTileY);

             int xSize = 256 * (xDif + 1);
             int ySize = 256 * (yDif + 1);

             BufferedImage image = new BufferedImage(
                     xSize,
                     ySize,
                     BufferedImage.TYPE_INT_ARGB
             );
             Graphics2D g = image.createGraphics();

             double minImageLon = WebMercatorUtils.getLongitudeFromX(minTileX * 256, zoom);
             double maxImageLon = WebMercatorUtils.getLongitudeFromX((maxTileX + 1) * 256, zoom);
             double minImageLat = WebMercatorUtils.getLatitudeFromY(minTileY * 256, zoom);
             double maxImageLat = WebMercatorUtils.getLatitudeFromY((maxTileY + 1) * 256, zoom);

             GeneralPath clip = new GeneralPath();
             int counter = 0;
             for (Coords2D coord : coords) {

                 double x = NumberUtils.getInNewRange(minImageLon, maxImageLon, 0, xSize, coord.getLon());
                 double y = NumberUtils.getInNewRange(minImageLat, maxImageLat, 0, ySize, coord.getLat());

                 if (counter == 0) {
                     clip.moveTo(x, y);
                 } else {
                     clip.lineTo(x, y);
                 }
                 counter++;
             }
             clip.closePath();

             g.setClip(clip);

             for (int x = minTileX; x <= maxTileX; x++) {
                 for (int y = minTileY; y <= maxTileY; y++) {

                     BufferedImage tile = plugin.getTerramapHandler().getTile(x, y, zoom);

                     g.drawImage(tile, (x - minTileX) * 256, (y - minTileY) * 256, null);

                 }
             }

             g.dispose();

             ByteArrayOutputStream os = new ByteArrayOutputStream();
             ImageIO.write(image, "png", os);
             InputStream is = new ByteArrayInputStream(os.toByteArray());

             file = FileUpload.fromData(is, "map.png");

        } catch (Exception e) {
             e.printStackTrace();
             return;
         }
        // IMAGE

        builder.setImage("attachment://map.png");

        if (event instanceof SlashCommandInteractionEvent) {
            event.getHook().sendMessageEmbeds(
                            builder.build()
                    )
                    .addFiles(file)
                    .addComponents(
                            ActionRow.of(
                                    Button.of(
                                            ButtonStyle.SUCCESS,
                                            "cityCommandViewPosts?name=" + cityName + "&user=" + event.getUser().getId(),
                                            "Ver publicaciones",
                                            Emoji.fromUnicode("U+1F4F8")
                                    ),
                                    Button.of(
                                            ButtonStyle.PRIMARY,
                                            "cityCommandSearch?user=" + event.getUser().getId(),
                                            "Buscar otra ciudad",
                                            Emoji.fromUnicode("U+1F50E")
                                    ),
                                    plugin.getDiscordHandler().getDeleteButton(event.getUser())
                            )
                    )
                    .queue();
        } else if (event instanceof IMessageEditCallback) {
            IMessageEditCallback editCallback = (IMessageEditCallback) event;
            editCallback.getHook().editOriginalEmbeds(
                    builder.build()
            )
                    .setFiles(file)
                    .setComponents(ActionRow.of(
                            Button.of(
                                    ButtonStyle.SUCCESS,
                                    "cityCommandViewPosts?name=" + cityName + "&user=" + event.getUser().getId(),
                                    "Ver publicaciones",
                                    Emoji.fromUnicode("U+1F4F8")
                            ),
                            Button.of(
                                    ButtonStyle.PRIMARY,
                                    "cityCommandSearch?user=" + event.getUser().getId(),
                                    "Buscar otra ciudad",
                                    Emoji.fromUnicode("U+1F50E")
                            ),
                            plugin.getDiscordHandler().getDeleteButton(event.getUser())
                    )).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {

        assert event.getSelectMenu().getId() != null;
        if (event.getSelectMenu().getId().startsWith("cityCommandSelector")) {

            Map<String, String> query = StringUtils.getQuery(event.getSelectMenu().getId().split("\\?")[1]);

            if (!query.get("user").equals(event.getUser().getId())) {
                DiscordUtils.respondError(event, "Solo quién usó el comando puede seleccionar una ciudad.");
                return;
            }

            String city = event.getValues().get(0);

            try {
                this.respondCityEmbed(event, city);
            } catch (SQLException | IOException e) {
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        String id = event.getButton().getId();
        assert id != null;

        if (id.startsWith("cityCommandSearch") || id.startsWith("cityCommandViewPosts") || id.startsWith("cityCommandViewInfo")) {

            Map<String, String> query = StringUtils.getQuery(id.split("\\?")[1]);

            if (!query.get("user").equals(event.getUser().getId())) {
                DiscordUtils.respondError(event, "Solo quién usó el comando puede seleccionar una ciudad.");
                return;
            }

            if (id.startsWith("cityCommandSearch")) {

                Modal modal = Modal.create(
                        "cityCommandSearch",
                        "Busca una ciudad"
                ).addActionRows(
                        ActionRow.of(
                                TextInput.create(
                                        "name",
                                        "nombre",
                                        TextInputStyle.SHORT
                                ).setRequired(true).build()
                        )
                ).build();

                event.replyModal(modal).queue();

            } else if (id.startsWith("cityCommandViewPosts")) {

                String name = query.get("name");

                try {

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.GREEN);
                    builder.setTitle("Publicaciones de proyectos en " + plugin.getCityManager().displayNames.get(name));

                    ResultSet finishedSet = plugin.getSqlManager().select(
                            "posts",
                            new SQLColumnSet("channel_id"),
                            new SQLANDConditionSet(
                                    new SQLJSONArrayCondition("cities", name),
                                    new SQLOperatorCondition(
                                            "LENGTH(project_id)", "=", 8
                                    )
                            ),
                            new SQLOrderSet(
                                    new SQLOrderExpression(
                                            "name", ASC
                                    )
                            )
                    ).addText(" LIMIT 78").retrieve();

                    List<String> finishedLines = new ArrayList<>();
                    while (finishedSet.next()) {
                        finishedLines.add("• <#" + finishedSet.getString("channel_id") + ">");
                    }

                    builder.addField(
                            ":white_check_mark: Proyectos terminados:",
                            (finishedLines.isEmpty() ? "No hay publicaciones de proyectos terminados." : String.join("\n", finishedLines)),
                            true
                    );

                    ResultSet ongoingSet = plugin.getSqlManager().select(
                            "posts",
                            new SQLColumnSet("channel_id"),
                            new SQLANDConditionSet(
                                    new SQLJSONArrayCondition("cities", name),
                                    new SQLOperatorCondition(
                                            "LENGTH(project_id)", "=", 6
                                    )
                            ),
                            new SQLOrderSet(
                                    new SQLOrderExpression(
                                            "name", ASC
                                    )
                            )
                    ).addText(" LIMIT 78").retrieve();

                    List<String> ongoingLines = new ArrayList<>();
                    while (ongoingSet.next()) {
                        ongoingLines.add("• <#" + ongoingSet.getString("channel_id") + ">");
                    }

                    builder.addField(
                            ":hammer_pick: Proyectos en construcción:",
                            (ongoingLines.isEmpty() ? "No hay publicaciones de proyectos en construcción." : String.join("\n", ongoingLines)),
                            true
                    );

                    event.editMessageEmbeds(
                            builder.build()
                    ).setFiles().setComponents(
                            ActionRow.of(
                                    Button.of(
                                            ButtonStyle.PRIMARY,
                                            "cityCommandViewInfo?user=" + event.getUser().getId() + "&name=" + name,
                                            "Ver información",
                                            Emoji.fromUnicode("U+2139")
                                    ),
                                    plugin.getDiscordHandler().getDeleteButton(event.getUser())
                            )
                    ).queue();

                } catch (SQLException e) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                }
            } else {

                String name = query.get("name");

                try {
                    this.respondCityEmbed(event, name);
                } catch (SQLException | IOException e) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                }

            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if (event.getModalId().equals("cityCommandSearch")) {

            ModalMapping nameMapping = event.getValue("name");
            assert nameMapping != null;
            String name = nameMapping.getAsString();

            try {
                this.respond(event, name);
            } catch (SQLException | IOException e) {
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
            }

        }

    }
}
