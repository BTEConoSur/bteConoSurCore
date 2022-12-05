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
import pizzaaxx.bteconosur.Inventory.InventoryAction;
import pizzaaxx.bteconosur.Inventory.InventoryDataSet;
import pizzaaxx.bteconosur.Inventory.InventoryGUI;
import pizzaaxx.bteconosur.Inventory.InventoryGUIClickEvent;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Projects.RegionSelectors.OwnerProjectSelector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
            case "add": {
                if (args.length < 2) {
                    p.sendMessage(this.getPrefix() + "Introduce un jugador para agregar.");
                    return true;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                if (!offlinePlayer.isOnline()) {
                    p.sendMessage(this.getPrefix() + "El jugador introducido no está en línea.");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);

                List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(
                        p.getLocation(),
                        new OwnerProjectSelector(
                                p.getUniqueId()
                        )
                );

                if (projectIDs.size() > 1) {
                    InventoryGUI gui = new InventoryGUI(
                            6,
                            "Selecciona un proyecto para agregar a §a" + target.getName()
                    );
                    int[] emptySlots = {
                            10, 11, 12, 13, 14, 15, 16,
                            19, 20, 21, 22, 23, 24, 25,
                            28, 29, 30, 31, 32, 33, 34,
                            37, 38, 39, 40, 41, 42, 43
                    };
                    gui.setEmptySlots(
                            emptySlots
                    );
                    int i = 0;
                    for (String id : projectIDs) {
                        gui.setItem(Material.STONE, emptySlots[i]);
                        gui.setData(
                                "projectID", id,
                                emptySlots[i]
                        );
                        gui.setAction(
                                event -> {
                                    event.closeGUI();
                                    Project project = plugin.getProjectRegistry().get(id);

                                    if (project.getMembers().contains(target.getUniqueId())) {
                                        p.sendMessage(this.getPrefix() + "El jugador introducido ya es parte de este proyecto.");
                                        return;
                                    }

                                    try {
                                        project.addMember(target.getUniqueId()).execute();
                                        p.sendMessage(this.getPrefix() + "Has agregado a §a" + target.getName() + "§f al proyecto §a" + project.getDisplayName() + "§f.");
                                        target.sendMessage(this.getPrefix() + "Has sido agregado al proyecto §a" + project.getDisplayName() + "§f.");
                                    } catch (SQLException e) {
                                        p.sendMessage(this.getPrefix() + "Ha ocurrido un error en la base de datos.");
                                    }
                                },
                                emptySlots[i]
                        );
                        i++;
                    }

                } else {
                    String id = projectIDs.get(0);
                    Project project = plugin.getProjectRegistry().get(id);

                    if (project.getMembers().contains(target.getUniqueId())) {
                        p.sendMessage(this.getPrefix() + "El jugador introducido ya es parte de este proyecto.");
                        return true;
                    }

                    try {
                        project.addMember(target.getUniqueId()).execute();
                        p.sendMessage(this.getPrefix() + "Has agregado a §a" + target.getName() + "§f al proyecto §a" + project.getDisplayName() + "§f.");
                        target.sendMessage(this.getPrefix() + "Has sido agregado al proyecto §a" + project.getDisplayName() + "§f.");
                    } catch (SQLException e) {
                        p.sendMessage(this.getPrefix() + "Ha ocurrido un error en la base de datos.");
                    }
                }

                break;
            }
            case "remove": {
                if (args.length < 2) {
                    p.sendMessage(this.getPrefix() + "Introduce un miembro para quitar.");
                    return true;
                }

                ServerPlayer target;
                try {
                    target = plugin.getPlayerRegistry().get(args[1]);
                } catch (SQLException | IOException e) {
                    p.sendMessage(this.getPrefix() + "El jugador introducido no existe.");
                    return true;
                }

                List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(
                        p.getLocation(),
                        new OwnerProjectSelector(
                                p.getUniqueId()
                        )
                );

                if (projectIDs.size() > 1) {
                    InventoryGUI gui = new InventoryGUI(
                            6,
                            "Selecciona un proyecto para remover a §a" + target.getName()
                    );
                    int[] emptySlots = {
                            10, 11, 12, 13, 14, 15, 16,
                            19, 20, 21, 22, 23, 24, 25,
                            28, 29, 30, 31, 32, 33, 34,
                            37, 38, 39, 40, 41, 42, 43
                    };
                    gui.setEmptySlots(
                            emptySlots
                    );
                    int i = 0;
                    for (String id : projectIDs) {
                        gui.setItem(Material.STONE, emptySlots[i]);
                        gui.setData(
                                "projectID", id,
                                emptySlots[i]
                        );
                        gui.setAction(
                                event -> {
                                    event.closeGUI();
                                    Project project = plugin.getProjectRegistry().get(id);

                                    if (!project.getMembers().contains(target.getUUID())) {
                                        p.sendMessage(this.getPrefix() + "El jugador introducido no es parte de este proyecto.");
                                        return;
                                    }

                                    try {
                                        project.removeMember(target.getUUID()).execute();
                                        p.sendMessage(this.getPrefix() + "Has removido a §a" + target.getName() + "§f del proyecto §a" + project.getDisplayName() + "§f.");
                                        target.sendNotification(
                                                this.getPrefix() + "Has sido removido del proyecto §a" + project.getDisplayName() + "§f.",
                                                 "**[PROYECTOS]** » Has sido removido del proyecto **" + project.getDisplayName() + "**."
                                        );
                                    } catch (SQLException e) {
                                        p.sendMessage(this.getPrefix() + "Ha ocurrido un error en la base de datos.");
                                    }
                                },
                                emptySlots[i]
                        );
                        i++;
                    }

                } else {
                    String id = projectIDs.get(0);
                    Project project = plugin.getProjectRegistry().get(id);

                    if (!project.getMembers().contains(target.getUUID())) {
                        p.sendMessage(this.getPrefix() + "El jugador introducido no es parte de este proyecto.");
                        return true;
                    }

                    try {
                        project.removeMember(target.getUUID()).execute();
                        p.sendMessage(this.getPrefix() + "Has removido a §a" + target.getName() + "§f del proyecto §a" + project.getDisplayName() + "§f.");
                        target.sendNotification(
                                this.getPrefix() + "Has sido removido del proyecto §a" + project.getDisplayName() + "§f.",
                                "**[PROYECTOS]** » Has sido removido del proyecto **" + project.getDisplayName() + "**."
                        );
                    } catch (SQLException e) {
                        p.sendMessage(this.getPrefix() + "Ha ocurrido un error en la base de datos.");
                    }
                }

                break;
            }

        }

        return true;
    }

    @Override
    public String getPrefix() {
        return "§f[§dPROYECTO§f] §7>> §f";
    }
}
