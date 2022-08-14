package pizzaaxx.bteconosur.projects;

import com.sk89q.worldedit.BlockVector2D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.OldCountry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.misc.Misc.*;
import static pizzaaxx.bteconosur.projects.OldProject.Tag.*;
import static pizzaaxx.bteconosur.projects.OldProject.getAvailableProjects;
import static pizzaaxx.bteconosur.country.cities.projects.Command.ProjectsCommand.projectsPrefix;

public class PFindCommand implements Listener {

    Map<Player, OldProject.Difficulty> pRandomDifficulties = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equals("1. Elige una dificultad")) {
            e.setCancelled(true);
            if (e.getSlot() == 11 || e.getSlot() == 13 || e.getSlot() == 15) {
                OldProject.Difficulty difficulty;
                if (e.getSlot() == 11) {
                    difficulty = OldProject.Difficulty.FACIL;
                } else if (e.getSlot() == 13) {
                    difficulty = OldProject.Difficulty.INTERMEDIO;
                } else {
                    difficulty = OldProject.Difficulty.DIFICIL;
                }

                pRandomDifficulties.put((Player) e.getWhoClicked(), difficulty);

                Inventory pRandomGui = Bukkit.createInventory(null, 27, "2. Elige un tipo de proyecto");

                ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
                ItemMeta gMeta = glass.getItemMeta();
                gMeta.setDisplayName(" ");
                glass.setItemMeta(gMeta);

                for (int i=0; i < 27; i++) {
                    pRandomGui.setItem(i, glass);
                }

                OldCountry country = new OldCountry(e.getWhoClicked().getLocation());
                Map<OldProject.Tag, List<String>> availableProjects = getAvailableProjects(country, difficulty);
                int available = availableProjects.get(EDIFICIOS).size();
                pRandomGui.setItem(10, getCustomHead("§aEdificios", "§fEdificios de oficinas y otros edificios de gran magnitud.\n\n" + "§aDisponibles: §f" + available, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY2YmIzYWQ4ZGFmMGMxNDk5YjVlNDZkY2Y0MTc2YzgzNDU0MzU1M2ExYTgxODAwOWU3Njc1ZTg5NjI5NWUxYSJ9fX0="));

                available = availableProjects.get(CASAS).size();
                pRandomGui.setItem(11, getCustomHead("§aCasas", "§fBarrios residenciales y/o con tiendas y empresas de menor tamaño.\n\n" + "§aDisponibles: §f" + available, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y3Y2RlZWZjNmQzN2ZlY2FiNjc2YzU4NGJmNjIwODMyYWFhYzg1Mzc1ZTlmY2JmZjI3MzcyNDkyZDY5ZiJ9fX0="));

                available = availableProjects.get(DEPARTAMENTOS).size();
                pRandomGui.setItem(12, getCustomHead("§aDepartamentos", "§fEdificios residenciales, hoteles, y otros edificios de tamaño medio.\n\n" + "§aDisponibles: §f" + available, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk5MTBjZjYwMGQyMWEwNDA0ZDlkZjRiMGQ2NTllZDQ4NDE4NmFlMDYxNDI3MGY3YTY0MjlmNzA0ZDBiZGJjOSJ9fX0="));

                available = availableProjects.get(CENTROS_COMERCIALES).size();
                pRandomGui.setItem(13, getCustomHead("§aCentros comerciales", "§fFranjas comerciales, malls, supermercados, fábricas.\n\n" + "§aDisponibles: §f" + available, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTAzYmQwMDQyMTcyOWNkNjM1Y2QzYjQ4MjQzNDMwYWQ0N2NmNzA3MDE4YTU5MTZmZjU5NTQ5ZDVlY2Q2Zjg3OSJ9fX0="));

                available = availableProjects.get(ESTABLECIMIENTOS).size();
                pRandomGui.setItem(14, getCustomHead("§aEstablecimientos", "§fColegios, universidades, bancos, museos, estaciones, lugares históricos, iglesias, etc.\n\n" + "§aDisponibles: §f" + available, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVlOGQ2ZjVjYjdhMzVhNGRkYmRhNDZmMDQ3ODkxNWRkOWViYmNlZjkyNGViOGNhMjg4ZTkxZDE5YzhjYiJ9fX0="));

                available = availableProjects.get(PARQUES).size();
                pRandomGui.setItem(15, getCustomHead("§aParques", "§fGrandes áreas verdes y otros lugares abiertos como canchas de deportes o cerros.\n\n" + "§aDisponibles: §f" + available, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWExZmJlZjNkMGM1MWFkNmM3MTNhYTIwYzQyZGIxODM0MzRjZWM0ZmI2M2E1YTNlYWExMDFhZDNjNWY3NWQxNSJ9fX0="));

                available = availableProjects.get(CARRETERAS).size();
                pRandomGui.setItem(16, getCustomHead("§aCarreteras", "§fCarreteras, caminos, túneles, puentes, intersecciones, etc.\n\n" + "§aDisponibles: §f" + available, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGRjMGJmZmZiNTg1YjNkNGU2ZThkM2Y5Y2JiMzAzZGUyZjUyZjIwMTQ4OGQ4MjEwZmE4Y2RjNDBiYmFkNTg4ZCJ9fX0="));

                pRandomGui.setItem(26, getCustomHead("Volver", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="));

                e.getWhoClicked().openInventory(pRandomGui);
            }
        }

        if (e.getInventory().getName().equals("2. Elige un tipo de proyecto")) {
            e.setCancelled(true);

            if (e.getSlot() >= 10 && e.getSlot() <= 16) {
                OldProject.Tag tag;
                if (e.getSlot() == 10) {
                    tag = EDIFICIOS;
                } else if (e.getSlot() == 11) {
                    tag = CASAS;
                } else if (e.getSlot() == 12) {
                    tag = DEPARTAMENTOS;
                } else if (e.getSlot() == 13) {
                    tag = CENTROS_COMERCIALES;
                } else if (e.getSlot() == 14) {
                    tag = ESTABLECIMIENTOS;
                } else if (e.getSlot() == 15) {
                    tag = PARQUES;
                } else {
                    tag = CARRETERAS;
                }

                List<String> projects = getAvailableProjects(new OldCountry(e.getWhoClicked().getLocation()), pRandomDifficulties.get((Player) e.getWhoClicked())).get(tag);
                Player p = (Player) e.getWhoClicked();

                if (projects.size() > 0) {
                    double minDistance = 100000000.0;
                    OldProject closest = new OldProject(projects.get(0));

                    BlockVector2D loc1 = new BlockVector2D(p.getLocation().getX(), p.getLocation().getZ());

                    for (String id : projects) {
                        OldProject project = new OldProject(id);
                        if (loc1.distance(project.getAverageCoordinate()) < minDistance) {
                            minDistance = loc1.distance(project.getAverageCoordinate());
                            closest = project;
                        }
                    }

                    Coords2D closestLoc = new Coords2D(closest.getAverageCoordinate());
                    p.teleport(new Location(mainWorld, closest.getAverageCoordinate().getBlockX(), closestLoc.getHighestY(), closest.getAverageCoordinate().getBlockZ()));

                    p.sendMessage(projectsPrefix + "¡Proyecto encontrado!");
                    p.sendMessage(projectsPrefix + "Usa §a/project info §fpara ver información del proyecto.");
                    p.sendMessage(projectsPrefix + "Usa §a/project borders §fpara ver los bordes del proyecto.");
                    p.sendMessage(projectsPrefix + "Usa §a/project claim §fpara reclamar el proyecto.");
                } else {
                    p.closeInventory();
                    p.sendMessage(projectsPrefix + "No hay proyectos §a" + pRandomDifficulties.get((Player) e.getWhoClicked()).toString().replace("dificil", "§aDIFÍCILES").replace("facil", "§aFÁCILES").replace("intermiedio", "§aINTERMEDIOS") + "§f de tipo §a" + tag.toString().replace("_", " ").toUpperCase() + "§f disponibles en §a" + new OldCountry(e.getWhoClicked().getLocation()).getName().toUpperCase() + "§f.");
                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            ((Player) e.getWhoClicked()).performCommand("p find");
                        }
                    };
                    runnable.runTaskLater(Bukkit.getPluginManager().getPlugin("bteConoSur"), 30);
                }
            } else if (e.getSlot() == 26) {
                ((Player) e.getWhoClicked()).performCommand("p find");
            }
        }
    }
}
