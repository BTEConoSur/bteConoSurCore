package pizzaaxx.bteconosur.presets;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.player.data.PlayerData;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.util.HashMap;
import java.util.Map;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;

public class PresetsCommand implements CommandExecutor {
    public static String presetsPrefix = "§f[§3PRESETS§f] §7>>§r ";

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Solo jugadores.");
            }

            Player p = (Player) sender;
            ServerPlayer s = new ServerPlayer(p);
            PlayerData playerData = new PlayerData(p);

            if (args.length > 0) {
                if (args[0].equals("set")) {
                    if (args.length > 1) {
                        if (args[1].matches("[a-zA-Z0-9_]{1,32}")) {
                            if (args.length > 2) {
                                String value = StringUtils.join(args, " ", 2, args.length);

                                YamlManager pData = new YamlManager(pluginFolder, "playerData/" + p.getUniqueId().toString() + ".yml");

                                Map<String, String> presets = new HashMap<>();
                                if (pData.getValue("presets") != null) {
                                    presets = (Map<String, String>) pData.getValue("presets");
                                }

                                presets.put(args[1], value);
                                pData.setValue("presets", presets);
                                pData.write();

                                p.sendMessage(presetsPrefix + "Has creado el §opreset§a " + args[1] + "§f con valor §a" + value + "§f.");
                            } else {
                                p.sendMessage(presetsPrefix + "Introuduce un valor para el §opreset§f.");
                            }
                        } else {
                            p.sendMessage(presetsPrefix + "Introduce un nombre válido.");
                            return true;
                        }
                    } else {
                        p.sendMessage(presetsPrefix + "Introduce un nombre para el §opreset§f.");
                    }
                } else if (args[0].equals("delete")) {
                    if (args.length > 1) {
                        if (args[1].matches("[a-zA-Z0-9_]{1,32}")) {
                            YamlManager pData = new YamlManager(pluginFolder, "playerData/" + p.getUniqueId().toString() + ".yml");

                            if (pData.getValue("presets") != null) {
                                Map<String, String> presets = (Map<String, String>) pData.getValue("presets");
                                presets.remove(args[1]);
                                if (presets.size() > 0) {
                                    pData.setValue("presets", presets);
                                } else {
                                    pData.deleteValue("presets");
                                }
                                pData.write();

                                p.sendMessage(presetsPrefix + "Has eliminado el §opreset§a " + args[1] + "§f.");
                            } else {
                                p.sendMessage(presetsPrefix + "El §opreset§f introducido no existe.");
                            }
                        } else {
                            p.sendMessage(presetsPrefix + "Introduce un nombre válido.");
                            return true;
                        }
                    } else {
                        p.sendMessage(presetsPrefix + "Introduce el nombre del §opreset§f que quieres eliminar.");
                    }
                } else if (args[0].equals("list")) {
                    YamlManager pData = new YamlManager(pluginFolder, "playerData/" + p.getUniqueId().toString() + ".yml");

                    if (pData.getAllData().get("presets") != null) {
                        p.sendMessage(">+-----------+[-< §3PRESETS §f>-]+-----------+<");

                        Map<String, String> presets = (Map<String, String>) pData.getAllData().get("presets");

                        int i = 1;
                        for (Map.Entry<String, String> entry : presets.entrySet()) {
                            p.sendMessage("§7" + i + ". §a" + entry.getKey() + "§7 - §f" + entry.getValue());
                            i++;
                        }

                        p.sendMessage(">+-----------+[-< ======= >-]+-----------+<");
                    } else {
                        p.sendMessage(presetsPrefix + "No tienes ningún §opreset§f.");
                    }
                } else {
                    p.sendMessage(presetsPrefix + "Introduce un subcomando válido.");
                }
            } else {
                p.sendMessage(presetsPrefix + "Introduce un subcomando.");
            }

        return true;
    }
}
