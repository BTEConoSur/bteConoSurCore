package pizzaaxx.bteconosur.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.chat.ChatManager;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class ChatHandler extends ListenerAdapter implements Listener {

    private final BTEConoSurPlugin plugin;
    private final LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();

    public ChatHandler(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(@NotNull AsyncChatEvent event) {
        event.setCancelled(true);

        Player p = event.getPlayer();
        OnlineServerPlayer onlineServerPlayer;
        try {
            onlineServerPlayer = plugin.getPlayerRegistry().get(p.getUniqueId()).asOnlinePlayer();
        } catch (SQLException | JsonProcessingException e) {
            p.sendMessage(PREFIX + "Ha ocurrido un error.");
            return;
        }

        ChatManager chatManager = onlineServerPlayer.getChatManager();

        if (chatManager.isHidden()) {
            p.sendMessage(PREFIX + "No puedes enviar mensajes con el chat oculto.");
            return;
        }

        String content = ((TextComponent) event.originalMessage()).content();
        List<Component> resultComponents = new ArrayList<>();
        Set<UUID> mentionedUUIDs = new HashSet<>();
        for (String word : content.split(" ")) {
            if (word.startsWith("@")) {
                String name = word.substring(1);
                Bukkit.getOnlinePlayers().stream()
                        .filter(player ->
                                (name.length() >= 3 && player.getName().toLowerCase().startsWith(name.toLowerCase())) || (levenshteinDistance.apply(name.toLowerCase(), player.getName().toLowerCase()) <= 3))
                        .findFirst()
                        .ifPresentOrElse(
                                player -> {
                                    mentionedUUIDs.add(player.getUniqueId());
                                    OfflineServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId());
                                    resultComponents.add(
                                            Component.text(player.getName(), GREEN)
                                                    .hoverEvent(
                                                            Component.join(
                                                                    JoinConfiguration.spaces(),
                                                                    s.getLoreWithTitle()
                                                            )
                                                    )
                                    );
                                },
                                () -> resultComponents.add(Component.text(word))
                        );
            } else {
                resultComponents.add(Component.text(word));
            }

            Set<String> channelsSent = new HashSet<>();
            Chat currentChat = chatManager.getCurrentChat();
            currentChat.sendMessage(
                    currentChat,
                    onlineServerPlayer,
                    Component.join(
                            JoinConfiguration.spaces(),
                            resultComponents
                    )
            );
            channelsSent.add(
                    currentChat.getProviderId() + "_" + currentChat.getChatId()
            );

            for (UUID targetID : mentionedUUIDs) {
                try {
                    OnlineServerPlayer target = plugin.getPlayerRegistry().get(targetID).asOnlinePlayer();
                    Chat targetChat = target.getChatManager().getCurrentChat();
                    if (channelsSent.add(targetChat.getProviderId() + "_" + targetChat.getChatId())) {
                        targetChat.sendMessage(
                                currentChat,
                                onlineServerPlayer,
                                Component.join(
                                        JoinConfiguration.spaces(),
                                        resultComponents
                                )
                        );
                    }
                    if (!target.getChatManager().isHidden()) {
                        Player player = target.getPlayer();
                        BukkitRunnable pitch1 = new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                            }
                        };
                        BukkitRunnable pitch2 = new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
                            }
                        };
                        BukkitRunnable pitch3 = new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                            }
                        };
                        BukkitRunnable pitch4 = new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
                            }
                        };
                        BukkitRunnable pitch5 = new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                            }
                        };
                        BukkitRunnable pitch6 = new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
                            }
                        };

                        pitch1.runTaskLaterAsynchronously(plugin, 2);
                        pitch2.runTaskLaterAsynchronously(plugin, 4);
                        pitch3.runTaskLaterAsynchronously(plugin, 6);
                        pitch4.runTaskLaterAsynchronously(plugin, 8);
                        pitch5.runTaskLaterAsynchronously(plugin, 10);
                        pitch6.runTaskLaterAsynchronously(plugin, 12);
                    } // PLAY SOUND
                } catch (SQLException | JsonProcessingException ignored) {}
            }
        }
    }

    public static Map<UUID, String> CHATS = new HashMap<>();

    public void addToChat(UUID uuid, @NotNull Chat chat) {
        chat.playerJoin(uuid);
        CHATS.put(uuid, chat.getProviderId() + "_" + chat.getChatId());
    }

    public void removeFromChat(UUID uuid, @NotNull Chat chat) {
        CHATS.remove(uuid, chat.getProviderId() + "_" + chat.getChatId());
        chat.playerLeave(uuid);
    }

    public void sendMessage(Chat chat, Component message) {
        for (Map.Entry<UUID, String> entry : CHATS.entrySet()) {
            if (entry.getValue().equals(chat.getProviderId() + "_" + chat.getChatId())) {
                try {
                    OnlineServerPlayer player = plugin.getPlayerRegistry().get(entry.getKey()).asOnlinePlayer();
                    if (player.getChatManager().isHidden()) {
                        continue;
                    }
                    player.getPlayer().sendMessage(message);
                } catch (SQLException | JsonProcessingException ignored) {}
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild()) {
            return;
        }

        Country country = plugin.getCountriesRegistry().getCountryByGuildId(event.getGuild().getId());

        if (country == null) {
            return;
        }

        if (!country.getChatID().equals(event.getChannel().getId())) {
            return;
        }

        if (event.getAuthor().isBot()) {
            return;
        }

        String content = event.getMessage().getContentDisplay();

        Component minecraftMessage = Component.text("[§bDISCORD§f] §8» §a" + event.getAuthor().getName() + "§f: ");
        StringBuilder discordMessage = new StringBuilder(":flag_" + country.getAbbreviation() + ": **" + event.getAuthor().getName() + "**: ");

        Pattern pattern = Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)");
        Matcher matcher = pattern.matcher(content);
        // if content has urls
        if (matcher.find()) {
            // in minecraft, add the content but replace the urls with a clickable message
            // in discord, add the content but replace urls with [link_to_message](url)
            // keep in mind multiple urls
            int start = 0;
            matcher.reset();
            while (matcher.find()) {
                String url = matcher.group();
                String before = content.substring(start, matcher.start());
                // add the content before the url
                minecraftMessage = minecraftMessage.append(
                        Component.text(before)
                );
                discordMessage.append(before);
                // update start
                start = matcher.end();

                // add the url
                minecraftMessage = minecraftMessage.append(
                        Component.text("enlace", GREEN)
                                .clickEvent(
                                        ClickEvent.openUrl(url)
                                )
                );
                discordMessage.append("[").append("enlace").append("](").append(event.getMessage().getJumpUrl()).append(")");
            }

            // if there is content after the last url
            if (start < content.length()) {
                String after = content.substring(start);
                minecraftMessage = minecraftMessage.append(
                        Component.text(after)
                );
                discordMessage.append(after);
            }
        } else {
            // if content has no urls
            minecraftMessage = minecraftMessage.append(
                    Component.text(content)
            );
            discordMessage.append(content);
        }

        // if message has attachments add clickable message in minecraft that links to the original message
        if (!event.getMessage().getAttachments().isEmpty()) {
            minecraftMessage = minecraftMessage.append(
                    Component.text(" [Ver " + event.getMessage().getAttachments().size() + " archivo(s) adjuntos(s)]", GREEN)
                            .clickEvent(
                                    ClickEvent.openUrl(event.getMessage().getJumpUrl())
                            )
            );
            discordMessage.append("\n\n[Ver ").append(event.getMessage().getAttachments().size()).append(" archivo(s) adjunto(s)](").append(event.getMessage().getJumpUrl()).append(")");
        }

        // send to minecraft
        plugin.getChatHandler().sendMessage(
                plugin,
                minecraftMessage
        );

        // send to other countries discord channel
        for (Country c : plugin.getCountriesRegistry().getCountries()) {
            if (c.equals(country)) {
                continue;
            }
            c.getChat().sendMessage(
                    discordMessage.toString()
            ).queue();
        }

    }
}
