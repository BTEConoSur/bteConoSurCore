package pizzaaxx.bteconosur.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.server.player.DataManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class PWarpCommand implements CommandExecutor {
    public static String pWarpPrefix = "§f[§6PWARP§f] §7>>§r ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                DataManager data = new ServerPlayer(p).getDataManager();
                if (args.length > 0) {
                    if (args[0].equals("set")) {
                        if (args.length > 1 && args[1].matches("[a-zA-Z0-9_]{1,32}") && !(args[1].equals("set")) && !(args[1].equals("delete")) && !(args[1].equals("list"))) {
                            Location loc = p.getLocation();
                            data.set("pwarps." + args[1] + ".x", loc.getBlockX());
                            data.set("pwarps." + args[1] + ".y", loc.getBlockY());
                            data.set("pwarps." + args[1] + ".z", loc.getBlockZ());
                            data.save();

                            p.sendMessage(pWarpPrefix + "Has establecido el warp personal §a" + args[1] + "§f en las coordenadas §a" + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ() + "§f.");
                        } else {
                            p.sendMessage(pWarpPrefix + "Introduce un nombre válido.");
                        }
                    } else if (args[0].equals("delete")) {
                        if (args.length > 1 && args[1].matches("[a-zA-Z0-9_]{1,32}") && !(args[1].equals("set")) && !(args[1].equals("delete")) && !(args[1].equals("list"))) {

                            data.set("pwarps." + args[1], null);
                            p.sendMessage(pWarpPrefix + "Has eliminado el warp personal §a" + args[1] + "§f.");

                        } else {
                            p.sendMessage(pWarpPrefix + "Introduce un nombre válido.");
                        }

                    } else if (args[0].equals("list")) {
                        if (data.contains("pwarps")) {
                            p.sendMessage(">+-----------+[-< §6PWARPS §f>-]+-----------+<");

                            int i = 1;
                            ConfigurationSection pwarps = data.getConfigurationSection("pwarps");
                            for (String pwarp : pwarps.getKeys(false)) {
                                p.sendMessage("§7" + i + ". §a" + pwarp + "§7 - §f" + pwarps.getInt(pwarp + ".x") + " " + pwarps.getInt(pwarp + ".y") + " " + pwarps.getInt(pwarp + ".z"));
                                i++;
                            }

                            p.sendMessage(">+-----------+[-< ====== >-]+-----------+<");
                        } else {
                            p.sendMessage(pWarpPrefix + "No tienes warps personales.");
                        }
                    } else {
                        if (args[0].matches("[a-zA-Z0-9_]{1,32}")) {
                            if (data.contains("pwarps")) {
                                ConfigurationSection pwarps = data.getConfigurationSection("pwarps");

                                if (pwarps.contains(args[0])) {

                                    int x = pwarps.getInt(args[0] + ".x");
                                    int y = pwarps.getInt(args[0] + ".x");
                                    int z = pwarps.getInt(args[0] + ".x");

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
        return true;
    }
}
