package pizzaaxx.bteconosur.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.ServerPlayer;

public class NickNameCommand implements CommandExecutor {
    public static String nickPrefix = "§f[§9NICK§f] §7>>§r ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        ServerPlayer s = new ServerPlayer(p);

            if (args.length > 0) {
                if (Bukkit.getOfflinePlayer(args[0]).isOnline() && Bukkit.getPlayer(args[0]) != p) {
                    if (p.hasPermission("bteconosur.nickname.others")) {
                        Player target = Bukkit.getPlayer(args[0]);
                        ServerPlayer t = new ServerPlayer(target);

                        if (args.length > 1) {
                            if (args[1].matches("[a-zA-Z0-9_]{1,16}")) {
                                if (!(new ServerPlayer(target).getName().equalsIgnoreCase(args[1]))) {
                                    t.setNickname(args[1]);
                                    p.sendMessage(nickPrefix + "Has establecido el apodo de §a" + new ServerPlayer(target).getName() + "§f en §a" + args[1] + "§f.");
                                } else {
                                    t.setNickname(null);
                                    p.sendMessage(nickPrefix + "Has reeestablecido el apodo de §a" + new ServerPlayer(target).getName() + "§f.");
                                }
                            } else {
                                p.sendMessage(nickPrefix + "Introduce un apodo válido.");
                            }
                        } else {
                            p.sendMessage(nickPrefix + "Introduce un apodo para §a" + new ServerPlayer(target).getName() + "§f.");
                        }
                    } else {
                        p.sendMessage(nickPrefix + "No puedes hacer esto.");
                    }
                } else {
                    if ((!p.hasPermission("bteconsur.nickname.others") && args[0].matches("[a-zA-Z0-9_]{1,16}")) || (p.hasPermission("bteconsur.nickname.others") && args[0].matches("[a-zA-Z0-9_&]{1,16}"))) {
                        if (!(new ServerPlayer(p).getName().equalsIgnoreCase(args[0]))) {
                            s.setNickname(args[0]);
                            p.sendMessage(nickPrefix + "Has establecido tu apodo en §a" + args[0].replace("&", "§") + "§f.");
                        } else {
                            s.setNickname(null);
                            p.sendMessage(nickPrefix + "Has reeestablecido tu apodo.");
                        }
                    } else {
                        p.sendMessage(nickPrefix + "Introduce un apodo válido.");
                    }
                }
            } else {
                if (p.hasPermission("bteconosur.nickname.others")) {
                    p.sendMessage(nickPrefix + "Introduce un apodo o un jugador.");
                } else {
                    p.sendMessage(nickPrefix + "Introduce un apodo.");
                }
            }

        return true;
    }
}
