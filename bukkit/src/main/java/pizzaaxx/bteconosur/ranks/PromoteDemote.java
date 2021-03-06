package pizzaaxx.bteconosur.ranks;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.server.player.GroupsManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

public class PromoteDemote implements CommandExecutor {
    public static String promotePrefix = "§f[§2PROMOTE§f] §7>>§r ";
    public static String demotePrefix = "§f[§4DEMOTE§f] §7>>§r ";

    public static LuckPerms lp;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
        }

        Player p = (Player) sender;
        GroupsManager pManager = new ServerPlayer(p).getGroupsManager();

        if (command.getName().equals("promote")) {
            if (args.length > 0) {
                if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    ServerPlayer s = new ServerPlayer(target);
                    GroupsManager tManager = s.getGroupsManager();
                    if (target != p) {
                        int targetPriority = tManager.getPrimaryGroup().getPriority();
                        if (targetPriority + 1 < pManager.getPrimaryGroup().getPriority() && targetPriority < GroupsManager.PrimaryGroup.ADMIN.getPriority()) {
                            tManager.promote();

                            p.sendMessage(promotePrefix + "Has promovido a §a" + s.getName() + "§f a §a" + tManager.getPrimaryGroup().toString().replace("default", "visita").toUpperCase() + "§f.");
                        } else {
                            p.sendMessage(promotePrefix + "No puedes promover a este jugador.");
                        }
                    } else {
                        p.sendMessage(promotePrefix + "No puedes promoverte a ti mismo.");
                    }
                } else {
                    p.sendMessage(promotePrefix + "El jugador no ha jugado antes en el servidor.");
                }
            } else {
                p.sendMessage(promotePrefix + "Introduce un jugador.");
            }
        }

        if (command.getName().equals("demote")) {
            if (args.length > 0) {
                if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    ServerPlayer s = new ServerPlayer(target);
                    GroupsManager manager = s.getGroupsManager();
                    if (target != p) {
                        int targetPriority = manager.getPrimaryGroup().getPriority();
                        if (pManager.getPrimaryGroup().getPriority() > targetPriority && targetPriority > 0) {
                            manager.demote();

                            p.sendMessage(demotePrefix + "Has degradado a §a" + s.getName() + "§f a §a" + manager.getPrimaryGroup().toString().replace("default", "visita").toUpperCase() + "§f.");
                        } else {
                            p.sendMessage(demotePrefix + "No puedes degradar a este jugador.");
                        }
                    } else {
                        p.sendMessage(demotePrefix + "No puedes degradarte a ti mismo.");
                    }
                } else {
                    p.sendMessage(demotePrefix + "El jugador no ha jugado antes en el servidor.");
                }
            } else {
                p.sendMessage(demotePrefix + "Introduce un jugador.");
            }
        }
        return true;
    }
}
