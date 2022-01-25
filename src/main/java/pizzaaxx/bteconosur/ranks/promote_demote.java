package pizzaaxx.bteconosur.ranks;

import net.dv8tion.jda.api.EmbedBuilder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.notifications.Notification;

import java.awt.*;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.discord.bot.conoSurBot;
import static pizzaaxx.bteconosur.ranks.main.primaryGroupsList;

public class promote_demote implements CommandExecutor {
    public String promotePrefix = "§f[§2PROMOTE§f] §7>>§r ";
    public String demotePrefix = "§f[§4DEMOTE§f] §7>>§r ";

    public static LuckPerms lp;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
        }

        Player p = (Player) sender;

        if (command.getName().equals("promote")) {
            if (args.length > 0) {
                if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    ServerPlayer s = new ServerPlayer(target);
                    if (target != p) {
                        if (primaryGroupsList.indexOf(new ServerPlayer(target).getPrimaryGroup()) + 1 < primaryGroupsList.indexOf(new ServerPlayer(p).getPrimaryGroup())) {
                            if (primaryGroupsList.indexOf(new ServerPlayer(target).getPrimaryGroup()) != primaryGroupsList.size()-1) {
                                s.promote();

                                p.sendMessage(promotePrefix + "Has promovido a §a" + new ServerPlayer(target).getName() + "§f a §a" + primaryGroupsList.get(primaryGroupsList.indexOf(new ServerPlayer(target).getPrimaryGroup()) + 1).replace("default", "visita").toUpperCase() + "§f.");
                                s.sendNotification("Has sido promovid@ a §a" + primaryGroupsList.get(primaryGroupsList.indexOf(new ServerPlayer(target).getPrimaryGroup()) + 1).replace("default", "visita").toUpperCase() + "§f.");

                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setColor(new Color(0, 255, 42));
                                embed.setAuthor(new ServerPlayer(target).getName() + " ha sido promovid@ a " + primaryGroupsList.get(primaryGroupsList.indexOf(new ServerPlayer(target).getPrimaryGroup()) + 1).replace("default", "visita").toUpperCase(), "https://cravatar.eu/helmavatar/%{_p}%/190.png");

                                gateway.sendMessageEmbeds(embed.build()).queue();
                            } else {
                                p.sendMessage(promotePrefix + "No puedes promover a este jugador.");
                            }
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
                    if (target != p) {
                        if (primaryGroupsList.indexOf(s.getPrimaryGroup()) < primaryGroupsList.indexOf(s.getPrimaryGroup())) {
                            if (primaryGroupsList.indexOf(s.getPrimaryGroup()) != 0) {
                                s.demote();

                                p.sendMessage(demotePrefix + "Has degradado a §a" + s.getName() + "§f a §a" + primaryGroupsList.get(primaryGroupsList.indexOf(s.getPrimaryGroup()) - 1).replace("default", "visita").toUpperCase() + "§f.");
                                s.sendNotification("Has sido degradad@ a §a" + primaryGroupsList.get(primaryGroupsList.indexOf(s.getPrimaryGroup()) - 1).replace("default", "visita").toUpperCase() + "§f.");

                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setColor(new Color(255, 0, 0));
                                embed.setAuthor(new ServerPlayer(target).getName() + " ha sido degradad@ a " + primaryGroupsList.get(primaryGroupsList.indexOf(new ServerPlayer(target).getPrimaryGroup()) + 1).replace("default", "visita").toUpperCase(), "https://cravatar.eu/helmavatar/%{_p}%/190.png");

                                gateway.sendMessageEmbeds(embed.build()).queue();
                            } else {
                                p.sendMessage(demotePrefix + "No puedes degradar a este jugador.");
                            }
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
