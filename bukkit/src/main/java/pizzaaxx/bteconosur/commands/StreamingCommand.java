package pizzaaxx.bteconosur.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import xyz.upperlevel.spigot.book.BookUtil;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.ranks.Streamer.streamerPrefix;

public class StreamingCommand implements CommandExecutor {

    private final BteConoSur plugin;

    public StreamingCommand(BteConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            if (sender instanceof Player) {
                Player p = (Player) sender;
                ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());
                if (args.length > 0) {
                    try {
                        URL url = new URL(args[0]);

                        EmbedBuilder embed = new EmbedBuilder();
                        if (args[0].contains("youtu")) {
                            embed.addField("¡" + s.getName() + " está en directo en YOUTUBE!", args[0], false);
                            embed.setColor(new Color(255, 0, 0));
                            embed.setThumbnail("https://cdn.discordapp.com/attachments/807694452214333482/846362783959744562/youtube-logo-5-2.png");

                            plugin.broadcast(">+----------------+[-< §aSTREAMING§f >-]+----------------+<");
                            plugin.broadcast(" ");

                            plugin.broadcast("¡§a" + s.getName() + "§f está en directo en §4YOUTUBE§f!");
                            plugin.broadcast(BookUtil.TextBuilder.of(args[0])
                                    .color(ChatColor.AQUA)
                                    .style(ChatColor.UNDERLINE)
                                    .onHover(BookUtil.HoverAction.showText("Click para abrir."))
                                    .onClick(BookUtil.ClickAction.openUrl(args[0]))
                                    .build());

                            plugin.broadcast(" ");
                            plugin.broadcast(">+----------------+[-< ========= >-]+----------------+<");

                        } else if (args[0].contains("twitch")) {
                            embed.addField("¡" + s.getName() + " está en directo en TWITCH!", args[0], false);
                            embed.setColor(new Color(147, 32, 223));
                            embed.setThumbnail("https://cdn.discordapp.com/attachments/807694452214333482/846365871696773140/tw.png");

                            plugin.broadcast(">+----------------+[-< §aSTREAMING§f >-]+----------------+<");
                            plugin.broadcast(" ");

                            plugin.broadcast("¡§a" + s.getName() + "§f está en directo en §5TWITCH§f!");
                            plugin.broadcast(BookUtil.TextBuilder.of(args[0])
                                    .color(ChatColor.AQUA)
                                    .style(ChatColor.UNDERLINE)
                                    .onHover(BookUtil.HoverAction.showText("Click para abrir."))
                                    .onClick(BookUtil.ClickAction.openUrl(args[0]))
                                    .build());

                            plugin.broadcast(" ");
                            plugin.broadcast(">+----------------+[-< ========= >-]+----------------+<");

                        } else if (args[0].contains("facebook")) {
                            embed.addField("¡" + s.getName() + " está en directo en FACEBOOK!", args[0], false);
                            embed.setColor(new Color(0, 120, 255));
                            embed.setThumbnail("https://cdn.discordapp.com/attachments/807694452214333482/846363420869001266/fbg.png");

                            plugin.broadcast(">+----------------+[-< §aSTREAMING§f >-]+----------------+<");
                            plugin.broadcast(" ");

                            plugin.broadcast("¡§a" + s.getName() + "§f está en directo en §9FACEBOOK§f!");
                            plugin.broadcast(BookUtil.TextBuilder.of(args[0])
                                    .color(ChatColor.AQUA)
                                    .style(ChatColor.UNDERLINE)
                                    .onHover(BookUtil.HoverAction.showText("Click para abrir."))
                                    .onClick(BookUtil.ClickAction.openUrl(args[0]))
                                    .build());

                            plugin.broadcast(" ");
                            plugin.broadcast(">+----------------+[-< ========= >-]+----------------+<");

                        }
                        gateway.sendMessageEmbeds(embed.build()).queue();
                    } catch (MalformedURLException e) {
                        sender.sendMessage(streamerPrefix + "Introduce un enlace válido.");
                    }
                } else {
                    sender.sendMessage(streamerPrefix + "Introduce un enlace.");
                }
            }
        return true;
    }
}
