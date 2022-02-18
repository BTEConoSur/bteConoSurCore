package pizzaaxx.bteconosur.join;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pizzaaxx.bteconosur.serverPlayer.PlayerRegistry;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;
import pizzaaxx.bteconosur.player.data.PlayerData;
import xyz.upperlevel.spigot.book.BookUtil;

import static pizzaaxx.bteconosur.chats.Command.chatsPrefix;

public class Join implements Listener {

    private final PlayerRegistry playerRegistry;

    public Join(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ServerPlayer serverPlayer = playerRegistry.get(player.getUniqueId());

            String scoreboard = serverPlayer.getScoreboard();
            if (scoreboard.equals("server")) {
                serverPlayer.updateScoreboard();
            }

        }

        playerRegistry.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        event.setJoinMessage(player.getDisplayName() + " ha entrado al servidor.");

        ServerPlayer serverPlayer = new ServerPlayer(player.getUniqueId());
        PlayerData playerData = serverPlayer.getData();

        String dataName = (String) playerData.getData("name");
        if (!dataName.equals(player.getName())) {
            playerData.setData("name", player.getName());
        }

        if (playerData.getData("primaryGroup") == null) {
            playerData.setData("primaryGroup", "default");
        }

        if (playerData.getData("chat") == null) {
            playerData.setData("chat", "global");
        }

        if (playerData.getData("defaultChat") == null) {
            playerData.setData("defaultChat", "global");
        }

        if (playerData.getData("hideChat") == null) {
            playerData.setData("hideChat", false);
        }

        if (playerData.getData("increment") == null) {
            playerData.setData("increment", 1);
        }

        if (playerData.getData("hideScoreboard") == null) {
            playerData.setData("hideScoreboard", false);
        }

        if (playerData.getData("scoreboard") == null) {
            playerData.setData("scoreboard", "server");
        }

        if (playerData.getData("scoreboardAuto") == null) {
            playerData.setData("scoreboardAuto", true);
        }

        playerData.save();

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

                playerData.deleteData("notifications");
                playerData.save();
            } else if (!serverPlayer.hasDiscordUser()) {
                player.sendMessage(">+--------------+[-< NOTIFICACIONES >-]+--------------+<");
                player.sendMessage("§c                   No tienes notificaciones nuevas.");
                player.sendMessage(" ");
            }
        }

        // DISCORD

        if (!(serverPlayer.hasDiscordUser())) {
            player.sendMessage(">+-----------------+[-< DISCORD >-]+-----------------+<");
            player.sendMessage(BookUtil.TextBuilder.of("§f               §f").build(), BookUtil.TextBuilder.of("§f[§aHAZ CLICK PARA CONECTAR TU CUENTA§f]").onHover(BookUtil.HoverAction.showText("Haz click para conectar tu cuenta.")).onClick(BookUtil.ClickAction.runCommand("/link")).build());
            player.sendMessage(" ");
        }

        player.sendMessage(">+--------------+[-< ============= >-]+--------------+<");

        // SET PLAYER'S CHAT TO DEFAULT

        if (!(serverPlayer.getChat().getName().equals(serverPlayer.getDefaultChat().getName()))) {
            serverPlayer.setChat(serverPlayer.getDefaultChat().getName());

            player.sendMessage(chatsPrefix + "Te has unido al chat §a" + serverPlayer.getChat().getFormattedName() + "§f. §7(Jugadores: " + serverPlayer.getChat().getMembers().size() + ")");
        }

        serverPlayer.updateData();
        serverPlayer.updateScoreboard();

        for (Player online : Bukkit.getOnlinePlayers()) {
            ServerPlayer serverPlayerOnline = new ServerPlayer(player.getUniqueId());
            if (serverPlayerOnline.getScoreboard().equals("server")) {
                serverPlayerOnline.updateScoreboard();
            }
        }
    }
}
