package pizzaaxx.bteconosur.terra;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.util.net.HttpRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.gui.book.BookBuilder;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;
import static pizzaaxx.bteconosur.utils.ChatUtils.*;

public class TpdirCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public TpdirCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(PREFIX + "Solo jugadores pueden usar este comando");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(PREFIX + "Introduce una dirección.");
            return true;
        }

        // join remaining arguments with + to form a single string
        String direction = String.join("+", args);

        try {
            HttpRequest request = HttpRequest.get(new URL(
                    "https://nominatim.openstreetmap.org/search.php?q=" + direction + "&format=jsonv2"
            ));
            request.execute();
            JsonNode response = plugin.getJsonMapper().readTree(request.getInputStream());

            if (response.isEmpty()) {
                player.sendMessage(PREFIX + "No se encontró la dirección.");
                return true;
            }

            List<JsonNode> locations = StreamSupport.stream(
                            response.spliterator(), false
                    ).filter(
                            a -> {
                                TerraCoords coords = TerraCoords.fromGeo(
                                        a.get("lon").asDouble(),
                                        a.get("lat").asDouble()
                                );
                                return plugin.getCountriesRegistry().getCountryAt(coords) != null;
                            }
                    ).sorted(
                            Comparator.comparingDouble((JsonNode a) -> a.get("importance").asDouble())
                    ).toList();

            if (locations.isEmpty()) {
                player.sendMessage(PREFIX + "No se encontró la dirección dentro del Cono Sur.");
                return true;
            } else if (locations.size() == 1) {

                JsonNode location = locations.get(0);
                TerraCoords coords = TerraCoords.fromGeo(
                        location.get("lon").asDouble(),
                        location.get("lat").asDouble()
                );

                plugin.teleportAsync(
                        player,
                        coords.getX(),
                        coords.getZ(),
                        "§7[TPDIR] §8» §7Se esta generando el terreno, espera un momento...",
                        "§7[TPDIR] §8» §7Teletransportándote a §a" + location.path("name").asText() + "§7."
                );

            } else {
                BookBuilder builder = new BookBuilder();
                List<Component> lines = new ArrayList<>();

                for (JsonNode location : locations) {
                    TerraCoords coords = TerraCoords.fromGeo(
                            location.get("lon").asDouble(),
                            location.get("lat").asDouble()
                    );
                    lines.add(
                            Component.text("▪ ", Style.style(TextColor.color(GRAY)))
                                    .append(Component.text(location.path("name").asText(), Style.style(TextColor.color(BLACK)))
                                            .hoverEvent(
                                                    Component.text(location.path("display_name").asText(), Style.style(TextColor.color(DARK_GRAY)))
                                                            .append(Component.newline())
                                                            .append(Component.newline())
                                                            .append(Component.text("[•] ", Style.style(TextColor.color(GREEN))))
                                                            .append(Component.text("Haz click para teletransportarte a ", Style.style(TextColor.color(GRAY))))
                                                            .append(Component.text(location.path("name").asText(), Style.style(TextColor.color(GREEN))))
                                                            .append(Component.text(".", Style.style(TextColor.color(GRAY))))
                                            ).clickEvent(
                                                    ClickEvent.callback(
                                                            audience -> plugin.teleportAsync(
                                                                    player,
                                                                    coords.getX(),
                                                                    coords.getZ(),
                                                                    "§7[TPDIR] §8» §7Se esta generando el terreno, espera un momento...",
                                                                    "§7[TPDIR] §8» §7Teletransportándote a §a" + location.path("name").asText() + "§7."
                                                            )
                                                    )
                                            )
                            )
                    );
                }

                List<List<Component>> linesPerPage = Lists.partition(lines, 12);
                for (List<Component> pageLines : linesPerPage) {
                    builder.addPage(
                            Component.empty()
                                    .append(Component.text("  ", Style.style(TextColor.color(DARK_GRAY), TextDecoration.STRIKETHROUGH)))
                                    .decoration(TextDecoration.STRIKETHROUGH, false)
                                    .append(Component.text("◆ ", Style.style(TextColor.color(DARK_GRAY))))
                                    .append(Component.text(StringUtils.transformToSmallCapital("Direcciones"), Style.style(TextColor.color(GREEN), TextDecoration.BOLD)))
                                    .append(Component.text(" ◆", Style.style(TextColor.color(DARK_GRAY))))
                                    .append(Component.text("  ", Style.style(TextColor.color(DARK_GRAY), TextDecoration.STRIKETHROUGH)))
                                    .decoration(TextDecoration.STRIKETHROUGH, false)
                                    .append(Component.newline())
                                    .append(Component.newline())
                                    .append(Component.join(JoinConfiguration.separator(Component.newline()), pageLines).decoration(TextDecoration.BOLD, false))
                    );
                }

                builder.open(player);
            }

        } catch (IOException e) {
            player.sendMessage(PREFIX + "Error al buscar la dirección.");
        }
        return true;
    }
}
