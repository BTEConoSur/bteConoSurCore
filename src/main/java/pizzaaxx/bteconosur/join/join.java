package pizzaaxx.bteconosur.join;

import com.sk89q.worldedit.bukkit.adapter.impl.CraftBukkit_v1_6_R3;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.chats.Chat;
import pizzaaxx.bteconosur.notifications.Notification;
import pizzaaxx.bteconosur.playerData.PlayerData;
import xyz.upperlevel.spigot.book.BookUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.IntToDoubleFunction;

import static pizzaaxx.bteconosur.chats.command.chatsPrefix;

public class join implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        e.setJoinMessage(new ServerPlayer(e.getPlayer()).getDisplayName() + " ha entrado al servidor.");

        Player p = e.getPlayer();
        ServerPlayer s = new ServerPlayer(p);

        // SET NECESSARY DATA

        PlayerData playerData = new PlayerData(p);

        if ((String) playerData.getData("name") != p.getName()) {
            playerData.setData("name", p.getName());
        }

        if (new PlayerData(p).getData("primaryGroup") == null) {
            playerData.setData("primaryGroup", "default");
        }

        if (new PlayerData(p).getData("chat") == null) {
            playerData.setData("chat", "global");
        }

        if (new PlayerData(p).getData("defaultChat") == null) {
            playerData.setData("defaultChat", "global");
        }

        if (new PlayerData(p).getData("hideChat") == null) {
            playerData.setData("hideChat", false);
        }

        if (new PlayerData(p).getData("increment") == null) {
            playerData.setData("increment", 1);
        }

        playerData.save();

        // SEND MESSAGES

        p.sendMessage(">+--------------+[-< ============= >-]+--------------+<");
        p.sendMessage(" ");
        p.sendMessage("§7                                Bienvenido a");
        p.sendMessage("§7                      §9§lBuildTheEarth: §a§lCono Sur");
        p.sendMessage(" ");

        // NOTIFICACIONES

        p.sendMessage(">+--------------+[-< NOTIFICACIONES >-]+--------------+<");

        if (s.getNotifications().size() > 0) {
            int i = 1;
            for (String notif : s.getNotifications()) {
                p.sendMessage(i + ". " + notif.replace("&", "§"));
                p.sendMessage(" ");
                i++;
            }

            playerData.deleteData("notifications");
            playerData.save();
        } else {
            p.sendMessage("§c                   No tienes notificaciones nuevas.");
            p.sendMessage(" ");
        }

        // DISCORD

        if (s.getDiscordUser() == null) {
            p.sendMessage(">+-----------------+[-< DISCORD >-]+-----------------+<");
            p.sendMessage(BookUtil.TextBuilder.of("§f               §f").build(), BookUtil.TextBuilder.of("§f[§aHAZ CLICK PARA CONECTAR TU CUENTA§f]").onHover(BookUtil.HoverAction.showText("Haz click para conectar tu cuenta.")).onClick(BookUtil.ClickAction.runCommand("/link")).build());
            p.sendMessage(" ");
        }

        p.sendMessage(">+--------------+[-< ============= >-]+--------------+<");

        // SET PLAYER'S CHAT TO DEFAULT

        if (!(s.getChat().getName().equals(s.getDefaultChat().getName()))) {
            s.setChat(s.getDefaultChat().getName());

            p.sendMessage(chatsPrefix + "Te has unido al chat §a" + s.getChat().getFormattedName() + "§f. §7(Jugadores: " + s.getChat().getMembers().size() + ")");
        }
    }
}
