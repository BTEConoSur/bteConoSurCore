package pizzaaxx.bteconosur.Chat;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.HelpMethods.ColorHelper;
import pizzaaxx.bteconosur.ServerPlayer.ChatManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

import java.awt.*;
import java.util.List;
import java.util.*;

import static pizzaaxx.bteconosur.Chat.IChat.CHAT_PREFIX;
import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.country.cities.projects.Command.ProjectsCommand.tutorialSteps;

public class ChatEventsListener extends ListenerAdapter implements Listener {

    private final BteConoSur plugin;
    private final Map<Color, ChatColor> chatColorMapping = new HashMap<>();

    public ChatEventsListener(BteConoSur plugin) {
        this.plugin = plugin;

        chatColorMapping.put(new Color(0,0,0), ChatColor.BLACK);
        chatColorMapping.put(new Color(0,0,170), ChatColor.DARK_BLUE);
        chatColorMapping.put(new Color(0,170,0), ChatColor.DARK_GREEN);
        chatColorMapping.put(new Color(0,170,170), ChatColor.DARK_AQUA);
        chatColorMapping.put(new Color(170,0,0), ChatColor.DARK_RED);
        chatColorMapping.put(new Color(170,0,170), ChatColor.DARK_PURPLE);
        chatColorMapping.put(new Color(255,170,0), ChatColor.GOLD);
        chatColorMapping.put(new Color(170,170,170), ChatColor.GRAY);
        chatColorMapping.put(new Color(85,85,85), ChatColor.DARK_GRAY);
        chatColorMapping.put(new Color(85,85,255), ChatColor.BLUE);
        chatColorMapping.put(new Color(85,255,85), ChatColor.GREEN);
        chatColorMapping.put(new Color(85,255,255), ChatColor.AQUA);
        chatColorMapping.put(new Color(255,85,85), ChatColor.RED);
        chatColorMapping.put(new Color(255,85,255), ChatColor.LIGHT_PURPLE);
        chatColorMapping.put(new Color(255,255,85), ChatColor.YELLOW);
        chatColorMapping.put(new Color(255,255,255), ChatColor.WHITE);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        ServerPlayer s = plugin.getPlayerRegistry().get(e.getPlayer().getUniqueId());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255,0,0));
        embed.setAuthor("" + s.getName() + " ha salido del servidor.", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png");
        gateway.sendMessageEmbeds(embed.build()).queue();
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        ServerPlayer s = plugin.getPlayerRegistry().get(e.getPlayer().getUniqueId());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0, 255, 42));
        if (!(e.getPlayer().hasPlayedBefore())) {
            embed.setAuthor("¡" + s.getName() + " ha entrado al servidor por primera vez!", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png");
        } else {
            embed.setAuthor(s.getName() + " ha entrado al servidor.", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png");
        }
        gateway.sendMessageEmbeds(embed.build()).queue();
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent e) {
        ServerPlayer s = plugin.getPlayerRegistry().get(e.getPlayer().getUniqueId());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(252, 127, 3));
        embed.setAuthor(s.getName() + " ha sido expulsado del servidor.", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png");
        embed.addField(":scroll: Razón:", ChatColor.stripColor(e.getReason()), false);
        gateway.sendMessageEmbeds(embed.build()).queue();
    }

    @EventHandler
    public void onBan(@NotNull PlayerQuitEvent e) {
        if (e.getPlayer().isBanned()) {
            ServerPlayer s = plugin.getPlayerRegistry().get(e.getPlayer().getUniqueId());
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(173, 38, 31));
            embed.setAuthor(s.getName() + " ha sido baneado del servidor.", null, "https://cravatar.eu/helmavatar/" + s.getName() + "/190.png");
            gateway.sendMessageEmbeds(embed.build()).queue();
        }
    }

    @EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Player p = e.getPlayer();
        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());
        ChatManager sChatManager = s.getChatManager();

        if (tutorialSteps.containsKey(p.getUniqueId())) {
            p.sendMessage(CHAT_PREFIX + "Tienes el chat oculto mientras tienes el tutorial activo.");
            return;
        }

        if (!sChatManager.isHidden()) {
            List<IChat> targetChats = new ArrayList<>();
            List<Player> pingedPlayers = new ArrayList<>();

            try {
                targetChats.add(sChatManager.getChat());

                String message = ChatColor.stripColor(e.getMessage());

                for (String word : message.split(" ")) {
                    if (word.startsWith("@")) {
                        if (Bukkit.getOfflinePlayer(word.replace("@", "")).isOnline()) {
                            Player target = Bukkit.getPlayer(word.replace("@", ""));

                            if (!pingedPlayers.contains(target)) {
                                pingedPlayers.add(target);
                            }

                            ServerPlayer sTarget = plugin.getPlayerRegistry().get(target.getUniqueId());
                            IChat targetChat;
                            try {
                                targetChat = sTarget.getChatManager().getChat();
                            } catch (ChatException ignored) {
                                sTarget.getChatManager().setGlobal();
                                targetChat = plugin.getChatManager().getGlobalChat();
                            }
                            if (!targetChats.contains(targetChat)) {
                                targetChats.add(targetChat);
                            }
                            message = message.replace(word, "§a~" + sTarget.getChatManager().getDisplayName() + "~§r");

                        }
                    }
                }

                for (IChat chat : targetChats) {
                    chat.sendMessage(message.replace("~", ""), e.getPlayer().getUniqueId());
                }

                for (Player player : pingedPlayers) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_XYLOPHONE, 1, 1);
                }
            } catch (ChatException exception) {
                sChatManager.setGlobal();
                p.sendMessage(CHAT_PREFIX + "Tu ocurrido un error con tu chat. Te hemos devuelto al chat global.");
            }
        } else {
            p.sendMessage(CHAT_PREFIX + "No puedes hablar mientras tienes el chat oculto.");
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getTextChannel().getId().equals(plugin.getGatewayId())) {
            if (!event.getAuthor().isBot() && !event.getMessage().getContentDisplay().startsWith("/")) {
                Member member = event.getMember();
                if (member != null) {
                    List<Role> roles = member.getRoles();
                    Color roleColor =  Color.WHITE;
                    if (!roles.isEmpty()) {
                        roleColor = roles.get(0).getColor();
                    }

                    ChatColor color = getChatColorFromColor(roleColor);
                    plugin.getChatManager().getGlobalChat().broadcast("[§bDISCORD§f] §7>> §r" + color + event.getMember().getEffectiveName() + ": §r" + event.getMessage().getContentDisplay());
                }
            }
        }
    }

    private ChatColor getChatColorFromColor(Color color) {

        Map<ChatColor, Double> mapping = new HashMap<>();

        for (Color key : chatColorMapping.keySet()) {
            mapping.put(chatColorMapping.get(key), ColorHelper.colorDistance(key, color));
        }

        List<Double> values = new ArrayList<>(mapping.values());
        values.sort(Comparator.naturalOrder());

        for (Map.Entry<ChatColor, Double> entry : mapping.entrySet()) {
            if (Objects.equals(entry.getValue(), values.get(0))) {
                return entry.getKey();
            }
        }
        return ChatColor.WHITE;
    }
}
