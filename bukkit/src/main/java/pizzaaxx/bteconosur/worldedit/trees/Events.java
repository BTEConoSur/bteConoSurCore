package pizzaaxx.bteconosur.worldedit.trees;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pizzaaxx.bteconosur.server.player.ServerPlayer;
import pizzaaxx.bteconosur.player.data.PlayerData;
import pizzaaxx.bteconosur.worldedit.PoissonDiskSampling;

import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.*;
import static pizzaaxx.bteconosur.worldedit.trees.Tree.treePrefix;

public class Events implements Listener, CommandExecutor {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL)) {
            if (e.getClickedBlock().getType() == Material.IRON_PLATE) {
                Block target = mainWorld.getBlockAt(e.getClickedBlock().getX(), e.getClickedBlock().getY() + 1, e.getClickedBlock().getZ());
                if (target.getState() instanceof Sign) {
                    Sign s = (Sign) target.getState();
                    List<String> actualLines = new ArrayList<>();
                    for (String line : s.getLines()) {
                        if (!(line.equals(""))) {
                            actualLines.add(line);
                        }
                    }
                    String name = String.join(" ", actualLines);

                    try {
                        Tree tree = new Tree(name);

                        ItemStack sapling = new ItemStack(Material.SAPLING, 1, (byte) 0);
                        ItemMeta meta = sapling.getItemMeta();
                        meta.setDisplayName("§aÁrbol: §f" + name);
                        sapling.setItemMeta(meta);

                        if (!(e.getPlayer().getInventory().contains(sapling))) {
                            e.getPlayer().getInventory().addItem(sapling);
                            e.getPlayer().sendMessage(treePrefix + "Has conseguido el árbol §a" + StringUtils.capitalize(name) + "§f.");
                        }

                    } catch (Exception exception) {

                    }
                }
            }
        }

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.hasItem() && (e.getItem().getType().equals(Material.SAPLING))) {
                ItemMeta meta = e.getItem().getItemMeta();
                if (meta.hasDisplayName()) {
                    String name = ChatColor.stripColor(meta.getDisplayName());

                    ServerPlayer s = new ServerPlayer(e.getPlayer());

                    Block origin = e.getClickedBlock().getRelative(e.getBlockFace());

                    if (name.startsWith("Grupo:")) {
                        String group = name.replace("Grupo: ", "");

                        if (s.getTreeGroup(group) != null) {
                            List<Tree> trees = s.getTreeGroup(group);

                            int item = new Random().nextInt(trees.size()); // In real life, the Random object should be rather more shared than this
                            int i = 0;
                            for(Tree tree : trees) {
                                if (i == item)
                                    getLocalSession(e.getPlayer()).remember(tree.place(new Vector(origin.getX(), origin.getY(), origin.getZ()), e.getPlayer(), null));
                                i++;
                            }
                        } else {
                            e.getPlayer().sendMessage(treePrefix + "Grupo de árboles no encontrado.");
                        }
                    } else if (name.startsWith("Árbol:")) {
                        name = name.replace("Árbol: ", "");

                        try {
                            Tree tree = new Tree(name);

                            getLocalSession(e.getPlayer()).remember(tree.place(new Vector(origin.getX(), origin.getY(), origin.getZ()), e.getPlayer(), null));
                        } catch (Exception exception) {
                            e.getPlayer().sendMessage(treePrefix + "Árbol no encontrado.");
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("treegroup")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ServerPlayer s = new ServerPlayer(p);
                PlayerData pData = new PlayerData(p);

                if (args.length > 0) {
                    if (args[0].equals("create")) {
                        if (args.length > 1) {
                            if (args[1].matches("[a-zA-Z0-9_]{1,20}") && !(args[1].equals("create")) && !(args[1].equals("delete")) && !(args[1].equals("add")) && !(args[1].equals("remove")) && !(args[1].equals("list"))) {
                                Map<String, List<String>> treegroups;
                                if (pData.getData("treegroups") != null) {
                                    treegroups = (Map<String, List<String>>) pData.getData("treegroups");
                                } else {
                                    treegroups = new HashMap<>();
                                }

                                if (!(treegroups.containsKey(args[1]))) {
                                    List<String> trees = new ArrayList<>();

                                    if (p.getInventory().getItemInMainHand() != null) {
                                        try {
                                            Tree tree = new Tree(p.getInventory().getItemInMainHand());
                                            String treeName = tree.getName();

                                            trees.add(treeName);

                                            treegroups.put(args[1], trees);
                                            pData.setData("treegroups", treegroups);
                                            pData.save();

                                            p.sendMessage(treePrefix + "Has creado el grupo de árboles §a" + args[1] + "§f.");
                                        } catch (Exception e) {
                                            p.sendMessage(treePrefix + "Debes tener un árbol en la mano para crear un grupo.");
                                        }
                                    } else {
                                        p.sendMessage(treePrefix + "Debes tener un árbol en la mano para crear un grupo.");

                                    }
                                } else {
                                    p.sendMessage(treePrefix + "Este grupo de árboles ya existe. Elíminalo antes de volver a crearlo.");
                                }
                            } else {
                                p.sendMessage(treePrefix + "Introduce un nombre válido.");
                            }
                        } else {
                            p.sendMessage(treePrefix + "Introduce un nombre para el grupo.");
                        }
                    } else if (args[0].equals("delete")) {
                        if (args.length > 1) {
                            if (args[1].matches("[a-zA-Z0-9_]{1,20}") && !(args[1].equals("create")) && !(args[1].equals("delete")) && !(args[1].equals("add")) && !(args[1].equals("remove")) && !(args[1].equals("list"))) {
                                Map<String, List<String>> treegroups;
                                if (pData.getData("treegroups") != null) {
                                    treegroups = (Map<String, List<String>>) pData.getData("treegroups");
                                    if (treegroups.containsKey(args[1])) {
                                        treegroups.remove(args[1]);

                                        if (treegroups.size() > 0) {
                                            pData.setData("treegroups", treegroups);
                                        } else {
                                            pData.deleteData("treegroups");
                                        }

                                        pData.save();

                                        p.sendMessage(treePrefix + "Has eliminado el grupo de árboles §a" + args[1] + "§f.");
                                    }  else {
                                        p.sendMessage(treePrefix + "Este grupo de árboles no existe.");
                                    }
                                } else {
                                    p.sendMessage(treePrefix + "No tienes grupos de árboles.");
                                }
                            } else {
                                p.sendMessage(treePrefix + "Introduce un nombre válido.");
                            }
                        } else {
                            p.sendMessage(treePrefix + "Introduce un nombre");
                        }
                    } else if (args[0].equals("add")) {
                        if (args.length > 1) {
                            if (args[1].matches("[a-zA-Z0-9_]{1,20}") && !(args[1].equals("create")) && !(args[1].equals("delete")) && !(args[1].equals("add")) && !(args[1].equals("remove")) && !(args[1].equals("list"))) {
                                if (pData.getData("treegroups") != null) {
                                    Map<String, List<String>> treegroups = (Map<String, List<String>>) pData.getData("treegroups");
                                    if (treegroups.containsKey(args[1])) {
                                        List<String> trees = treegroups.get(args[1]);

                                        if (p.getInventory().getItemInMainHand() != null) {
                                            try {
                                                Tree tree = new Tree(p.getInventory().getItemInMainHand());
                                                String treeName = tree.getName();

                                                if (!(trees.contains(treeName))) {
                                                    trees.add(treeName);

                                                    treegroups.put(args[1], trees);
                                                    pData.setData("treegroups", treegroups);
                                                    pData.save();

                                                    p.sendMessage(treePrefix + "Has añadido el árbol §a" + treeName + "§f al grupo de árboles §a" + args[1] + "§f.");
                                                } else {
                                                    p.sendMessage(treePrefix + "El árbol que tienes en la mano ya es parte del grupo introducido.");
                                                }
                                            } catch (Exception e) {
                                                p.sendMessage(treePrefix + "Debes tener un árbol en la mano para añadir al grupo.");
                                            }
                                        } else {
                                            p.sendMessage(treePrefix + "Debes tener un árbol en la mano para añadir al grupo.");
                                        }
                                    } else {
                                        p.sendMessage(treePrefix + "El grupo de árboles introducido no existe.");
                                    }
                                } else {
                                    p.sendMessage(treePrefix + "El grupo de árboles introducido no existe.");
                                }
                            } else {
                                p.sendMessage(treePrefix + "Introduce un nombre válido.");
                            }
                        } else {
                            p.sendMessage(treePrefix + "Introduce el nombre del grupo.");
                        }
                    } else if (args[0].equals("remove")) {
                        if (args.length > 1) {
                            if (args[1].matches("[a-zA-Z0-9_]{1,20}") && !(args[1].equals("create")) && !(args[1].equals("delete")) && !(args[1].equals("add")) && !(args[1].equals("remove")) && !(args[1].equals("list"))) {
                                if (pData.getData("treegroups") != null) {
                                    Map<String, List<String>> treegroups = (Map<String, List<String>>) pData.getData("treegroups");
                                    if (treegroups.containsKey(args[1])) {
                                        List<String> trees = treegroups.get(args[1]);

                                        if (trees.size() < 2) {
                                            p.sendMessage(treePrefix + "Un grupo de árboles debe contener al menos 1 árbol.");
                                            return true;
                                        }

                                        if (args.length > 2 && args[2].matches("[0-9]{1,100}")) {
                                            int index = Integer.parseInt(args[2]);

                                            if (index - 1 < trees.size()) {
                                                String name = trees.get(index-1);

                                                trees.remove(index-1);

                                                if (trees.size() > 0) {
                                                    treegroups.put(args[1], trees);
                                                } else {
                                                    treegroups.remove(args[1]);
                                                }

                                                pData.setData("treegroups", treegroups);
                                                pData.save();

                                                p.sendMessage(treePrefix + "Has quitado el árbol §a" + name + "§f del grupo de árboles §a" + args[1] + "§f.");
                                            } else {
                                                p.sendMessage(treePrefix + "El número excede el número de árboles en el grupo.");
                                            }
                                        } else {
                                            p.sendMessage(treePrefix + "Introduce el número del árbol a remover.");
                                        }
                                    } else {
                                        p.sendMessage(treePrefix + "El grupo de árboles introducido no existe.");
                                    }
                                } else {
                                    p.sendMessage(treePrefix + "El grupo de árboles introducido no existe.");
                                }
                            } else {
                                p.sendMessage(treePrefix + "Introduce un nombre válido.");
                            }
                        } else {
                            p.sendMessage(treePrefix + "Introduce el nombre del grupo.");
                        }
                    } else if (args[0].equals("list")) {
                        if (args.length > 1) {
                            if (args[1].matches("[a-zA-Z0-9_]{1,20}") && !(args[1].equals("create")) && !(args[1].equals("delete")) && !(args[1].equals("add")) && !(args[1].equals("remove")) && !(args[1].equals("list"))) {
                                Map<String, List<String>> treegroups;
                                if (pData.getData("treegroups") != null) {
                                    treegroups = (Map<String, List<String>>) pData.getData("treegroups");
                                    if (treegroups.containsKey(args[1])) {
                                        p.sendMessage(">+-------+[-< §a§lGRUPO DE ÁRBOLES §f>-]+-------+<");

                                        int i = 1;
                                        for (Tree tree : s.getTreeGroup(args[1])) {
                                            p.sendMessage("§7" + i + ". §a" + tree.getName());
                                            i++;
                                        }

                                        p.sendMessage(">+-------+[-< ================== §f>-]+-------+<");
                                    }  else {
                                        p.sendMessage(treePrefix + "Este grupo de árboles no existe.");
                                    }
                                } else {
                                    p.sendMessage(treePrefix + "No tienes grupos de árboles.");
                                }
                            } else {
                                p.sendMessage(treePrefix + "Introduce un nombre válido.");
                            }
                        } else {
                            if (pData.getData("treegroups") != null) {
                                p.sendMessage(">+-------+[-< §a§lGRUPOS DE ÁRBOLES §f>-]+-------+<");

                                int i = 1;
                                for (Map.Entry<String, List<String>> entry: ((Map<String, List<String>>) pData.getData("treegroups")).entrySet()) {
                                    p.sendMessage("§7" + i + ". §a" + entry.getKey());
                                    i++;
                                }

                                p.sendMessage(">+-------+[-< ================ §f>-]+-------+<");
                            } else {
                                p.sendMessage(treePrefix + "No tienes grupos de árboles.");
                            }
                        }
                    } else {
                        if (args[0].matches("[a-zA-Z0-9_]{1,20}")) {
                            if (s.getTreeGroup(args[0]) != null) {
                                s.getTreeGroup(args[0]);

                                ItemStack tree = new ItemStack(Material.SAPLING);
                                ItemMeta meta = tree.getItemMeta();
                                meta.setDisplayName("§aGrupo: §f" + args[0]);
                                tree.setItemMeta(meta);

                                p.getInventory().addItem(tree);

                                p.sendMessage(treePrefix + "Has conseguido el árbol del grupo de árboles §a" + args[0] + "§f.");
                            } else {
                                p.sendMessage(wePrefix + "El grupo de árboles introducido no existe.");
                            }
                        } else {
                            p.sendMessage(treePrefix + "Introduce un nombre válido.");
                        }
                    }
                }
            }
        }

        if (command.getName().equals("/treecover")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ServerPlayer s = new ServerPlayer(p);

                List<Tree> trees = new ArrayList<>();
                int radius = 2;

                if (args.length > 0 && args[0].matches("[0-9]{1,5}") && Integer.parseInt(args[0]) >= 2) {
                    radius = Integer.parseInt(args[0]);

                    if (args.length > 1) {

                        if (s.getTreeGroup(args[1]) != null) {

                            trees = s.getTreeGroup(args[1]);

                        } else {
                            p.sendMessage(wePrefix + "El grupo de árboles introducido no existe.");
                            return true;
                        }
                    } else {
                        try {
                            Tree tree = new Tree(p.getInventory().getItemInMainHand());

                            trees.add(tree);
                        } catch (Exception e) {
                            p.sendMessage(wePrefix + "Introduce el nombre de un grupo de árboles o ten un árbol en la mano.");
                            return true;
                        }
                    }


                } else {
                    p.sendMessage(wePrefix + "Introduce una distancia mínima (Mayor o igual a 2).");
                    return true;
                }

                Region region = null;
                try {
                    region = getSelection(p);
                } catch (IncompleteRegionException e) {
                    p.sendMessage(wePrefix + "Selecciona un área primero.");
                    return true;
                }

                PoissonDiskSampling sampling = new PoissonDiskSampling(radius + 1, region, p, trees);
                try {
                    sampling.generate();
                } catch (MaxChangedBlocksException e) {
                    p.sendMessage(wePrefix + "Límite de bloques alcanzado.");
                    return true;
                }


                p.sendMessage(wePrefix + "Árboles generados.");
            }
        }
        return true;
    }
}
