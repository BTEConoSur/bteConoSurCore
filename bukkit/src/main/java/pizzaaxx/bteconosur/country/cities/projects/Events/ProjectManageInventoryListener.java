package pizzaaxx.bteconosur.country.cities.projects.Events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

import java.util.*;
import java.util.stream.Collectors;

import static pizzaaxx.bteconosur.BteConoSur.playerRegistry;
import static pizzaaxx.bteconosur.misc.Misc.getCustomHead;
import static pizzaaxx.bteconosur.country.cities.projects.Command.ProjectsCommand.*;

public class ProjectManageInventoryListener implements Listener {

    public final static Map<UUID, Map<Integer, String>> inventoryActions = new HashMap<>();

    private final Plugin plugin;

    public ProjectManageInventoryListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null) {
            return;
        }

        String name = inventory.getName();

        Player player = (Player) event.getWhoClicked();
        // Proyecto KIPOMP - DSTemuco

        if (name.startsWith("Proyecto ")) {

            event.setCancelled(true);

            String id = name.substring(9, 15).toLowerCase();

            if (OldProject.isProjectAt(player.getLocation()) && new OldProject(player.getLocation()).getId().equals(id)) {
                try {
                    OldProject project = new OldProject(id);

                    if (project.getOwner().getUniqueId() == player.getUniqueId()) {

                        ItemStack item = inventory.getItem(event.getSlot());
                        if (item != null && item != background && inventoryActions.containsKey(player.getUniqueId()) && inventoryActions.get(player.getUniqueId()).containsKey(event.getSlot())) {

                            String action = inventoryActions.get(player.getUniqueId()).get(event.getSlot());

                            if (action.equals("transfer")) {

                                Inventory gui = Bukkit.createInventory(null, 36, "Transferir el proyecto");

                                List<Integer> memberSlots = Arrays.asList(
                                        10, 11, 12, 13, 14, 15, 16,
                                        19, 20, 21, 22, 23, 24, 25
                                );

                                for (int i = 0; i < 36; i++) {
                                    if (!memberSlots.contains(i)) {
                                        gui.setItem(i, background);
                                    }
                                }

                                Map<Integer, String> actions = new HashMap<>();

                                int i = 0;
                                for (OfflinePlayer member : project.getMembers()) {
                                    if (member.isOnline()) {
                                        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                                        SkullMeta m = (SkullMeta) head.getItemMeta();
                                        ServerPlayer sMember = new ServerPlayer(member);
                                        m.setDisplayName("§f" + sMember.getName());
                                        m.setLore(Arrays.asList(
                                                sMember.getLoreWithoutTitle(),
                                                "\n§e[➡] §7Haz click para §etransferir §7el proyecto al jugador"
                                        ));
                                        m.setOwningPlayer(member);
                                        head.setItemMeta(m);

                                        actions.put(memberSlots.get(i), "transfer " + sMember.getPlayer().getUniqueId() + " " + project.getId());

                                        gui.setItem(memberSlots.get(i), head);

                                        i++;
                                    }

                                }


                                gui.setItem(35, getCustomHead("§f< Volver", "§0" + project.getId(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="));

                                gui.setItem(31, getCustomHead("§9[i] §fInformación", "§fSólo puedes transferir un proyecto a miembros del proyecto que estén conectados.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlkODU5ZDZiYWYzM2VjY2RlOTk3NTAxYTc2ZThiODNjNDFhYTY4NTliOGU0ZmUxYmUyYWMwOGNjMDQ4NDMifX19"));

                                player.openInventory(gui);
                                inventoryActions.put(player.getUniqueId(), actions);

                            } else if (action.startsWith("remove")) {

                                String uuid = action.replace("remove ", "");

                                player.closeInventory();

                                player.performCommand("p remove " + new ServerPlayer(UUID.fromString(uuid)).getName());

                                BukkitRunnable runnable = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.performCommand("p manage");
                                    }
                                };
                                runnable.runTaskLaterAsynchronously(plugin, 40);
                            } else if (action.equals("add")) {
                                Inventory gui = Bukkit.createInventory(null, 54, "Añadir miembros");
                                List<Integer> playerSlots = Arrays.asList(
                                        10, 11, 12, 13, 14, 15, 16,
                                        19, 20, 21, 22, 23, 24, 25,
                                        28, 29, 30, 31, 32, 33, 34,
                                        37, 38, 39, 40, 41, 42, 43
                                );
                                for (int i = 0; i < 54; i++) {
                                    if (!playerSlots.contains(i)) {
                                        gui.setItem(i, background);
                                    }
                                }
                                List<UUID> members = project.getAllMembers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList());
                                int i = 0;

                                Map<Integer, String> actions = new HashMap<>();

                                for (Player p : Bukkit.getOnlinePlayers()) {

                                    if (!members.contains(p.getUniqueId())) {
                                        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                                        SkullMeta m = (SkullMeta) head.getItemMeta();
                                        ServerPlayer sMember = new ServerPlayer(p);
                                        m.setDisplayName("§f" + sMember.getName());
                                        m.setLore(Arrays.asList(
                                                sMember.getLoreWithoutTitle(),
                                                "\n§a[+] §7Haz click para §aañadir §7al jugador al proyecto"
                                        ));
                                        m.setOwningPlayer(p);
                                        head.setItemMeta(m);

                                        actions.put(playerSlots.get(i), "add " + sMember.getPlayer().getUniqueId() + " " + project.getId());
                                        gui.setItem(playerSlots.get(i), head);

                                        i++;
                                    }
                                }

                                gui.setItem(49, getCustomHead("§9[i] §fInformación", "§fSolo puedes añadir a un proyecto a jugadores que estén conectados (y que aun no sean parte del proyecto).", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlkODU5ZDZiYWYzM2VjY2RlOTk3NTAxYTc2ZThiODNjNDFhYTY4NTliOGU0ZmUxYmUyYWMwOGNjMDQ4NDMifX19"));

                                gui.setItem(53, getCustomHead("§f< Volver", "§0" + project.getId(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="));

                                player.openInventory(gui);
                                inventoryActions.put(player.getUniqueId(), actions);
                            }
                        }

                    } else {
                        player.closeInventory();
                        player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    player.closeInventory();
                    player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
                }
            } else {
                player.closeInventory();
                player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
            }
        } else if (name.equals("Transferir el proyecto")) {

            event.setCancelled(true);

            ItemStack item = inventory.getItem(event.getSlot());

            if (event.getSlot() == 35) {
                player.closeInventory();
                player.performCommand("p manage");
                return;
            }

            if (item != null && item != background && inventoryActions.containsKey(player.getUniqueId()) && inventoryActions.get(player.getUniqueId()).containsKey(event.getSlot())) {

                String action = inventoryActions.get(player.getUniqueId()).get(event.getSlot());

                // transfer UUID id

                UUID uuid = UUID.fromString(action.split(" ")[1]);

                String id = action.split(" ")[2];

                if (OldProject.isProjectAt(player.getLocation()) && new OldProject(player.getLocation()).getId().equals(id)) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);

                    if (p.isOnline()) {
                        Inventory gui = Bukkit.createInventory(null, 9, "¿Transferir a " + playerRegistry.get(uuid).getName() + "?");

                        for (int i = 0; i < 9; i++) {
                            gui.setItem(i, background);
                        }

                        Map<Integer, String> actions = new HashMap<>();
                        gui.setItem(2, getCustomHead("§a[✔] §fConfirmar", "§cEsta acción no se puede deshacer.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0="));
                        actions.put(2, "transfer confirm " + uuid + " " + action.split(" ")[2]);
                        gui.setItem(6, getCustomHead("§c[x] §fCancelar", "§fVolverás al menú principal.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ=="));
                        player.openInventory(gui);
                        inventoryActions.put(player.getUniqueId(), actions);
                    } else {
                        player.closeInventory();
                        player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
                    }
                } else {
                    player.closeInventory();
                    player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
                }

            }

        } else if (name.equals("Añadir miembros")) {

            ItemStack item = inventory.getItem(event.getSlot());

            if (event.getSlot() == 53) {
                player.closeInventory();
                player.performCommand("p manage");
                return;
            }

            if (item != null && item != background && inventoryActions.containsKey(player.getUniqueId()) && inventoryActions.get(player.getUniqueId()).containsKey(event.getSlot())) {

                String action = inventoryActions.get(player.getUniqueId()).get(event.getSlot());

                // add UUID id

                UUID uuid = UUID.fromString(action.split(" ")[1]);

                String id = action.split(" ")[2];

                if (OldProject.isProjectAt(player.getLocation()) && new OldProject(player.getLocation()).getId().equals(id)) {

                    player.closeInventory();

                    player.performCommand("p add " + new ServerPlayer(uuid).getName());

                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.performCommand("p manage");
                        }
                    };
                    runnable.runTaskLaterAsynchronously(plugin, 40);

                } else {
                    player.closeInventory();
                    player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
                }

            }


        } else if (name.startsWith("¿Transferir a ")) {

            if (event.getSlot() == 6) {

                player.closeInventory();
                player.performCommand("p manage");

            } else if (event.getSlot() == 2) {
                String action = inventoryActions.get(player.getUniqueId()).get(2);

                // transfer confirm UUID id

                UUID uuid = UUID.fromString(action.split(" ")[2]);

                String id = action.split(" ")[3];

                if (OldProject.isProjectAt(player.getLocation()) && new OldProject(player.getLocation()).getId().equals(id) && Bukkit.getOfflinePlayer(uuid).isOnline()) {
                    transferConfirmation.add(player);
                    player.closeInventory();
                    player.performCommand("p transfer " + playerRegistry.get(uuid).getName());

                } else {
                    player.closeInventory();
                    player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
                }
            }

        }
    }

}
