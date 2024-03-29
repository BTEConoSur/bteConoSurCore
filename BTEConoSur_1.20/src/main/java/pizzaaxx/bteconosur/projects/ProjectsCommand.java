package pizzaaxx.bteconosur.projects;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import com.sk89q.worldedit.util.net.HttpRequest;
import com.sk89q.worldedit.world.World;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.FileUpload;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.cities.City;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.discord.DiscordConnector;
import pizzaaxx.bteconosur.gui.book.BookBuilder;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.projects.ProjectsManager;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardManager;
import pizzaaxx.bteconosur.projects.selectors.region.MemberSelector;
import pizzaaxx.bteconosur.projects.selectors.region.NonMemberSelector;
import pizzaaxx.bteconosur.projects.selectors.region.OwnerSelector;
import pizzaaxx.bteconosur.projects.selectors.region.ProjectRegionSelector;
import pizzaaxx.bteconosur.utils.SatMapHandler;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;
import static pizzaaxx.bteconosur.BTEConoSurPlugin.WORLDEDIT_CONNECTOR;
import static pizzaaxx.bteconosur.utils.ChatUtils.*;

public class ProjectsCommand extends ListenerAdapter implements CommandExecutor, Listener {

    private final BTEConoSurPlugin plugin;

    public static final Map<UUID, ProjectManagerInterface> MANAGER_INTERFACES = new HashMap<>();

