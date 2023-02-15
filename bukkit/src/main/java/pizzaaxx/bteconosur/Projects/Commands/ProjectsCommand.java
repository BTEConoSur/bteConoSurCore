package pizzaaxx.bteconosur.Projects.Commands;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.FileUpload;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Cities.Actions.CityActionException;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Inventory.*;
import pizzaaxx.bteconosur.Player.Managers.ProjectManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.Projects.RegionSelectors.MemberProjectSelector;
import pizzaaxx.bteconosur.Projects.RegionSelectors.NonMemberProjectSelector;
import pizzaaxx.bteconosur.Projects.RegionSelectors.NotClaimedProjectSelector;
import pizzaaxx.bteconosur.Projects.RegionSelectors.OwnerProjectSelector;
import pizzaaxx.bteconosur.Projects.SQLSelectors.NotOwnerSQLSelector;
import pizzaaxx.bteconosur.Projects.SQLSelectors.OwnerSQLSelector;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.RegionUtils;
import pizzaaxx.bteconosur.Utils.SatMapHandler;
import xyz.upperlevel.spigot.book.BookUtil;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static pizzaaxx.bteconosur.Projects.Project.MAX_PROJECTS_PER_PLAYER;

public class ProjectsCommand implements CommandExecutor, Prefixable {

    private final BTEConoSur plugin;

