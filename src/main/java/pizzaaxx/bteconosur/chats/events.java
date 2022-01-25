package pizzaaxx.bteconosur.chats;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.yaml.YamlManager;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.ArrayList;
import java.util.List;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.chats.command.chatsPrefix;

public class events implements Listener, EventListener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Player p = e.getPlayer();
        ServerPlayer s = new ServerPlayer(p);

        if (!(s.isChatHidden())) {
            List<Chat> targetChats = new ArrayList<>();
            List<Player> pingedPlayers = new ArrayList<>();

            targetChats.add(s.getChat());

            String message = ChatColor.stripColor(e.getMessage());

            for (String word : e.getMessage().split(" ")) {
                if (word.startsWith("@")) {
                    if (Bukkit.getOfflinePlayer(word.replace("@", "")).isOnline()) {
                        Player target = Bukkit.getPlayer(word.replace("@", ""));

                        if (!(pingedPlayers.contains(target))) {
                            pingedPlayers.add(target);
                        }

                        if (!(targetChats.contains(new ServerPlayer(target).getChat()))) {
                            targetChats.add(new ServerPlayer(target).getChat());
                        }

                        message = message.replace(word, "§a~" + s.getDisplayName() + "~");
                    }
                }
            }

            String finalMessage = String.join(" ", s.getPrefixes()) + "§f <" + s.getDisplayName() + "§f> " + message.replace("~", "");

            for (Chat chat : targetChats) {
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
