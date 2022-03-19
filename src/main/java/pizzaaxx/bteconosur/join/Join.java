package pizzaaxx.bteconosur.join;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pizzaaxx.bteconosur.serverPlayer.*;
import xyz.upperlevel.spigot.book.BookUtil;

import static pizzaaxx.bteconosur.chats.ChatCommand.CHAT_PREFIX;

public class Join implements Listener {

    private final PlayerRegistry playerRegistry;

    public Join(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ServerPlayer serverPlayer = playerRegistry.get(player.getUniqueId());

            ScoreboardManager.ScoreboardType scoreboard = serverPlayer.getScoreboardManager().getType();
            if (scoreboard == ScoreboardManager.ScoreboardType.SERVER) {
                serverPlayer.getScoreboardManager().update();
            }

        }

        playerRegistry.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        event.setJoinMessage(player.getDisplayName() + " ha entrado al servidor.");

        ServerPlayer serverPlayer = new ServerPlayer(player.getUniqueId());
        DataManager data = serverPlayer.getDataManager();

        if (!data.getString("name").equals(player.getName())) {
            data.set("name", player.getName());
        }

        if (data.get("primaryGroup") == null) {
            data.set("primaryGroup", "default");
        }

        if (data.get("chat") == null) {
            data.set("chat", "global");
        }

        if (data.get("defaultChat") == null) {
            data.set("defaultChat", "global");
        }

        if (data.get("hideChat") == null) {
            data.set("hideChat", false);
        }

        if (data.get("increment") == null) {
            data.set("increment", 1);
        }

        if (data.get("hideScoreboard") == null) {
            data.set("hideScoreboard", false);
        }

        if (data.get("scoreboard") == null) {
            data.set("scoreboard", "server");
        }

        if (data.get("scoreboardAuto") == null) {
            data.set("scoreboardAuto", true);
        }

        data.save();

        // SEND MESSAGES

        player.sendMessage(">+--------------+[-< ============= >-]+--------------+<");
        player.sendMessage(" ");
        player.sendMessage("§7                                Bienvenido a");
        player.sendMessage("§7                      §9§lBuildTheEarth: §a§lCono Sur");
        player.sendMessage(" ");

        // NOTIFICACIONES

        if (player.hasPlayedBefore()) {

            if (serverPlayer.getNotifications().size() > 0) {
                player.sendMessage(">+--------------+[-< NOTIFICACIONES >-]+--------------+<");
                int i = 1;
                for (String notif : serverPlayer.getNotifications()) {
                    player.sendMessage(i + ". " + notif.replace("&", "§"));
                    player.sendMessage(" ");
                    i++;
                }

                data.set("notifications", null);
                data.save();
            } else if (!serverPlayer.getDiscordManager().isLinked()) {
                player.sendMessage(">+--------------+[-< NOTIFICACIONES >-]+--------------+<");
                player.sendMessage("§c                   No tienes notificaciones nuevas.");
                player.sendMessage(" ");
            }
        }

        // DISCORD

        if (!serverPlayer.getDiscordManager().isLinked()) {
            player.sendMessage(">+-----------------+[-< DISCORD >-]+-----------------+<");
            player.sendMessage(BookUtil.TextBuilder.of("§f               §f").build(), BookUtil.TextBuilder.of("§f[§aHAZ CLICK PARA CONECTAR TU CUENTA§f]").onHover(BookUtil.HoverAction.showText("Haz click para conectar tu cuenta.")).onClick(BookUtil.ClickAction.runCommand("/link")).build());
            player.sendMessage(" ");
        }

        player.sendMessage(">+--------------+[-< ============= >-]+--------------+<");

        // SET PLAYER'S CHAT TO DEFAULT

        ChatManager manager = serverPlayer.getChatManager();
        if (!manager.getChat().equals(manager.getDefaultChat())) {
            manager.setChat(manager.getDefaultChat().getName());

            player.sendMessage(CHAT_PREFIX + "Te has unido al chat §a" + manager.getChat().getFormattedName() + "§f. §7(Jugadores: " + manager.getChat().getMembers().size() + ")");
        }

        if (serverPlayer.getScoreboardManager().getType() == ScoreboardManager.ScoreboardType.ME) {
            serverPlayer.getScoreboardManager().update();
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            ServerPlayer serverPlayerOnline = new ServerPlayer(online.getUniqueId());
            if (serverPlayerOnline.getScoreboardManager().getType() == ScoreboardManager.ScoreboardType.SERVER) {
                serverPlayerOnline.getScoreboardManager().update();
            }
        }
    }
}