    public ProjectsCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
            return true;
        }

        Player p = (Player) sender;
        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());
        ProjectManager projectManager = s.getProjectManager();

        if (args.length < 1) {
            p.sendMessage(this.getPrefix() + "Introduce un subcomando. Usa §a/help project§f para obtener ayuda.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create": {

                Region region;
                try {
                    region = plugin.getWorldEdit().getSelection(p);
                } catch (IncompleteRegionException e) {
                    p.sendMessage(getPrefix() + "Selecciona un área poligonal.");
                    return true;
                }

                if (!(region instanceof Polygonal2DRegion)) {
                    p.sendMessage(getPrefix() + "Selecciona un área poligonal.");
                    return true;
                }

                Polygonal2DRegion polyRegion = (Polygonal2DRegion) region;

                Location loc = new Location(plugin.getWorld(), polyRegion.getPoints().get(0).getBlockX(), 100, polyRegion.getPoints().get(0).getBlockZ());

                if (!plugin.getCountryManager().isInsideCountry(loc)) {
                    p.sendMessage("El área seleccionada no está dentro de ningún país.");
                    return true;
                }

                Country country = plugin.getCountryManager().getCountryAt(loc);

                if (country == null) {
                    p.sendMessage(getPrefix() + "El área seleccionada no está dentro de ningún país.");
                    return true;
                }

                if (projectManager.hasAdminPermission(country)) {

                    if (args.length < 2) {
                        p.sendMessage(getPrefix() + "Introduce un tipo de proyecto.");
                        return true;
                    }

                    ProjectType type = country.getProjectType(args[1]);

                    if (type == null) {
                        p.sendMessage(getPrefix() + "Introduce un tipo de proyecto válido. §7Opciones: " + country.getProjectTypes().stream().map(ProjectType::getName).collect(Collectors.joining(", ")));
                        return true;
                    }

                    if (args.length < 3) {
                        p.sendMessage(getPrefix() + "Introduce un puntaje.");
                        return true;
                    }

                    int points;
                    try {
                        points = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        p.sendMessage(getPrefix() + "Introduce un número válido.");
                        return true;
                    }

                    if (!type.getPointsOptions().contains(points)) {
                        List<String> pointsStrings = new ArrayList<>();
                        for (int pointOption : type.getPointsOptions()) {
                            pointsStrings.add(String.valueOf(pointOption));
                        }
                        p.sendMessage(getPrefix() + "El puntaje introducido no es parte de las opciones. §7Opciones: " + String.join(", ", pointsStrings));
                        return true;
                    }

                    Project project;
                    try {
                        project = plugin.getProjectRegistry().createProject(
                                country,
                                type,
                                points,
                                polyRegion.getPoints()
                        ).exec();
                        p.sendMessage(getPrefix() + "Proyecto de tipo §a" + type.getDisplayName() + "§f y puntaje §a" + points + "§f creado con la ID §a" + project.getId() + "§f.");
                    } catch (SQLException | CityActionException | IOException e) {
                        e.printStackTrace();
                        p.sendMessage(getPrefix() + "Ha ocurrido un error.");
                        return true;
                    }
                } else {

                    try {
                        if (projectManager.getProjects(new OwnerSQLSelector(p.getUniqueId())).size() >= MAX_PROJECTS_PER_PLAYER) {
                            p.sendMessage(getPrefix() + "Solo puedes ser líder de 15 proyectos a la vez.");
                            return true;
                        }
                    } catch (SQLException e) {
                        p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                        return true;
                    }

                    ResultSet set;
                    try {
                        set = plugin.getSqlManager().select(
                                "project_requests",
                                new SQLColumnSet(
                                        "message_id",
                                        "country"
                                ),
                                new SQLANDConditionSet(
                                        new SQLOperatorCondition(
                                                "owner", "=", p.getUniqueId()
                                        ),
                                        new SQLOperatorCondition(
                                                "country", "=", country.getName()
                                        )
                                )
                        ).retrieve();

                        BukkitRunnable sendRequest = new BukkitRunnable() {
                            @Override
                            public void run() {
                                EmbedBuilder builder = new EmbedBuilder();
                                builder.setColor(Color.GREEN);
                                builder.setImage("attachment://map.png");
                                builder.setTitle(s.getName() + " quiere crear un proyecto.");

                                try {
                                    StringSelectMenu.Builder typeMenu = StringSelectMenu.create("projectCreationRequestTypeMenu");
                                    typeMenu.setPlaceholder("Selecciona un tipo de proyecto");

                                    for (ProjectType type : country.getProjectTypes()) {
                                        if (type.isUnlocked(projectManager)) {
                                            typeMenu.addOption(type.getDisplayName(), type.getName());
                                        }
                                    }

                                    StringSelectMenu.Builder pointsMenu = StringSelectMenu.create("projectCreationRequestPointsMenu");
                                    pointsMenu.addOption("a", "b");
                                    pointsMenu.setPlaceholder("Selecciona un puntaje");
                                    pointsMenu.setDisabled(true);

                                    Button acceptButton = Button.of(
                                            ButtonStyle.SUCCESS,
                                            "projectCreationRequestAccept",
                                            "Aceptar",
                                            Emoji.fromCustom(
                                                    "approve",
                                                    959984723868913714L,
                                                    false
                                            )
                                    ).withDisabled(true);

                                    Button rejectButton = Button.of(
                                            ButtonStyle.DANGER,
                                            "projectCreationRequestReject",
                                            "Rechazar",
                                            Emoji.fromCustom(
                                                    "reject",
                                                    959984723789250620L,
                                                    false
                                            )
                                    );

                                    country.getRequestsChannel()
                                            .sendFiles(
                                                    FileUpload.fromData(
                                                            plugin.getSatMapHandler().getMapStream(
                                                                    new SatMapHandler.SatMapPolygon(
                                                                            plugin,
                                                                            polyRegion.getPoints(),
                                                                            "3068ff"
                                                                    )
                                                            ),
                                                            "map.png"
                                                    ))
                                            .addEmbeds(builder.build())
                                            .setComponents(
                                                    ActionRow.of(
                                                            typeMenu.build()
                                                    ),
                                                    ActionRow.of(
                                                            pointsMenu.build()
                                                    ),
                                                    ActionRow.of(
                                                            acceptButton,
                                                            rejectButton
                                                    )
                                            )
                                            .queue(
                                            message -> {
                                                try {
                                                    plugin.getSqlManager().insert(
                                                            "project_requests",
                                                            new SQLValuesSet(
                                                                    new SQLValue(
                                                                            "owner", p.getUniqueId()
                                                                    ),
                                                                    new SQLValue(
                                                                            "region_points", polyRegion.getPoints()
                                                                    ),
                                                                    new SQLValue(
                                                                            "message_id", message.getId()
                                                                            ),
                                                                    new SQLValue(
                                                                            "country", country.getName()
                                                                    )
                                                            )
                                                    ).execute();
                                                } catch (SQLException e) {
                                                    p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                                    message.delete().queue();
                                                }
                                            }
                                    );
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    p.sendMessage(getPrefix() + "Ha ocurrido un error al enviar la solicitud.");
                                    return;
                                }
                                p.sendMessage(getPrefix() + "Solicitud enviada correctamente.");
                            }
                        };

                        if (set.next()) {
                            // OPEN GUI
                            InventoryGUI gui = new InventoryGUI(
                                    1,
                                    "¿Reemplazar la última solicitud en " + country.getDisplayName() + "?"
                            );
                            gui.setItem(
                                    ItemBuilder.head(
                                            ItemBuilder.CONFIRM_HEAD,
                                            "§aConfirmar",
                                            null
                                    ),
                                    3
                            );

                            gui.setLCAction(
                                    event -> {
                                        try {
                                            String messageID = set.getString("message_id");

                                            plugin.getCountryManager().get(set.getString("country")).getRequestsChannel().deleteMessageById(messageID).queue();

                                            plugin.getSqlManager().delete(
                                                    "project_requests",
                                                    new SQLANDConditionSet(
                                                            new SQLOperatorCondition(
                                                                    "owner", "=", p.getUniqueId()
                                                            )
                                                    )
                                            ).execute();

                                            event.closeGUI();
                                        } catch (SQLException e) {
                                            event.closeGUI();
                                            p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                            return;
                                        }
                                        sendRequest.run();
                                    },
                                    3
                            );

                            gui.setItem(
                                    ItemBuilder.head(
                                            ItemBuilder.CANCEL_HEAD,
                                            "§cCancelar",
                                            null
                                    ),
                                    5
                            );
                            gui.setLCAction(
                                    InventoryGUIClickEvent::closeGUI,
                                    5
                            );
                            plugin.getInventoryHandler().open(p, gui);
                        } else {
                            sendRequest.run();
                        }
                    } catch (SQLException e) {
                        p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                        return true;
                    }


                }

                break;
            }
            case "manage": {

                List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(p.getLocation(), new MemberProjectSelector(p.getUniqueId()));

                if (projectIDs.size() == 0) {
                    p.sendMessage(getPrefix() + "No estás dentro de ningún proyecto del que seas miembro.");
                } else if (projectIDs.size() == 1) {

                    this.openManageInventory(p, projectIDs.get(0));

                } else {

                    PaginatedInventoryGUI gui = new PaginatedInventoryGUI(
                            6,
                            "Elige un proyecto para administrar"
                    );

                    for (String id : projectIDs) {

                        Project project = plugin.getProjectRegistry().get(id);
                        gui.add(
                                project.getItem(),
                                event -> this.openManageInventory(p, id),
                                null, null, null
                        );

                    }

                    gui.openTo(p, plugin);

                }

                break;

            }
            case "borders": {
                List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(p.getLocation());
                if (projectIDs.size() == 0) {
                    p.sendMessage(getPrefix() + "No hay proyectos en este lugar.");
                } else if (projectIDs.size() == 1) {
                    Project project = plugin.getProjectRegistry().get(projectIDs.get(0));
                    Polygonal2DSelection selection = new Polygonal2DSelection(
                            plugin.getWorld(),
                            project.getRegion().getPoints(),
                            p.getLocation().getBlockY() - 10,
                            p.getLocation().getBlockY() + 10
                    );
                    plugin.getWorldEdit().setSelection(p, selection);
                } else {
                    PaginatedInventoryGUI gui = new PaginatedInventoryGUI(6, "Elige un proyecto para mostrar");
                    for (String id : projectIDs) {
                        Project project = plugin.getProjectRegistry().get(id);
                        gui.add(
                                project.getItem(),
                                event -> {
                                    event.closeGUI();
                                    Polygonal2DSelection selection = new Polygonal2DSelection(
                                            plugin.getWorld(),
                                            project.getRegion().getPoints(),
                                            p.getLocation().getBlockY() - 10,
                                            p.getLocation().getBlockY() + 10
                                    );
                                    plugin.getWorldEdit().setSelection(p, selection);
                                },
                                null,
                                null,
                                null
                        );
                    }
                    gui.openTo(p, plugin);
                }
                break;
            }
            case "name": {
                if (args.length < 2) {
                    p.sendMessage(getPrefix() + "Introduce un nombre.");
                    return true;
                }

                String name = args[1];

                if (!name.matches("[a-zA-Z1-9_]{1,32}")) {
                    p.sendMessage(getPrefix() + "Introduce un nombre válido.");
                    return true;
                }

                List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(p.getLocation(), new OwnerProjectSelector(p.getUniqueId()));

                if (projectIDs.size() == 0) {
                    p.sendMessage(getPrefix() + "No hay proyectos que lideres aquí.");
                    return true;
                } else if (projectIDs.size() == 1) {
                    Project project = plugin.getProjectRegistry().get(projectIDs.get(0));
                    try {
                        String oldName = project.getDisplayName();
                        project.setDisplayName(name).execute();
                        p.sendMessage(getPrefix() + "Nombre del proyecto §a" + oldName + "§f cambiado a §a" + name + "§f.");
                    } catch (SQLException | IOException e) {
                        p.sendMessage(getPrefix() + "Ha ocurrido un error.");
                    }
                } else {
                    PaginatedInventoryGUI gui = new PaginatedInventoryGUI(
                            6,
                            "Elige un proyecto"
                    );
                    for (String id : projectIDs) {
                        Project project = plugin.getProjectRegistry().get(id);
                        gui.add(
                                project.getItem(),
                                event -> {
                                    event.closeGUI();
                                    try {
                                        String oldName = project.getDisplayName();
                                        project.setDisplayName(name).execute();
                                        p.sendMessage(getPrefix() + "Nombre del proyecto §a" + oldName + "§f cambiado a §a" + name + "§f.");
                                    } catch (SQLException | IOException e) {
                                        p.sendMessage(getPrefix() + "Ha ocurrido un error.");
                                    }
                                },
                                null,
                                null,
                                null
                        );
                    }
                    gui.openTo(p, plugin);
                }
                break;
            }
            case "list": {

                try {
                    BookUtil.BookBuilder builder = BookUtil.writtenBook();
                    List<BaseComponent[]> pages = new ArrayList<>();

                    List<String> ownerIDs = new ArrayList<>(projectManager.getProjects(new OwnerSQLSelector(p.getUniqueId())));
                    Collections.sort(ownerIDs);
                    List<String> memberIDs = new ArrayList<>(projectManager.getProjects(new NotOwnerSQLSelector(p.getUniqueId())));
                    Collections.sort(memberIDs);
                    List<String> ids = new ArrayList<>(ownerIDs);
                    ids.addAll(memberIDs);

                    for (String id : ids) {
                        Project project = plugin.getProjectRegistry().get(id);
                        BlockVector2D avgPoint = RegionUtils.getAveragePoint(project.getRegion());

                        BookUtil.PageBuilder pageBuilder = BookUtil.PageBuilder.of(BookUtil.TextBuilder.of("§7\"§0" + project.getDisplayName() + "§7\"").build());
                        pageBuilder.newLine();
                        pageBuilder.newLine();
                        pageBuilder.add("• ID: §8" + project.getId());
                        pageBuilder.newLine();
                        pageBuilder.add("• País: §8" + project.getCountry().getDisplayName());
                        pageBuilder.newLine();
                        pageBuilder.add("• Tipo: §8" + project.getType().getDisplayName());
                        pageBuilder.newLine();
                        pageBuilder.add("• Ptje.: §8" + project.getPoints());
                        pageBuilder.newLine();
                        pageBuilder.add("• Coord.: §8");
                        pageBuilder.add(
                                BookUtil.TextBuilder.of(
                                        avgPoint.getBlockX() + " " + plugin.getWorld().getHighestBlockAt(avgPoint.getBlockX(), avgPoint.getBlockZ()).getLocation().getBlockY() + " " + avgPoint.getBlockZ())
                                        .onClick(BookUtil.ClickAction.runCommand("/tp " + avgPoint.getBlockX() + " " + plugin.getWorld().getHighestBlockAt(avgPoint.getBlockX(), avgPoint.getBlockZ()).getLocation().getBlockY() + " " + avgPoint.getBlockZ()))
                                        .onHover(BookUtil.HoverAction.showText("Haz click para ir"))
                                        .build());
                        pageBuilder.newLine();
                        if (project.isClaimed()) {
                            pageBuilder.add("• Líder: §8");
                            ServerPlayer owner = plugin.getPlayerRegistry().get(project.getOwner());
                            pageBuilder.add(
                                    BookUtil.TextBuilder.of(owner.getName())
                                            .onHover(BookUtil.HoverAction.showText(String.join("\n", owner.getLore(true))))
                                            .build()
                            );
                            if (project.getMembers().size() > 0) {
                                pageBuilder.add("• Mmbrs.: §8");
                                int counter = 0;
                                for (UUID memberUUID : project.getMembers()) {
                                    ServerPlayer member = plugin.getPlayerRegistry().get(memberUUID);
                                    pageBuilder.add(
                                            BookUtil.TextBuilder.of(member.getName())
                                                    .onHover(BookUtil.HoverAction.showText(String.join(", ", member.getLore(true))))
                                                    .build()
                                    );
                                    if (counter < project.getMembers().size() - 1) {
                                        pageBuilder.add(", ");
                                    }
                                    counter++;
                                }
                            }
                        }
                        pages.add(pageBuilder.build());
                    }

                    builder.pages(pages);

                    BookUtil.openPlayer(p, builder.build());
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                }

                break;
            }
            case "claim": {
                try {
                    if (projectManager.getProjects(new OwnerSQLSelector(p.getUniqueId())).size() >= MAX_PROJECTS_PER_PLAYER) {
                        p.sendMessage(getPrefix() + "Solo puedes ser líder de 15 proyectos a la vez.");
                        return true;
                    }
                } catch (SQLException e) {
                    p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                    return true;
                }

                List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(p.getLocation(), new NonMemberProjectSelector(p.getUniqueId()), new NotClaimedProjectSelector());

                if (projectIDs.size() == 0) {
                    p.sendMessage(getPrefix() + "No hay proyectos disponibles aquí.");
                } else if (projectIDs.size() == 1) {
                    Project project = plugin.getProjectRegistry().get(projectIDs.get(0));
                    if (!project.getType().isUnlocked(projectManager)) {
                        p.sendMessage(getPrefix() + "Aún no desbloqueas los proyectos de tipo §a" + project.getType().getDisplayName() + "§f. Solo puedes reclamar proyectos de tipo §a" + project.getCountry().getUnlockedProjectTypes(projectManager).stream().map(ProjectType::getDisplayName).collect(Collectors.joining("§f, §a")) + "§f.");
                        return true;
                    }

                    if (project.isClaimed()) {
                        p.sendMessage(getPrefix() + "El proyecto ya ha sido reclamado. Usa §a/p request§f para solicitar unirte.");
                        return true;
                    }

                    try {
                        project.claim(p.getUniqueId()).execute();
                        p.sendMessage(getPrefix() + "Has reclamado el proyecto §a" + project.getDisplayName() + "§f de tipo §a" + project.getType().getDisplayName() + "§f.");
                    } catch (SQLException | IOException e) {
                        p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                    }
                } else {
                    PaginatedInventoryGUI gui = new PaginatedInventoryGUI(
                            6,
                            "Elige un proyecto para reclamar"
                    );
                    for (String id : projectIDs) {
                        Project project = plugin.getProjectRegistry().get(id);
                        gui.add(
                                project.getItem(),
                                event -> {
                                    event.closeGUI();
                                    if (!project.getType().isUnlocked(projectManager)) {
                                        p.sendMessage(getPrefix() + "Aún no desbloqueas los proyectos de tipo §a" + project.getType().getDisplayName() + "§f. Solo puedes reclamar proyectos de tipo §a" + project.getCountry().getUnlockedProjectTypes(projectManager).stream().map(ProjectType::getDisplayName).collect(Collectors.joining("§f, §a")) + "§f.");
                                        return;
                                    }

                                    if (project.isClaimed()) {
                                        p.sendMessage(getPrefix() + "El proyecto ya ha sido reclamado. Usa §a/p request§f para solicitar unirte.");
                                        return;
                                    }

                                    try {
                                        project.claim(p.getUniqueId()).execute();
                                        p.sendMessage(getPrefix() + "Has reclamado el proyecto §a" + project.getDisplayName() + "§f de tipo §a" + project.getType().getDisplayName() + "§f.");
                                    } catch (SQLException | IOException e) {
                                        p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                    }
                                },
                                null,
                                null,
                                null
                        );
                    }
                    gui.openTo(p, plugin);
                }
                break;
            }
            case "delete": {

                Country country = plugin.getCountryManager().getCountryAt(p.getLocation());

                if (country == null) {
                    p.sendMessage(getPrefix() + "No estás dentro de ningún país.");
                    return true;
                }

                if (!s.getProjectManager().hasAdminPermission(country)) {
                    p.sendMessage(getPrefix() + "No tienes permisos para administrar proyectos en §a" + country.getDisplayName() + "§f.");
                    return true;
                }

                List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(p.getLocation());

                if (projectIDs.size() == 0) {
                    p.sendMessage(getPrefix() + "No hay proyectos en este lugar.");
                } else if (projectIDs.size() == 1) {

                    Project project = plugin.getProjectRegistry().get(projectIDs.get(0));

                    InventoryGUI confirmGUI = new InventoryGUI(
                            1,
                            "¿Eliminar proyecto " + project.getDisplayName() + "?"
                    );
                    confirmGUI.setItem(
                            ItemBuilder.head(
                                    ItemBuilder.CONFIRM_HEAD,
                                    "§aConfirmar",
                                    null
                            ),
                            3
                    );
                    confirmGUI.setLCAction(
                            confirmEvent -> {
                                confirmEvent.closeGUI();
                                try {
                                    plugin.getProjectRegistry().deleteProject(
                                            project,
                                            p.getUniqueId()
                                    ).exec();
                                    p.sendMessage(getPrefix() + "Proyecto §a" + project.getId() + "§f eliminado.");
                                } catch (SQLException | IOException e) {
                                    p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                }
                            },
                            3
                    );
                    confirmGUI.setItem(
                            ItemBuilder.head(
                                    ItemBuilder.CANCEL_HEAD,
                                    "§cCancelar",
                                    null
                            ),
                            5
                    );
                    confirmGUI.setLCAction(
                            InventoryGUIClickEvent::closeGUI,
                            5
                    );
                    plugin.getInventoryHandler().open(p, confirmGUI);
                } else {
                    PaginatedInventoryGUI gui = new PaginatedInventoryGUI(
                            6,
                            "Elige un proyecto para eliminar"
                    );

                    for (String id : projectIDs) {
                        Project project = plugin.getProjectRegistry().get(id);

                        gui.add(
                                project.getItem(),
                                event -> {
                                    InventoryGUI confirmGUI = new InventoryGUI(
                                            1,
                                            "¿Eliminar proyecto " + project.getDisplayName() + "?"
                                    );
                                    confirmGUI.setItem(
                                            ItemBuilder.head(
                                                    ItemBuilder.CONFIRM_HEAD,
                                                    "§aConfirmar",
                                                    null
                                            ),
                                            3
                                    );
                                    confirmGUI.setLCAction(
                                            confirmEvent -> {
                                                confirmEvent.closeGUI();
                                                try {
                                                    plugin.getProjectRegistry().deleteProject(
                                                            project,
                                                            p.getUniqueId()
                                                    ).exec();
                                                    p.sendMessage(getPrefix() + "Proyecto §a" + project.getId() + "§f eliminado.");
                                                } catch (SQLException | IOException e) {
                                                    p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                                }
                                            },
                                            3
                                    );
                                    confirmGUI.setItem(
                                            ItemBuilder.head(
                                                    ItemBuilder.CANCEL_HEAD,
                                                    "§cCancelar",
                                                    null
                                            ),
                                            5
                                    );
                                    confirmGUI.setLCAction(
                                            cancelEvent -> {
                                                cancelEvent.closeGUI();
                                                gui.openTo(p, plugin);
                                            },
                                            5
                                    );
                                    plugin.getInventoryHandler().open(p, confirmGUI);
                                },
                                null,
                                null,
                                null
                        );
                    }
                    gui.openTo(p, plugin);
                }

                break;
            }
            case "request": {

                List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(p.getLocation(), new NonMemberProjectSelector(p.getUniqueId()));

                if (projectIDs.size() == 0) {

                    p.sendMessage(getPrefix() + "No hay proyectos (de los que no seas miembro ya) aquí.");

                } else if (projectIDs.size() == 1) {

                    Project project = plugin.getProjectRegistry().get(projectIDs.get(0));

                    if (project.isFull()) {
                        p.sendMessage(getPrefix() + "Este proyecto ya alcanzó el límite de miembros.");
                        return true;
                    }

                    if (!project.isClaimed()) {
                        p.sendMessage(getPrefix() + "Este proyecto aún no está reclamado. Reclámalo usando §a/p claim§f.");
                        return true;
                    }

                    try {
                        if (project.request(p.getUniqueId())) {
                            p.sendMessage(getPrefix() + "Solicitud enviada al líder del proyecto §a" + project.getDisplayName() + "§f.");
                        } else {
                            p.sendMessage(getPrefix() + "Ya tienes una solicitud activa en este proyecto.");
                        }
                    } catch (SQLException e) {
                        p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                    }

                } else {

                    PaginatedInventoryGUI gui = new PaginatedInventoryGUI(
                            6,
                            "Elige un proyecto para solicitar"
                    );

                    for (String id : projectIDs) {
                        Project project = plugin.getProjectRegistry().get(id);

                        gui.add(
                                project.getItem(),
                                event -> {
                                    if (project.isFull()) {
                                        p.sendMessage(getPrefix() + "Este proyecto ya alcanzó el límite de miembros.");
                                        return;
                                    }

                                    if (!project.isClaimed()) {
                                        p.sendMessage(getPrefix() + "Este proyecto aún no está reclamado. Reclámalo usando §a/p claim§f.");
                                        return;
                                    }

                                    try {
                                        if (project.request(p.getUniqueId())) {
                                            p.sendMessage(getPrefix() + "Solicitud enviada al líder del proyecto §a" + project.getDisplayName() + "§f.");
                                        } else {
                                            p.sendMessage(getPrefix() + "Ya tienes una solicitud activa en este proyecto.");
                                        }
                                    } catch (SQLException e) {
                                        p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                    }
                                },
                                null, null, null
                        );
                    }

                }

                break;
            }
        }
        return true;
    }

    private void openManageInventory(@NotNull Player player, String id) {

        ServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId());

        Project project = plugin.getProjectRegistry().get(id);

        boolean isLeader = project.getOwner().equals(player.getUniqueId());
        boolean isPending = project.isPending();

        Integer[] slots;
        if (project.getMembers().size() > 29) {
            slots = new Integer[]{
                    4,  5,  6,  7,  8,
                    13, 14, 15, 16, 17,
                    22, 23, 24, 25, 26,
                    31, 32, 33, 34, 35,
                    40, 41, 42, 43, 44
            };
        } else {
            slots = new Integer[]{
                    4,  5,  6,  7,  8,
                    13, 14, 15, 16, 17,
                    22, 23, 24, 25, 26,
                    31, 32, 33, 34, 35,
                    40, 41, 42, 43, 44,
                    49, 50, 51, 52, 53
            };
        }

        CustomSlotsPaginatedGUI gui = new CustomSlotsPaginatedGUI(
                "Proyecto " + project.getDisplayName(),
                6,
                slots,
                50,
                52
        );

        {

            List<String> lore = new ArrayList<>();
            lore.add("§aPaís: §f" + project.getCountry().getDisplayName());

            if (!project.getCities().isEmpty()) {
                lore.add("§aCiudad(es): §f" + project.getCities().stream().map(City::getDisplayName).collect(Collectors.joining(", ")));
            }

            lore.add("§aTipo: §f" + project.getType().getDisplayName());
            lore.add("§aPuntos: §f" + project.getPoints());

            if (project.getTag() != null) {
                lore.add("§aEtiqueta: §f" + project.getTag().toString());
            }

            ItemStack item = ItemBuilder.head(
                    ItemBuilder.INFO_HEAD,
                    "§d§lInformación",
                    lore
            );

            gui.setStatic(
                    10,
                    item
            );

        } // INFO

        {
            ServerPlayer owner = plugin.getPlayerRegistry().get(project.getOwner());

            InventoryAction action;

            List<String> lore = new ArrayList<>(owner.getLore(false));
            lore.add(" ");

            if (isLeader) {
                if (isPending) {
                    action = null;
                    lore.add("§8[➡] No puedes transferir el proyecto mientras está pendiente de revisión.");
                } else {
                    {
                        action = transferClickEvent -> {
                            CustomSlotsPaginatedGUI transferGUI = new CustomSlotsPaginatedGUI(
                                    "Elige un jugador para transferir",
                                    5,
                                    new Integer[]{
                                            9, 10, 11, 12, 13, 14, 15, 16, 17,
                                            18, 19, 20, 21, 22, 23, 24, 25, 26,
                                            27, 28, 29, 30, 31, 32, 33, 34, 35
                                    },
                                    0,
                                    8
                            );

                            transferGUI.setStatic(
                                    4,
                                    ItemBuilder.head(
                                            ItemBuilder.INFO_HEAD,
                                            "§aSolo puedes transferir un proyecto a miembros que estén en línea, que hayan desbloqueado el tipo de proyecto y no hayan alcanzado el máximo de proyectos.",
                                            null
                                    )
                            );

                            transferGUI.setStatic(
                                    36,
                                    ItemBuilder.head(
                                            ItemBuilder.LEFT_DOWN_CORNER_ARROW_HEAD,
                                            "Volver",
                                            null
                                    ),
                                    transferReturnClickEvent -> this.openManageInventory(player, id)
                            );

                            for (UUID memberUUID : project.getMembers()) {
                                if (Bukkit.getOfflinePlayer(memberUUID).isOnline()) {

                                    ServerPlayer member = plugin.getPlayerRegistry().get(memberUUID);

                                    try {
                                        if (!project.getType().isUnlocked(member.getProjectManager()) || member.getProjectManager().getProjects(new OwnerSQLSelector(memberUUID)).size() >= MAX_PROJECTS_PER_PLAYER) {
                                            continue;
                                        }
                                    } catch (SQLException e) {
                                        transferClickEvent.closeGUI();
                                        player.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                        break;
                                    }

                                    List<String> lore1 = new ArrayList<>(member.getLore(false));
                                    lore1.add(" ");
                                    lore1.add("§e[➡]§7 Haz click para transferir");

                                    transferGUI.addPaginated(
                                            ItemBuilder.head(
                                                    memberUUID,
                                                    "§a" + member.getName(),
                                                    lore1
                                            ),
                                            transferPlayerClickEvent -> {
                                                InventoryGUI confirmTransferGUI = new InventoryGUI(
                                                        1,
                                                        "¿Transferir a " + member.getName() + "?"
                                                );

                                                confirmTransferGUI.setItem(
                                                        ItemBuilder.head(
                                                                ItemBuilder.CONFIRM_HEAD,
                                                                "§aConfirmar",
                                                                null
                                                        ),
                                                        3
                                                );
                                                confirmTransferGUI.setLCAction(
                                                        transferConfirmClickEvent -> {
                                                            try {
                                                                project.transfer(memberUUID).execute();
                                                                player.sendMessage(getPrefix() + "Has transferido el proyecto §a" + project.getDisplayName() + "§f a §a" + member.getName() + "§f.");
                                                                this.openManageInventory(player, id);
                                                            } catch (SQLException | IOException e) {
                                                                transferConfirmClickEvent.closeGUI();
                                                                player.sendMessage(getPrefix() + "Ha ocurrido un error con la base de datos.");
                                                                this.openManageInventory(player, id);
                                                            }
                                                        },
                                                        3
                                                );

                                                confirmTransferGUI.setItem(
                                                        ItemBuilder.head(
                                                                ItemBuilder.CANCEL_HEAD,
                                                                "§cCancelar",
                                                                null
                                                        ),
                                                        5
                                                );
                                                confirmTransferGUI.setLCAction(
                                                        transferCancelClickEvent -> transferGUI.openTo(player, plugin),
                                                        5
                                                );
                                                plugin.getInventoryHandler().open(player, confirmTransferGUI);
                                            },
                                            null, null, null
                                    );

                                }
                            }

                            transferGUI.openTo(player, plugin);
                        };

                    } // ACTION
                    lore.add("§e[➡] §7Haz click para transferir el proyecto");
                }
            } else {
                action = null;
                lore.add("§8[➡] Solo el líder puede transferir el proyecto");
            }

            gui.setStatic(
                    11,
                    ItemBuilder.head(
                            project.getOwner(),
                            "§a§lLíder: §a" + owner.getName(),
                            lore
                    ),
                    action
            );
        } // TRANSFER

        {

            if (isLeader) {

                if (isPending) {
                    gui.setStatic(
                            28,
                            ItemBuilder.head(
                                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZiMGNlNjczYjNmMjhjNDYxMGNlYTdjZTA0MmM4NTBlMzRjYzk4OGNiMGQ3YzgwMzk3OWY1MGRkMGYxNTczMSJ9fX0=",
                                    "§6§lSolicitudes de unión",
                                    Collections.singletonList("§8[+] No puedes agregar miembros mientras el proyecto está pendiente de revisión.")
                            )
                    );
                } else {
                    int total = project.getRequests().size();

                    ItemStack item = ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZiMGNlNjczYjNmMjhjNDYxMGNlYTdjZTA0MmM4NTBlMzRjYzk4OGNiMGQ3YzgwMzk3OWY1MGRkMGYxNTczMSJ9fX0=",
                            "§6§lSolicitudes de unión (" + total + ")",
                            Collections.singletonList("§a[+]§7 Haz click para ver las solicitudes")
                    );

                    InventoryAction action = requestsClickEvent -> {
                        Runnable openRequestsGUI = new Runnable() {
                            @Override
                            public void run() {
                                CustomSlotsPaginatedGUI requestsGUI = new CustomSlotsPaginatedGUI(
                                        "Solicitudes de unión",
                                        5,
                                        new Integer[]{
                                                9, 10, 11, 12, 13, 14, 15, 16, 17,
                                                18, 19, 20, 21, 22, 23, 24, 25, 26,
                                                27, 28, 29, 30, 31, 32, 33, 34, 35
                                        },
                                        0,
                                        8
                                );

                                requestsGUI.setStatic(
                                        36,
                                        ItemBuilder.head(
                                                ItemBuilder.LEFT_DOWN_CORNER_ARROW_HEAD,
                                                "Volver",
                                                null
                                        ),
                                        event -> openManageInventory(player, id)
                                );

                                for (UUID targetUUID : project.getRequests()) {

                                    ServerPlayer target = plugin.getPlayerRegistry().get(targetUUID);

                                    List<String> lore = new ArrayList<>(target.getLore(false));
                                    lore.add(" ");
                                    lore.add("§a[+]§7 Haz click izquierdo para aceptar la solicitud");
                                    lore.add("§c[✕]§7 Haz click derecho para rechazar la solicitud");

                                    requestsGUI.addPaginated(
                                            ItemBuilder.head(
                                                    targetUUID,
                                                    "§a" + target.getName(),
                                                    lore
                                            ),
                                            requestAcceptClickEvent -> {
                                                try {
                                                    project.addMember(targetUUID).execute();
                                                    player.sendMessage(getPrefix() + "Has agregado a §a" + target.getName() + "§f al proyecto §a" + project.getDisplayName() + "§f.");
                                                    target.sendNotification(
                                                            getPrefix() + "§a" + player.getName() + "§f te ha agregado al proyecto §a" + project.getDisplayName() + "§f.",
                                                            "**[PROYECTO]** » **" + player.getName() + "** te ha agregado al proyecto **" + project.getDisplayName() + "**."
                                                    );
                                                    plugin.getSqlManager().delete(
                                                            "project_join_requests",
                                                            new SQLANDConditionSet(
                                                                    new SQLOperatorCondition(
                                                                            "target", "=", targetUUID
                                                                    ),
                                                                    new SQLOperatorCondition(
                                                                            "project_id", "=", id
                                                                    )
                                                            )
                                                    ).execute();
                                                    this.run();
                                                } catch (SQLException | IOException e) {
                                                    player.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                                    requestAcceptClickEvent.closeGUI();
                                                }
                                            },
                                            null,
                                            requestRejectClickEvent -> {
                                                try {
                                                    player.sendMessage(getPrefix() + "Has rechazado la solicitud de unión de §a" + target.getName() + "§f al proyecto §a" + project.getDisplayName() + "§f.");
                                                    target.sendNotification(
                                                            getPrefix() + "§a" + player.getName() + "§f ha rechazado tu solicitud de unión al proyecto §a" + project.getDisplayName() + "§f.",
                                                            "**[PROYECTO]** » **" + player.getName() + "** ha rechazado tu solicitud de unión al proyecto **" + project.getDisplayName() + "**."
                                                    );
                                                    plugin.getSqlManager().delete(
                                                            "project_join_requests",
                                                            new SQLANDConditionSet(
                                                                    new SQLOperatorCondition(
                                                                            "target", "=", targetUUID
                                                                    ),
                                                                    new SQLOperatorCondition(
                                                                            "project_id", "=", id
                                                                    )
                                                            )
                                                    ).execute();
                                                    this.run();
                                                } catch (SQLException e) {
                                                    player.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                                    requestRejectClickEvent.closeGUI();
                                                }
                                            },
                                            null
                                    );

                                }

                                requestsGUI.openTo(player, plugin);
                            }
                        };
                        openRequestsGUI.run();
                    };

                    gui.setStatic(
                            28,
                            item,
                            action
                    );
                }
            } else {
                gui.setStatic(
                        28,
                        ItemBuilder.head(
                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZiMGNlNjczYjNmMjhjNDYxMGNlYTdjZTA0MmM4NTBlMzRjYzk4OGNiMGQ3YzgwMzk3OWY1MGRkMGYxNTczMSJ9fX0=",
                                "§6§lSolicitudes de unión",
                                Collections.singletonList("§8[+] Solo el líder puede manejar las solicitudes.")
                        )
                );
            }

        } // REQUESTS

        {

            if (isLeader) {

                if (isPending) {
                    gui.setStatic(
                            29,
                            ItemBuilder.head(
                                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZiMTkzMmM0MmNkN2FmNjIxYjhlNTJmZGY0OWE0YTdmYTZmNDgwOTViYjYwOGUwNTgwNTVhZjM4YjNmMWZjNCJ9fX0=",
                                    "§e§lTerminar proyecto",
                                    Collections.singletonList("§8[✔] El proyecto ya está marcado como finalizado.")
                            )
                    );
                } else {
                    gui.setStatic(
                            29,
                            ItemBuilder.head(
                                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZiMTkzMmM0MmNkN2FmNjIxYjhlNTJmZGY0OWE0YTdmYTZmNDgwOTViYjYwOGUwNTgwNTVhZjM4YjNmMWZjNCJ9fX0=",
                                    "§e§lTerminar proyecto",
                                    Collections.singletonList("§e[✔]§f Haz click para finalizar el proyecto.")
                            ),
                            finishClickEvent -> {
                                InventoryGUI finishConfirmGUI = new InventoryGUI(
                                        1,
                                        "¿Confirmas finalizar el proyecto?"
                                );
                                finishConfirmGUI.setItem(
                                        ItemBuilder.head(
                                                ItemBuilder.CONFIRM_HEAD,
                                                "§aConfirmar",
                                                Collections.singletonList("§cEsta acción es irreversible.")
                                        ),
                                        3
                                );
                                finishConfirmGUI.setLCAction(
                                        finishConfirmClickEvent -> {
                                            project.setPending(true);
                                            player.sendMessage(getPrefix() + "Has marcado el proyecto §a" + project.getDisplayName() + "§f como terminado.");
                                            this.openManageInventory(player, id);
                                        },
                                        3
                                );

                                finishConfirmGUI.setItem(
                                        ItemBuilder.head(
                                                ItemBuilder.CANCEL_HEAD,
                                                "§cCancelar",
                                                null
                                        ),
                                        5
                                );
                                finishConfirmGUI.setLCAction(
                                        finishCancelClickEvent -> gui.openTo(player, plugin),
                                        5
                                );

                                plugin.getInventoryHandler().open(player, finishConfirmGUI);
                            }
                    );
                }

            } else {
                gui.setStatic(
                        29,
                        ItemBuilder.head(
                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZiMTkzMmM0MmNkN2FmNjIxYjhlNTJmZGY0OWE0YTdmYTZmNDgwOTViYjYwOGUwNTgwNTVhZjM4YjNmMWZjNCJ9fX0=",
                                "§e§lTerminar proyecto",
                                Collections.singletonList("§8[✔] Solo el líder puede finalizar el proyecto.")
                        )
                );
            }

        } // FINISH

        {
            if (project.getOwner().equals(player.getUniqueId())) {

                ItemStack item = ItemBuilder.head(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDcyZjg5MTVlNmJlYjM3YWRjNTQ5MWRkZTU1ZDhlNGJhZGU3Nzg2MTgwNWI5NDdiNTgzYWU2NTQ3MDU0OGJkYyJ9fX0=",
                        "§c§lAbandonar proyecto",
                        Collections.singletonList("§c[⬅]§7 Haz click para abandonar el proyecto.")
                );

                InventoryAction action = leaveClickEvent -> {
                    InventoryGUI leaveConfirmGUI = new InventoryGUI(
                            1,
                            "¿Confirmas abandonar el proyecto?"
                    );

                    leaveConfirmGUI.setItem(
                            ItemBuilder.head(
                                    ItemBuilder.CONFIRM_HEAD,
                                    "§aConfirmar",
                                    Collections.singletonList("§cEres el líder de este proyecto, si abandonas, todos los miembros serán removidos del proyecto y este quedará libre para reclamar.")
                            ),
                            3
                    );
                    leaveConfirmGUI.setLCAction(
                            leaveConfirmClickEvent -> {
                                try {
                                    player.sendMessage(getPrefix() + "Has abandonado el proyecto §a" + project.getDisplayName() + "§f.");
                                    for (UUID memberUUID : project.getMembers()) {
                                        ServerPlayer member = plugin.getPlayerRegistry().get(memberUUID);
                                        member.sendNotification(
                                                getPrefix() + "El líder de tu proyecto §a" + project.getDisplayName() + "§f ha abandonado el proyecto y con él todos los miembros han sido removidos. EL proyecto está disponible para reclamar de nuevo.",
                                                "**[PROYECTO]** » El líder de tu proyecto **" + project.getDisplayName() + "** ha abandonado el proyecto y con él todos los miembros han sido removidos. EL proyecto está disponible para reclamar de nuevo."
                                        );
                                    }
                                    project.emptyProject().execute();
                                } catch (SQLException | IOException e) {
                                    leaveConfirmClickEvent.closeGUI();
                                    player.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                }

                            },
                            3
                    );

                    leaveConfirmGUI.setItem(
                            ItemBuilder.head(
                                    ItemBuilder.CANCEL_HEAD,
                                    "§cCancelar",
                                    null
                            ),
                            5
                    );
                    leaveConfirmGUI.setLCAction(
                            leaveCancelClickEvent -> openManageInventory(player, id),
                            5
                    );

                    plugin.getInventoryHandler().open(player, leaveConfirmGUI);
                };

                gui.setStatic(
                        37,
                        item,
                        action
                );

            } else if (project.getMembers().contains(player.getUniqueId())) {

                ItemStack item = ItemBuilder.head(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDcyZjg5MTVlNmJlYjM3YWRjNTQ5MWRkZTU1ZDhlNGJhZGU3Nzg2MTgwNWI5NDdiNTgzYWU2NTQ3MDU0OGJkYyJ9fX0=",
                        "§c§lAbandonar proyecto",
                        Collections.singletonList("§c[⬅]§7 Haz click para abandonar el proyecto.")
                );

                InventoryAction action = leaveClickEvent -> {
                    InventoryGUI leaveConfirmGUI = new InventoryGUI(
                            1,
                            "¿Confirmas abandonar el proyecto?"
                    );

                    leaveConfirmGUI.setItem(
                            ItemBuilder.head(
                                    ItemBuilder.CONFIRM_HEAD,
                                    "§aConfirmar",
                                    null
                            ),
                            3
                    );
                    leaveConfirmGUI.setLCAction(
                            leaveConfirmClickEvent -> {

                                try {
                                    project.memberLeave(player.getUniqueId()).execute();
                                    plugin.getPlayerRegistry().get(project.getOwner()).sendNotification(
                                            getPrefix() + "§a" + s.getName() + "§f ha abandonado tu proyecto §a" + project.getDisplayName() + "§f.",
                                            "**[PROYECTO]** » **" + s.getName() + "** ha abandonado tu proyecto **" + project.getDisplayName() + "**."
                                    );
                                    player.sendMessage(getPrefix() + "Has abandonado el proyecto §a" + project.getDisplayName() + "§f.");
                                } catch (SQLException | IOException e) {
                                    leaveConfirmClickEvent.closeGUI();
                                    player.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                }

                            },
                            3
                    );

                    leaveConfirmGUI.setItem(
                            ItemBuilder.head(
                                    ItemBuilder.CANCEL_HEAD,
                                    "§cCancelar",
                                    null
                            ),
                            5
                    );
                    leaveConfirmGUI.setLCAction(
                            leaveCancelClickEvent -> openManageInventory(player, id),
                            5
                    );

                    plugin.getInventoryHandler().open(player, leaveConfirmGUI);
                };

                gui.setStatic(
                        37,
                        item,
                        action
                );

            } else {
                gui.setStatic(
                        37,
                        ItemBuilder.head(
                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDcyZjg5MTVlNmJlYjM3YWRjNTQ5MWRkZTU1ZDhlNGJhZGU3Nzg2MTgwNWI5NDdiNTgzYWU2NTQ3MDU0OGJkYyJ9fX0=",
                                "§c§lAbandonar proyecto",
                                Collections.singletonList("§8[⬅] No eres miembro de este proyecto.")
                        )
                );
            }
        } // LEAVE

        for (UUID memberUUID : project.getMembers()) {
            ServerPlayer member = plugin.getPlayerRegistry().get(memberUUID);

            List<String> lore = new ArrayList<>(member.getLore(false));
            lore.add(" ");
            lore.add("§c[-]§7 Haz §fShift + Click Derecho§7 para remover a §f" + member.getName() + "§7.");

            gui.addPaginated(
                    ItemBuilder.head(
                            memberUUID,
                            "§a§l" + member.getName(),
                            lore
                    ),
                    null,
                    null,
                    null,
                    removeClickEvent -> {
                        try {
                            project.removeMember(memberUUID).execute();
                            player.sendMessage(getPrefix() + "Has removido a §a" + member.getName() + "§f del proyecto.");
                            member.sendNotification(
                                    getPrefix() + "Has sido removido del proyecto §a" + project.getDisplayName() + "§f.",
                                    "**[PROYECTO]** » Has sido removido del proyecto **" + project.getDisplayName() + "**."
                            );
                            this.openManageInventory(player, id);
                        } catch (SQLException | IOException e) {
                            removeClickEvent.closeGUI();
                            player.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                        }
                    }
            );
        } // MIEMBROS

        {
            if (isLeader) {
                if (!isPending) {
                    if (project.getAllMembers().size() < project.getType().getMaxMembers()) {
                        gui.addPaginated(
                                ItemBuilder.head(
                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                                        "§a§lAgregar miembros",
                                        Collections.singletonList("§a[+]§f Haz click para agregar miembros")
                                ),
                                addEventClick -> {
                                    CustomSlotsPaginatedGUI addGUI = new CustomSlotsPaginatedGUI(
                                            "Elige un jugador para agregar",
                                            5,
                                            new Integer[]{
                                                    9, 10, 11, 12, 13, 14, 15, 16, 17,
                                                    18, 19, 20, 21, 22, 23, 24, 25, 26,
                                                    27, 28, 29, 30, 31, 32, 33, 34, 35
                                            },
                                            0,
                                            8
                                    );

                                    addGUI.setStatic(
                                            4,
                                            ItemBuilder.head(
                                                    ItemBuilder.INFO_HEAD,
                                                    "Solo puedes agregar jugadores que estén en línea",
                                                    null
                                            )
                                    );

                                    addGUI.setStatic(
                                            36,
                                            ItemBuilder.head(
                                                    ItemBuilder.LEFT_DOWN_CORNER_ARROW_HEAD,
                                                    "Volver",
                                                    null
                                            ),
                                            event -> this.openManageInventory(player, id)
                                    );

                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (!project.isMember(p.getUniqueId())) {
                                            ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(p.getUniqueId());

                                            List<String> lore = new ArrayList<>(serverPlayer.getLore(false));
                                            lore.add(" ");
                                            lore.add("§a[+] §7Haz click para agregar a §f" + serverPlayer.getName() + "§7.");

                                            addGUI.addPaginated(
                                                    ItemBuilder.head(
                                                            p.getUniqueId(),
                                                            "§a§l" + serverPlayer.getName(),
                                                            lore
                                                    ),
                                                    addPlayerClickEvent -> {
                                                        try {
                                                            project.addMember(p.getUniqueId()).execute();
                                                            player.sendMessage(getPrefix() + "Has agregado a §a" + serverPlayer.getName() + "§f al proyecto.");
                                                            serverPlayer.sendNotification(
                                                                    getPrefix() + "Has sido agregado al proyecto §a" + project.getDisplayName() + "§f.",
                                                                    "**[PROYECTO]** » Has sido agregado al proyecto **" + project.getDisplayName() + "**."
                                                            );
                                                            this.openManageInventory(player, id);
                                                        } catch (SQLException | IOException e) {
                                                            addPlayerClickEvent.closeGUI();
                                                            player.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                                        }
                                                    },
                                                    null, null, null
                                            );
                                        }
                                    }
                                    addGUI.openTo(player, plugin);
                                },
                                null, null, null
                        );
                    } else {
                        gui.addPaginated(
                                ItemBuilder.head(
                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                                        "§a§lAgregar miembros",
                                        Collections.singletonList("§8[+] El proyecto ya alcanzó el máximo de miembros.")
                                ),
                                null, null, null, null
                        );
                    }
                } else {
                    gui.addPaginated(
                            ItemBuilder.head(
                                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                                    "§a§lAgregar miembros",
                                    Collections.singletonList("§8[+] No puedes agregar miembros mientras el proyecto está pendiente de revisión.")
                            ),
                            null, null, null, null
                    );
                }
            }
        } // ADD

        gui.openTo(player, plugin);

    }

    @Override
    public String getPrefix() {
        return "§f[§dPROYECTO§f] §7>> §f";
    }
}
