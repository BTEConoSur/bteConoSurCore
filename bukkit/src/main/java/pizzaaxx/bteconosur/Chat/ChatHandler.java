package pizzaaxx.bteconosur.Chat;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.Managers.ChatManager;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatHandler implements Listener, Prefixable {

    private final BTEConoSur plugin;
    private final Map<String, Chat> chats = new HashMap<>();


    public ChatHandler(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public boolean isLoaded(String name) {
        return chats.containsKey(name);
    }

    public void registerChat(Chat chat) {
        chats.put(chat.getID(), chat);
    }

    public void tryUnregister(@NotNull Chat chat) {
        if (chat.isUnloadable()) {
            if (chat.getPlayers().isEmpty()) {
                chats.remove(chat.getID());
            }
        }
    }

    public Chat getChat(String name) {
        return chats.get(name);
    }

    @EventHandler
    public void onMessage(@NotNull AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();

        ChatManager chatManager = plugin.getPlayerRegistry().get(player.getUniqueId()).getChatManager();

        if (chatManager.isHidden()) {
            player.sendMessage(getPrefix() + "No puedes hablar mientras tienes el chat oculto.");
            return;
        }

        String message = event.getMessage();

        Pattern pattern = Pattern.compile("@[a-zA-Z0-9_]{1,32}");
        Matcher matcher = pattern.matcher(message);

        Chat originChat;
        try {
            originChat = plugin.getPlayerRegistry().get(player.getUniqueId()).getChatManager().getCurrentChat();
        } catch (SQLException e) {
            plugin.warn("Problem with Chat Manager: " + player.getUniqueId());
            player.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
            return;
        }

        Set<Chat> targetChats = new HashSet<>();
        Set<UUID> targetPlayers = new HashSet<>();

        while (matcher.find()) {
            String match = matcher.group();
            String partialName = match.replace("@", "");
            Player target = plugin.getOnlinePlayer(partialName);
            if (target != null) {
                try {
                    ChatManager targetChatManager = plugin.getPlayerRegistry().get(target.getUniqueId()).getChatManager();
                    if (!targetChatManager.isHidden()) {
                        message = matcher.replaceAll("§a" + target.getName());
                        targetChats.add(targetChatManager.getCurrentChat());
                        targetPlayers.add(target.getUniqueId());
                    } else {
                        message = matcher.replaceAll("§7" + target.getName());
                    }
                } catch (SQLException e) {
                    plugin.warn("Problem with Chat Manager: " + target.getUniqueId());
                }
            }
        }

        originChat.sendMessage(player.getUniqueId(), message);
        for (Chat chat : targetChats) {
            if (chat != originChat) {
                chat.sendMessageFromOther(originChat, player.getUniqueId(), message);
            }
        }

        for (UUID uuid : targetPlayers) {
            Player target = Bukkit.getPlayer(uuid);
            BukkitRunnable pitch1 = new BukkitRunnable() {
                @Override
                public void run() {
                    target.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 1);
                }
            };
            BukkitRunnable pitch2 = new BukkitRunnable() {
                @Override
                public void run() {
                    target.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 2);
                }
            };
            BukkitRunnable pitch3 = new BukkitRunnable() {
                @Override
                public void run() {
                    target.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 1);
                }
            };
            BukkitRunnable pitch4 = new BukkitRunnable() {
                @Override
                public void run() {
                    target.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 2);
                }
            };
            BukkitRunnable pitch5 = new BukkitRunnable() {
                @Override
                public void run() {
                    target.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 1);
                }
            };
            BukkitRunnable pitch6 = new BukkitRunnable() {
                @Override
                public void run() {
                    target.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 2);
                }
            };

            pitch1.runTaskLaterAsynchronously(plugin, 2);
            pitch2.runTaskLaterAsynchronously(plugin, 4);
            pitch3.runTaskLaterAsynchronously(plugin, 6);
            pitch4.runTaskLaterAsynchronously(plugin, 8);
            pitch5.runTaskLaterAsynchronously(plugin, 10);
            pitch6.runTaskLaterAsynchronously(plugin, 12);
        }
    }

    @Override
    public String getPrefix() {
        return "§f[§aCHAT§f] §7>> §f";
    }
}
