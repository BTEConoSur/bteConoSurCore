package pizzaaxx.bteconosur.teleport;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.playerData.PlayerData;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.util.HashMap;
import java.util.Map;

import static pizzaaxx.bteconosur.bteConoSur.mainWorld;

public class pWarp implements CommandExecutor {
    public static String pWarpPrefix = "§f[§6PWARP§f] §7>>§r ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("pwarp")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                PlayerData pData =  new PlayerData(p);
                if (args.length > 0) {
                    if (args[0].equals("set")) {
                        if (args.length > 1 && args[1].matches("[a-zA-Z0-9_]{1,32}") && !(args[1].equals("set")) && !(args[1].equals("delete")) && !(args[1].equals("list"))) {
                            Map<String, Map<String, Integer>> pwarps;
                            if (pData.getData("pwarps") != null) {
                                pwarps = (Map<String, Map<String, Integer>>) pData.getData("pwarps");
                            } else {
                                pwarps = new HashMap<>();
                            }

                            Map<String, Integer> coordinates = new HashMap<>();

                            coordinates.put("x", p.getLocation().getBlockX());
                            coordinates.put("y", p.getLocation().getBlockY());
                            coordinates.put("z", p.getLocation().getBlockZ());

                            pwarps.put(args[1], coordinates);

                            pData.setData("pwarps", pwarps);
                            pData.save();

                            p.sendMessage(pWarpPrefix + "Has establecido el warp personal §a" + args[1] + "§f en las coordenadas §a" + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ() + "§f.");
                        } else {
                            p.sendMessage(pWarpPrefix + "Introduce un nombre válido.");
                        }
                    } else if (args[0].equals("delete")) {
                        if (args.length > 1 && args[1].matches("[a-zA-Z0-9_]{1,32}") && !(args[1].equals("set")) && !(args[1].equals("delete")) && !(args[1].equals("list"))) {

                            if (pData.getData("pwarps") != null) {
                                Map<String, Map<String, Integer>> pwarps = (Map<String, Map<String, Integer>>) pData.getData("pwarps");

                                pwarps.remove(args[1]);

                                if (pwarps.size() > 0) {
                                    pData.setData("pwarps", pwarps);
                                } else {
                                    pData.deleteData("pwarps");
                                }

                                pData.save();

                                p.sendMessage(pWarpPrefix + "Has eliminado el warp personal §a" + args[1] + "§f.");
                            }
                        } else {
                            p.sendMessage(pWarpPrefix + "Introduce un nombre válido.");
                        }
                    } else if (args[0].equals("list")) {
                        if (pData.getData("pwarps") != null) {
                            p.sendMessage(">+-----------+[-< §6PWARPS §f>-]+-----------+<");

                            int i = 1;
                            for (Map.Entry<String, Map<String, Integer>> entry : ((Map<String, Map<String, Integer>>) pData.getData("pwarps")).entrySet()) {
                                p.sendMessage("§7" + i + ". §a" + entry.getKey() + "§7 - §f" + entry.getValue().get("x") + " " + entry.getValue().get("y") + " " + entry.getValue().get("z"));
                                i++;
                            }

                            p.sendMessage(">+-----------+[-< ====== >-]+-----------+<");
                        } else {
                            p.sendMessage(pWarpPrefix + "No tienes warps personales.");
                        }
                    } else {
                        if (args[0].matches("[a-zA-Z0-9_]{1,32}")) {
                            if (pData.getData("pwarps") != null) {
                                Map<String, Map<String, Integer>> pwarps = (Map<String, Map<String, Integer>>) pData.getData("pwarps");

                                if (pwarps.containsKey(args[0])) {
                                    Map<String, Integer> coords = pwarps.get(args[0]);

                                    int x = coords.get("x");
                                    int y = coords.get("y");
                                    int z = coords.get("z");

                                    p.teleport(new Location(mainWorld, x, y, z));

                                    p.sendMessage(pWarpPrefix + "Teletransportándote al warp personal §a" + args[0] + "§f.");
                                } else {
                                    p.sendMessage(pWarpPrefix + "El warp personal introducido no existe.");
                                }
                            } else {
                                p.sendMessage(pWarpPrefix + "El warp personal introducido no existe.");
                            }
                        } else {
                            p.sendMessage(pWarpPrefix + "Introduce un nombre válido.");
                        }
                    }
                }
            }
        }
        return true;
    }
}
