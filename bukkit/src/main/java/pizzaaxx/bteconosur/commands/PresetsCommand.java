package pizzaaxx.bteconosur.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.ServerPlayer.DataManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

public class PresetsCommand implements CommandExecutor, Listener {
    public static String presetsPrefix = "§f[§3PRESETS§f] §7>>§r ";

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
        }

        Player p = (Player) sender;
        final ServerPlayer s = new ServerPlayer(p);
        final DataManager data = s.getDataManager();

        if (args.length > 0) {
                if (args[0].equals("set")) {
                    if (args.length > 1) {
                        if (args[1].matches("[a-zA-Z0-9_]{1,32}")) {
                            if (args.length > 2) {
                                String value = StringUtils.join(args, " ", 2, args.length);

                                data.set("presets." + args[1], value);
                                data.save();

                                p.sendMessage(presetsPrefix + "Has establecido el §opreset§a " + args[1] + "§f con valor §a" + value + "§f.");
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

                            if (data.contains("presets." + args[1])) {
                                data.set("presets." + args[1], null);
                                data.save();

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
                    if (data.contains("presets")) {
                        p.sendMessage(">+-----------+[-< §3PRESETS §f>-]+-----------+<");

                        int i = 1;
                        for (String preset : data.getConfigurationSection("presets").getKeys(false)) {
                            p.sendMessage("§7" + i + ". §a" + preset + "§7 - §f" + data.getString("presets." + preset));
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

    @EventHandler
    public void onCommand(@NotNull PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();
        if (message.contains("$")) {
            DataManager data = new ServerPlayer(e.getPlayer()).getDataManager();
            ConfigurationSection presets = data.getConfigurationSection("presets");
            for (String word : message.split(" ")) {
                if (word.startsWith("$")) {
                    String preset = word.replace("$", "");
                    if (preset.matches("[a-zA-z0-9_]{1,32}") && presets.contains(preset)) {
                        message = message.replace(word, presets.getString(preset));
                    }
                }
            }
        }
        e.setMessage(message);
    }
}
