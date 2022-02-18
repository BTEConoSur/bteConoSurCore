package pizzaaxx.bteconosur.chats;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.yaml.YamlManager;
import xyz.upperlevel.spigot.book.BookUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.chats.Command.chatsPrefix;

public class Events implements Listener, EventListener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        ServerPlayer s = new ServerPlayer(e.getPlayer());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255,0,0));
        embed.setAuthor("" + s.getName() + " ha salido del servidor.", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png");
        gateway.sendMessageEmbeds(embed.build()).queue();
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent e) {
        if (!(e.getPlayer().hasPlayedBefore())) {
            ServerPlayer s = new ServerPlayer(e.getPlayer());
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0, 255, 42));
            embed.setAuthor("¡" + s.getName() + " ha entrado al servidor por primera vez!", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png");
            gateway.sendMessageEmbeds(embed.build()).queue();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ServerPlayer s = new ServerPlayer(e.getPlayer());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0, 255, 42));
        embed.setAuthor(s.getName() + " ha entrado al servidor.", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png");
        gateway.sendMessageEmbeds(embed.build()).queue();
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        ServerPlayer s = new ServerPlayer(e.getPlayer());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(252, 127, 3));
        embed.setAuthor(s.getName() + " ha sido expulsado del servidor.", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png");
        embed.addField(":scroll: Razón:", ChatColor.stripColor(e.getReason()), false);
        gateway.sendMessageEmbeds(embed.build()).queue();
    }

    @EventHandler
    public void onBan(PlayerQuitEvent e) {
        if (e.getPlayer().isBanned()) {
            ServerPlayer s = new ServerPlayer(e.getPlayer());
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(173, 38, 31));
            embed.setAuthor(s.getName() + " ha sido baneado del servidor.", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png");
            gateway.sendMessageEmbeds(embed.build()).queue();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Player p = e.getPlayer();
        ServerPlayer s = new ServerPlayer(p);

        if (!(s.isChatHidden())) {
            List<String> targetChats = new ArrayList<>();
            List<Player> pingedPlayers = new ArrayList<>();

            targetChats.add(s.getChat().getName());

            String message = ChatColor.stripColor(e.getMessage());

            for (String word : e.getMessage().split(" ")) {
                if (word.startsWith("@")) {
                    if (Bukkit.getOfflinePlayer(word.replace("@", "")).isOnline()) {
                        Player target = Bukkit.getPlayer(word.replace("@", ""));

                        if (!(pingedPlayers.contains(target))) {
                            pingedPlayers.add(target);
                        }

                        if (!(targetChats.contains(new ServerPlayer(target).getChat().getName()))) {
                            targetChats.add(new ServerPlayer(target).getChat().getName());
                        }

                        message = message.replace(word, "§a~" + s.getChatManager().getDisplayName() + "~");
                    }
                }
            }

            for (String name : targetChats) {
                Chat chat = new Chat(name);

                Country country = null;

                if (name.startsWith("project_")) {
                    try {
                        Project project = new Project(name.replace("project_", ""));

                        country = new Country(project.getOldCountry());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else if (!name.equals("global")) {
                    country = new Country(name);
                }

                String bRankPrefix = "";
                if (country != null && !country.getCountry().equals("argentina")) {
                    if (s.getBuilderRank(country) != null) {
                        String bRank = s.getBuilderRank(country);
                        if (bRank.equals("maestro")) {
                            bRankPrefix = "§f [§6MAESTRO§f]";
                        }
                        if (bRank.equals("veterano")) {
                            bRankPrefix = "§f [§eVETERANO§f]";
                        }
                        if (bRank.equals("avanzado")) {
                            bRankPrefix = "§f [§1AVANZADO§f]";
                        }
                    }
                }

                String finalMessage = String.join(" ", s.getPrefixes()) + bRankPrefix + "§f <" + s.getChatManager().getDisplayName() + "§f> " + message.replace("~", "");


                if (chat.getName().equals("global")) {
                    List<String> strings = new ArrayList<>();
                    strings.add(":speech_balloon: **");

                    strings.add("[" + YamlManager.getYamlData(pluginFolder, "discord/groupEmojis.yml").get(s.getPrimaryGroup()) + "] ");

                    for (String group : s.getSecondaryGroups()) {
                        strings.add("[" + YamlManager.getYamlData(pluginFolder, "discord/groupEmojis.yml").get(group) + "] ");

                    }
                    strings.add(s.getName() + ":** " + ChatColor.stripColor(message.replace("~", "**")));

                    gateway.sendMessage(String.join("", strings)).queue();
                }

                chat.sendMessage(finalMessage);
            }

            for (Player player : pingedPlayers) {
                if (new ServerPlayer(player).isChatHidden()) {
                    p.sendMessage(BookUtil.TextBuilder.of(chatsPrefix + "§a" + new ServerPlayer(player).getName() + "tiene el chat oculto. ").build(),
                            BookUtil.TextBuilder.of("§a[ENVIAR POR PRIVADO]")
                                    .onHover(BookUtil.HoverAction.showText("Haz click para enviar el menaje por privado."))
                                    .onClick(BookUtil.ClickAction.runCommand("/msg " + new ServerPlayer(player).getName() + ChatColor.stripColor(e.getMessage())))
                                    .build());
                    continue;
                }

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_XYLOPHONE, 1, 1);
            }
        } else {
            p.sendMessage(chatsPrefix + "No puedes hablar mientras tienes el chat oculto.");
        }
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getTextChannel() == gateway) {
                if (!(e.getAuthor().isBot()) && !(e.getMessage().getContentDisplay().startsWith("/"))) {
                    ChatColor color = ChatColor.getByChar((String) YamlManager.getYamlData(pluginFolder, "discord/chatColors.yml").get(e.getMember().getRoles().get(0).getId()));

                    new Chat("global").sendMessage("[§bDISCORD§f] §7>> §r" + color + e.getMember().getEffectiveName() + ": §r" + e.getMessage().getContentDisplay());
                }
            }
        }
    }
}