    public ProjectsCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getPlayerClickEvent().registerBlockingCondition(
                1,
                event -> (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && ProjectsCommand.MANAGER_INTERFACES.containsKey(event.getPlayer().getUniqueId()),
                event -> {
                    int slot = event.getPlayer().getInventory().getHeldItemSlot();
                    ProjectsCommand.MANAGER_INTERFACES.get(event.getPlayer().getUniqueId()).onManageClick(slot);
                }
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo jugadores pueden usar este comando.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Introduce un subcomando.");
            return true;
        }

        switch (args[0]) {
            case "redefine" -> this.triggerActionForProjects(
                    player,
                    id -> {
                        Project project = plugin.getProjectsRegistry().get(id);
                        LocalSession session = WORLDEDIT_CONNECTOR.getLocalSession(player);
                        // get players selection
                        Region region;
                        try {
                            region = WORLDEDIT_CONNECTOR.getSelection(player);
                        } catch (IncompleteRegionException e) {
                            player.sendMessage(PREFIX + "Selecciona una región poligonal");
                            return;
                        }
                        if (!(region instanceof Polygonal2DRegion polyRegion)) {
                            player.sendMessage(PREFIX + "Selecciona una región poligonal");
                            return;
                        }
                        // get points from polygonal region
                        List<BlockVector2> points = polyRegion.getPoints();

                        // construct polygon from points
                        List<Coordinate> coordinates = new ArrayList<>();
                        for (BlockVector2 point : points) {
                            coordinates.add(new Coordinate(point.getX(), point.getZ()));
                        }
                        coordinates.add(coordinates.get(0));
                        Polygon polygon = new GeometryFactory().createPolygon(coordinates.toArray(new Coordinate[0]));

                        // get country at centroid
                        Point centroid = polygon.getCentroid();
                        Country country = plugin.getCountriesRegistry().getCountryAt(centroid.getX(), centroid.getY());

                        // check if country is the same
                        if (country == null || !country.getName().equals(project.getCountry().getName())) {
                            player.sendMessage(PREFIX + "La región seleccionada no está dentro del país del proyecto.");
                            return;
                        }

                        // get BlockVector2 from project polygon
                        List<BlockVector2> newPoints = new ArrayList<>();
                        for (Coordinate coordinate : polygon.getCoordinates()) {
                            newPoints.add(BlockVector2.at(coordinate.getX(), coordinate.getY()));
                        }

                        // get image with original project polygon and new polygon, original in red and new in blue
                        try (HttpRequest request = plugin.getSatMapHandler().getMapStream(
                                new SatMapHandler.SatMapPolygon(
                                        newPoints,
                                        "ff0000"
                                ),
                                new SatMapHandler.SatMapPolygon(
                                        points,
                                        "0000ff"
                                )
                        )) {

                            country.getRequests().sendMessageEmbeds(
                                    new EmbedBuilder()
                                            .setColor(Color.GREEN)
                                            .setImage("attachment://map.png")
                                            .build()
                            ).addFiles(
                                    FileUpload.fromData(
                                            request.getInputStream().readAllBytes(),
                                            "map.png"
                                    )
                            ).queue(); // TODO

                        } catch (IOException e) {
                            player.sendMessage(PREFIX + "Ha ocurrido un error.");
                            e.printStackTrace();
                            return;
                        }
                    },
                    new OwnerSelector(player.getUniqueId())
            );
            case "create" -> {

                // get selected region and check if its polygonal
                LocalSession session = WORLDEDIT_CONNECTOR.getLocalSession(player);
                if (session == null) {
                    player.sendMessage(PREFIX + "No has seleccionado una región.");
                    return true;
                }
                Region region;
                try {
                    region = WORLDEDIT_CONNECTOR.getSelection(player);
                } catch (IncompleteRegionException e) {
                    player.sendMessage(PREFIX + "Selecciona una región poligonal");
                    return true;
                }

                if (!(region instanceof Polygonal2DRegion polyRegion)) {
                    player.sendMessage(PREFIX + "Selecciona una región poligonal");
                    return true;
                }

                // construct polygon from polygonal region
                List<BlockVector2> points = polyRegion.getPoints();
                Coordinate[] coordinates = new Coordinate[points.size() + 1];
                for (int i = 0; i < points.size(); i++) {
                    BlockVector2 point = points.get(i);
                    coordinates[i] = new Coordinate(point.getX(), point.getZ());
                }
                coordinates[points.size()] = coordinates[0];
                Polygon polygon = new GeometryFactory().createPolygon(coordinates);

                Point centroid = polygon.getCentroid();

                // get country at centroid
                Country country = plugin.getCountriesRegistry().getCountryAt(centroid.getX(), centroid.getY());

                // warn if not within country
                if (country == null) {
                    player.sendMessage(PREFIX + "La región seleccionada no está dentro de ningún país.");
                    return true;
                }

                OfflineServerPlayer serverPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());
                Set<OfflineServerPlayer.Role> roles = serverPlayer.getRoles();
                if ((roles.contains(OfflineServerPlayer.Role.ADMIN) || roles.contains(OfflineServerPlayer.Role.MOD)) && serverPlayer.getManagedCountries().contains(country.getName())) {

                    if (args.length < 2) {
                        player.sendMessage(PREFIX + "Introduce un tipo de proyecto");
                        return true;
                    }

                    // get project type
                    ProjectType type = country.getProjectType(args[1]);
                    if (type == null) {
                        player.sendMessage(PREFIX + "Tipo de proyecto inválido.");
                        return true;
                    }

                    if (args.length < 3) {
                        player.sendMessage(PREFIX + "Introduce un puntaje para el proyecto.");
                        return true;
                    }

                    // get points
                    int pointsInt;
                    try {
                        pointsInt = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(PREFIX + "Introduce un número válido.");
                        return true;
                    }

                    if (!type.getPointOptions().contains(pointsInt)) {
                        player.sendMessage(PREFIX + "Puntaje inválido.");
                        return true;
                    }

                    // get city at centroid
                    City city = country.getCityAt(centroid.getX(), centroid.getY());
                    assert city != null;

                    try {
                        String id = plugin.getProjectsRegistry().createProject(
                                country,
                                city.getID(),
                                type,
                                pointsInt,
                                polygon
                        );
                        player.sendMessage(PREFIX + "Proyecto creado con la ID §a" + id + "§r.");
                    } catch (SQLException e) {
                        player.sendMessage(PREFIX + "Ha ocurrido un error en la base de datos.");
                        e.printStackTrace();
                        return true;
                    }
                } else {

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.GREEN);
                    builder.setTitle(serverPlayer.getName() + " quiere crear un proyecto");
                    plugin.log(
                            plugin.getSatMapHandler().getMap(
                                    new SatMapHandler.SatMapPolygon(
                                            points,
                                            "5882fa"
                                    )
                            )
                    );
                    try (HttpRequest request = plugin.getSatMapHandler().getMapStream(
                            new SatMapHandler.SatMapPolygon(
                                    points,
                                    "5882fa"
                            )
                    )) {
                        builder.setImage("attachment://map.png");

                        StringSelectMenu.Builder typeSelector = StringSelectMenu.create("projectCreateRequestType");
                        for (ProjectType type : country.getProjectTypes()) {
                            ProjectsManager manager = serverPlayer.getProjectsManager();
                            if (manager.hasUnlocked(type)) {
                                typeSelector.addOption(
                                        type.getDisplayName(),
                                        type.getName()
                                );
                            }
                        }

                        StringSelectMenu.Builder pointsSelector = StringSelectMenu.create("projectCreateRequestPoints");
                        pointsSelector.addOption("a", "a");
                        pointsSelector.setDisabled(true);

                        InputStream is = request.getInputStream();
                        byte[] bytes = is.readAllBytes();
                        country.getRequests().sendMessageEmbeds(
                                builder.build()
                        ).addFiles(
                                FileUpload.fromData(
                                        bytes,
                                        "map.png"
                                )
                        ).addComponents(
                                ActionRow.of(typeSelector.build()),
                                ActionRow.of(pointsSelector.build()),
                                ActionRow.of(
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
                                )
                        ).queue(
                                message -> {
                                    try {
                                        plugin.getSqlManager().insert(
                                                "project_creation_requests",
                                                new SQLValuesSet(
                                                        new SQLValue("owner", player.getUniqueId()),
                                                        new SQLValue("region", polygon),
                                                        new SQLValue("message_id", message.getId()),
                                                        new SQLValue("country", country)
                                                )
                                        ).execute();
                                    } catch (SQLException e) {
                                        message.delete().queue();
                                        player.sendMessage(PREFIX + "Ha ocurrido un error en la base de datos.");
                                        return;
                                    }

                                    player.sendMessage(PREFIX + "Solicitud enviada.");
                                }
                        );

                    } catch (IOException e) {
                        player.sendMessage(PREFIX + "Ha ocurrido un error.");
                        e.printStackTrace();
                        return true;
                    }
                }
            }
            case "claim" -> this.triggerActionForProjects(
                    player,
                    id -> {
                        try {
                            Project project = plugin.getProjectsRegistry().get(id);
                            if (project.isClaimed()) {
                                player.sendMessage(PREFIX + "Este proyecto ya ha sido reclamado.");
                                return;
                            }
                            project.getEditor().claim(player.getUniqueId());
                        } catch (SQLException e) {
                            e.printStackTrace();
                            player.sendMessage(PREFIX + "Ha ocurrido un error en la base de datos.");
                        }
                    },
                    new NonMemberSelector(player.getUniqueId())
            );
            case "progress" -> {
                // TODO
            }
            case "name" -> {
                if (args.length < 2) {
                    player.sendMessage(PREFIX + "Introduce un nombre para el proyecto.");
                    return true;
                }

                String name = args[1];

                if (!name.matches("[a-zA-Z0-9_-ñÑáÁéÉíÍóÓúÚ]{1,32}")) {
                    player.sendMessage(PREFIX + "Nombre inválido.");
                    return true;
                }

                this.triggerActionForProjects(
                        player,
                        id -> {
                            try {
                                Project project = plugin.getProjectsRegistry().get(id);
                                project.getEditor().setName(name);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                player.sendMessage(PREFIX + "Ha ocurrido un error en la base de datos.");
                            }
                        },
                        new OwnerSelector(player.getUniqueId())
                );
            }
            case "borders" -> this.triggerActionForProjects(
                    player,
                    id -> {
                        Project project = plugin.getProjectsRegistry().get(id);
                        LocalSession session = WORLDEDIT_CONNECTOR.getLocalSession(player);

                        World world = BukkitAdapter.adapt(player.getWorld());
                        List<BlockVector2> points = new ArrayList<>();
                        // extract points from polygon
                        for (Coordinate coordinate : project.getPolygon().getCoordinates()) {
                            points.add(BlockVector2.at(coordinate.getX(), coordinate.getY()));
                        }
                        session.setRegionSelector(
                                world,
                                new Polygonal2DRegionSelector(
                                        world,
                                        points,
                                        (int) (player.getLocation().getY() - 10),
                                        (int) (player.getLocation().getY() + 10)
                                )
                        );
                    }
            );
            case "info" -> this.triggerActionForProjects(
                    player,
                    id -> {
                        Project project = plugin.getProjectsRegistry().get(id);
                        OnlineServerPlayer osp = (OnlineServerPlayer) plugin.getPlayerRegistry().get(player.getUniqueId());
                        ScoreboardManager manager = osp.getScoreboardManager();
                        try {
                            if (manager.isAuto()) {
                                manager.setAuto(false);
                                player.sendActionBar(Component.text("Scoreboard automático desactivado.", TextColor.color(GRAY)));
                            }
                            manager.setDisplay(project);
                        } catch (SQLException e) {
                            player.sendMessage(PREFIX + "Ha ocurrido un error en la base de datos.");
                        }
                    }
            );
            case "manage" -> {

                if (MANAGER_INTERFACES.containsKey(player.getUniqueId())) {
                    MANAGER_INTERFACES.get(player.getUniqueId()).disable();
                    MANAGER_INTERFACES.remove(player.getUniqueId());
                    return true;
                }

                this.triggerActionForProjects(
                        player,
                        id -> {
                            try {
                                ProjectManagerInterface manager = new ProjectManagerInterface(plugin, player, id);
                                manager.init();
                                this.MANAGER_INTERFACES.put(player.getUniqueId(), manager);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                player.sendMessage(PREFIX + "Ha ocurrido un error en la base de datos.");
                            }
                        },
                        new MemberSelector(player.getUniqueId())
                );

            }
            case "list" -> {
                BookBuilder builder = new BookBuilder();
                List<Component> lines = new ArrayList<>();

                OfflineServerPlayer serverPlayer = plugin.getPlayerRegistry().get(player.getUniqueId());

                for (String id : serverPlayer.getProjectsManager().getProjects()) {
                    Project project = plugin.getProjectsRegistry().get(id);
                    lines.add(
                            Component.text("▪ ", Style.style(TextColor.color(GRAY)))
                                    .append(Component.text(StringUtils.transformToSmallCapital(project.getDisplayName()), TextColor.color(project.getType().getColor().getRGB()))
                                            .hoverEvent(
                                                    Component.text("Haz click para ir al proyecto ", TextColor.color(GRAY))
                                                            .append(Component.text(project.getDisplayName(), TextColor.color(project.getType().getColor().getRGB())))
                                            ).clickEvent(
                                                    ClickEvent.callback(
                                                            audience -> {
                                                                Point centroid = project.getPolygon().getCentroid();
                                                                plugin.teleportAsync(
                                                                        player,
                                                                        centroid.getX(),
                                                                        centroid.getY(),
                                                                        PREFIX + "§7Se esta generando el terreno, espera un momento...",
                                                                        PREFIX + "§7Teletransportándote a §a" + project.getDisplayName() + "§7."
                                                                );
                                                            }
                                                    )
                                            )
                                    )
                    );
                }

                List<List<Component>> linesPerPage = Lists.partition(lines, 12);
                for (List<Component> pageLines : linesPerPage) {
                    builder.addPage(
                            Component.empty()
                                    .append(Component.text("   ", Style.style(TextColor.color(DARK_GRAY), TextDecoration.STRIKETHROUGH)))
                                    .decoration(TextDecoration.STRIKETHROUGH, false)
                                    .append(Component.text("◆ ", Style.style(TextColor.color(DARK_GRAY))))
                                    .append(Component.text(StringUtils.transformToSmallCapital("Proyectos"), Style.style(TextColor.color(GREEN), TextDecoration.BOLD)))
                                    .append(Component.text(" ◆", Style.style(TextColor.color(DARK_GRAY))))
                                    .append(Component.text("   ", Style.style(TextColor.color(DARK_GRAY), TextDecoration.STRIKETHROUGH)))
                                    .decoration(TextDecoration.STRIKETHROUGH, false)
                                    .append(Component.newline())
                                    .append(Component.newline())
                                    .append(Component.join(JoinConfiguration.separator(Component.newline()), pageLines).decoration(TextDecoration.BOLD, false))
                    );
                }

                builder.open(player);
            }
        }

        return true;
    }

    public void triggerActionForProjects(
            @NotNull Player player,
            Consumer<String> action,
            ProjectRegionSelector... selectors
    ) {
        Set<String> ids = plugin.getProjectsRegistry().getProjectsAt(player.getLocation(), selectors);
        if (ids.isEmpty()) {
            player.sendMessage(PREFIX + "No estás dentro de ningún proyecto.");
        } else if (ids.size() == 1) {
            action.accept(ids.iterator().next());
        } else {
            BookBuilder builder = new BookBuilder();
            List<Component> lines = new ArrayList<>();

            for (String id : ids) {
                Project project = plugin.getProjectsRegistry().get(id);
                lines.add(
                        Component.text("▪ ", Style.style(TextColor.color(GRAY)))
                                .append(Component.text(project.getDisplayName(), TextColor.color(project.getType().getColor().getRGB()))
                                        .hoverEvent(
                                                Component.text("[•] ", Style.style(TextColor.color(GREEN)))
                                                        .append(Component.text("Haz click para seleccionar el proyecto ", TextColor.color(GRAY)))
                                                        .append(Component.text(project.getDisplayName(), TextColor.color(project.getType().getColor().getRGB())))
                                        ).clickEvent(
                                                ClickEvent.callback(
                                                        audience -> action.accept(id)
                                                )
                                        )
                                )
                );
            }

            List<List<Component>> linesPerPage = Lists.partition(lines, 12);
            for (List<Component> pageLines : linesPerPage) {
                builder.addPage(
                        Component.empty()
                                .append(Component.text("   ", Style.style(TextColor.color(DARK_GRAY), TextDecoration.STRIKETHROUGH)))
                                .decoration(TextDecoration.STRIKETHROUGH, false)
                                .append(Component.text("◆ ", Style.style(TextColor.color(DARK_GRAY))))
                                .append(Component.text(StringUtils.transformToSmallCapital("Proyectos"), Style.style(TextColor.color(GREEN), TextDecoration.BOLD)))
                                .append(Component.text(" ◆", Style.style(TextColor.color(DARK_GRAY))))
                                .append(Component.text("   ", Style.style(TextColor.color(DARK_GRAY), TextDecoration.STRIKETHROUGH)))
                                .decoration(TextDecoration.STRIKETHROUGH, false)
                                .append(Component.newline())
                                .append(Component.newline())
                                .append(Component.join(JoinConfiguration.separator(Component.newline()), pageLines).decoration(TextDecoration.BOLD, false))
                );
            }

            builder.open(player);
        }
    }

    @EventHandler
    public void onMainHandChange(@NotNull PlayerItemHeldEvent event) {
        if (MANAGER_INTERFACES.containsKey(event.getPlayer().getUniqueId())) {
            ProjectManagerInterface manager = MANAGER_INTERFACES.get(event.getPlayer().getUniqueId());
            manager.onChangeSlot(event.getNewSlot());
        }
    }
}
