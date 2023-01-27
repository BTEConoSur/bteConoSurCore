package pizzaaxx.bteconosur.Projects.Commands;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Inventory.*;
import pizzaaxx.bteconosur.Player.Managers.ProjectManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.Projects.RegionSelectors.OwnerProjectSelector;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.SatMapHandler;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static pizzaaxx.bteconosur.Utils.StringUtils.LOWER_CASE;

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

                if (projectManager.hasAdminPermission(country)) {



                } else {

                    ResultSet set;
                    try {
                        set = plugin.getSqlManager().select(
                                "project_requests",
                                new SQLColumnSet(
                                        "message_id",
                                        "country"
                                ),
                                new SQLConditionSet(
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

                                    for (ProjectType type : country.getTypes()) {
                                        typeMenu.addOption(type.getDisplayName(), type.getName());
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
                                            ItemBuilder.confirmHead(),
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
                                                    new SQLConditionSet(
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
                                            ItemBuilder.cancelHead(),
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
                List<String> projectOptions = plugin.getProjectRegistry().getProjectsAt(
                        p.getLocation(),
                        new OwnerProjectSelector(
                                p.getUniqueId()
                        )
                );

                if (projectOptions.isEmpty()) {
                    p.sendMessage(this.getPrefix() + "No hay proyectos de los que seas líder donde estás actualmente.");
                    return true;
                }

                if (projectOptions.size() == 1) {
                    this.openManageInventory(p, projectOptions.get(0));
                } else {
                    InventoryGUI gui = new InventoryGUI(
                            6,
                            "Selecciona un proyecto para manejar"
                    );
                    int[] emptySlots = {
                            10, 11, 12, 13, 14, 15, 16,
                            19, 20, 21, 22, 23, 24, 25,
                            28, 29, 30, 31, 32, 33, 34,
                            37, 38, 39, 40, 41, 42, 43
                    };
                    gui.setEmptySlots(emptySlots);
                    int i = 0;
                    for (String id : projectOptions) {
                        Project project = plugin.getProjectRegistry().get(id);
                        gui.setItem(
                                new ItemBuilder(Material.STONE)
                                        .name("Proyecto §a" + project.getDisplayName())
                                        .lore(
                                                "§fID: " + project.getId(),
                                                "§fTipo: " + project.getType().getDisplayName(),
                                                "§Miembros: " + project.getMembers().size()
                                        )
                                        .build(),
                                emptySlots[i]
                        );
                        gui.setLCAction(
                                event -> {
                                    this.openManageInventory(p, id);
                                },
                                emptySlots[i]
                        );
                        i++;
                    }
                }
            }
        }
        return true;
    }

    private void openManageInventory(Player player, String projectID) {
        Project project = plugin.getProjectRegistry().get(projectID);
        InventoryGUI manageGUI = new InventoryGUI(
                6,
                "Proyecto " + project.getId()
        );
        // Finalizar
        // Miembros -> Quitar / Agregar
        // Abandonar

        int[] memberSlots = {
                3, 4, 5, 6, 7, 8,
                12, 13, 14, 15, 16, 17,
                21, 22, 23, 24, 25, 26,
                30, 31, 32, 33, 34, 35,
                39, 40, 41, 42, 43, 44,
                48, 49, 50, 51, 52, 53
        };

        // Transferir
        manageGUI.setItem(
                ItemBuilder.head(
                        project.getOwner(),
                        "§aTransferir proyecto",
                        new ArrayList<>(
                                (project.isPending() ?
                                        Arrays.asList(
                                                "§7Haz click para transferir el proyecto a otro miembro.",
                                                "§8Solo puede transferir el proyecto a miembros actuales del proyecto que se encuentren en línea."
                                        ) :
                                        Collections.singletonList(
                                                "§cNo puedes transferir el proyecto mientras está pendiente."
                                        )
                                )
                        )
                ),
                10);
        if (!project.isPending()) {
            manageGUI.setLCAction( // TRANSFERIR
                    event -> {
                        int [] transferMemberSlots = InventoryGUI.getIntInRange(9, 53);
                        InventoryGUI transferGUI = new InventoryGUI(
                                6,
                                "Selecciona un jugador para transferir el proyecto"
                        );
                        transferGUI.setEmptySlots(
                                transferMemberSlots
                        );
                        transferGUI.setItem(
                                ItemBuilder.head(
                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlkODU5ZDZiYWYzM2VjY2RlOTk3NTAxYTc2ZThiODNjNDFhYTY4NTliOGU0ZmUxYmUyYWMwOGNjMDQ4NDMifX19",
                                        "§9Información",
                                        new ArrayList<>(
                                                Collections.singletonList(
                                                        "Solo puedes transferir proyectos a miembros de este que se encuentren en línea."
                                                )
                                        )
                                ),
                                4
                        );
                        int i = 0;
                        for (UUID member : project.getMembers()) {
                            if (Bukkit.getOfflinePlayer(member).isOnline()) {
                                ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(member);
                                transferGUI.setItem(
                                        ItemBuilder.head(
                                                member,
                                                "§a" + serverPlayer.getName(),
                                                null
                                        ),
                                        transferMemberSlots[i]
                                );
                                transferGUI.setLCAction(
                                        event1 -> {
                                            InventoryGUI confirmTransferGUI = new InventoryGUI(
                                                    1,
                                                    "¿Confirmas la transferencia?"
                                            );
                                            confirmTransferGUI.setItem(
                                                    ItemBuilder.head(
                                                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=",
                                                            "§aConfirmar",
                                                            null
                                                    ),
                                                    3
                                            );
                                            confirmTransferGUI.setLCAction(
                                                    event2 -> {
                                                        event2.closeGUI();
                                                        try {
                                                            project.transfer(member).execute();
                                                            player.sendMessage(this.getPrefix() + "Has transferido el proyecto §a" + project.getDisplayName() + "§f a §a" + plugin.getPlayerRegistry().get(member).getName());
                                                            Bukkit.getPlayer(member).sendMessage(this.getPrefix() + "§a" + player.getName() + "§f te ha transferido el proyecto §a" + project.getDisplayName() + "§f.");
                                                        } catch (SQLException | IOException e) {
                                                            player.sendMessage(this.getPrefix() + "Ha ocurrido un error en la base de datos.");
                                                        }
                                                    },
                                                    3
                                            );
                                            confirmTransferGUI.setItem(
                                                    ItemBuilder.head(
                                                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=",
                                                            "§cCancelar",
                                                            null
                                                    ),
                                                    5
                                            );
                                            confirmTransferGUI.setLCAction(
                                                    event2 -> plugin.getInventoryHandler().open(player, transferGUI),
                                                    5
                                            );
                                        },
                                        transferMemberSlots[i]
                                );
                                i++;
                            }
                        }
                    },
                    10
            );
        }
        if (project.isPending()) {
            manageGUI.setItem(
                    ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmVjYjYyYzYzYjI1NzVlYzhkYjc3MWM1N2M4YjU2MDUxNWJiNTA0MTkwMjM4YTk2MWU2ZTI0M2VmNTYwMmVkNCJ9fX0=",
                            "§cProyecto finalizado",
                            new ArrayList<>(
                                    Collections.singletonList(
                                            "§7El proyecto ya está marcado como finalizado."
                                    )
                            )
                    ),
                    28
            );
        } else {
            manageGUI.setItem(
                    ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTRkNjFlYmMyOWM2MDk3MjQwNTNlNDJmNjE1YmM3NDJhMTZlZjY4Njk2MTgyOWE2ZDAxMjcwNDUyOWIxMzA4NSJ9fX0=",
                            "§aFinalizar proyecto",
                            new ArrayList<>(
                                    Collections.singletonList(
                                            "§7No podrás construir ni editar el proyecto mientras esté pendiente."
                                    )
                            )
                    ),
                    28
            );
            manageGUI.setLCAction(
                    new InventoryAction() {
                        @Override
                        public void exec(InventoryGUIClickEvent event) {
                            InventoryGUI confirmFinishGUI = new InventoryGUI(
                                    1,
                                    "¿Confirmas que quieres finalizar el proyecto?"
                            );
                            confirmFinishGUI.setItem(
                                    ItemBuilder.head(
                                            ItemBuilder.confirmHead(),
                                            "§aConfirmar",
                                            null
                                    ),
                                    3
                            );
                            confirmFinishGUI.setItem(
                                    ItemBuilder.head(
                                            ItemBuilder.cancelHead(),
                                            "§cCancelar",
                                            null
                                    ),
                                    5
                            );
                            confirmFinishGUI.setLCAction(
                                    new InventoryAction() {
                                        @Override
                                        public void exec(InventoryGUIClickEvent event) {
                                            plugin.getInventoryHandler().open(player, manageGUI);
                                        }
                                    },
                                    5
                            );
                        }
                    },
                    28
            );
        }

    }

    @Override
    public String getPrefix() {
        return "§f[§dPROYECTO§f] §7>> §f";
    }
}
