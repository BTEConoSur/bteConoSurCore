package pizzaaxx.bteconosur.Projects.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Inventory.*;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Projects.RegionSelectors.OwnerProjectSelector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

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

        if (args.length < 1) {
            p.sendMessage(this.getPrefix() + "Introduce un subcomando. Usa §a/help project§f para obtener ayuda.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create": {


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
                        gui.setAction(
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
            manageGUI.setAction( // TRANSFERIR
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
                                transferGUI.setAction(
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
                                            confirmTransferGUI.setAction(
                                                    event2 -> {
                                                        event2.closeGUI();
                                                        try {
                                                            project.transfer(member).execute();
                                                            player.sendMessage(this.getPrefix() + "Has transferido el proyecto §a" + project.getDisplayName() + "§f a §a" + plugin.getPlayerRegistry().get(member).getName());
                                                            Bukkit.getPlayer(member).sendMessage(this.getPrefix() + "§a" + player.getName() + "§f te ha transferido el proyecto §a" + project.getDisplayName() + "§f.");
                                                        } catch (SQLException e) {
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
                                            confirmTransferGUI.setAction(
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
            manageGUI.setAction(
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
                            confirmFinishGUI.setAction(
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
