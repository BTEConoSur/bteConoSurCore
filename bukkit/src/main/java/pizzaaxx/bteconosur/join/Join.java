package pizzaaxx.bteconosur.join;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.chats.Chat;
import pizzaaxx.bteconosur.server.player.*;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.ArrayList;
import java.util.List;

import static pizzaaxx.bteconosur.chats.ChatCommand.CHAT_PREFIX;
import static pizzaaxx.bteconosur.projects.ProjectsCommand.tutorialSteps;

public class Join implements Listener, CommandExecutor {

    private final PlayerRegistry playerRegistry;
    private final Plugin plugin;

    public Join(PlayerRegistry playerRegistry, Plugin plugin) {
        this.playerRegistry = playerRegistry;
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {

        ServerPlayer s = new ServerPlayer(event.getPlayer());
        Chat chat = s.getChatManager().getChat();
        chat.removeMember(event.getPlayer());

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (player.getUniqueId() != event.getPlayer().getUniqueId()) {
                ServerPlayer serverPlayer = playerRegistry.get(player.getUniqueId());

                ScoreboardManager.ScoreboardType scoreboard = serverPlayer.getScoreboardManager().getType();
                if (scoreboard == ScoreboardManager.ScoreboardType.SERVER) {
                    serverPlayer.getScoreboardManager().update();
                }
            }
        }
        tutorialSteps.remove(event.getPlayer().getUniqueId());
        playerRegistry.remove(event.getPlayer().getUniqueId());

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoinHighest(@NotNull PlayerJoinEvent event) {

        ServerPlayer player = new ServerPlayer(event.getPlayer());

        event.setJoinMessage(player.getChatManager().getDisplayName() + " §7ha entrado al servidor.");
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {

        Player player = event.getPlayer();

        ServerPlayer serverPlayer = new ServerPlayer(player.getUniqueId());
        DataManager data = serverPlayer.getDataManager();


        // SEND MESSAGES

        player.sendMessage(">+--------------+[-< ============= >-]+--------------+<");
        player.sendMessage(" ");
        player.sendMessage("§7                                Bienvenido a");
        player.sendMessage("§7                      §9§lBuildTheEarth: §a§lCono Sur");
        player.sendMessage(" ");

        // NOTIFICACIONES

        if (player.hasPlayedBefore()) {

            List<String> notifications = serverPlayer.getNotifications();
            if (notifications.size() > 0) {
                player.sendMessage(">+--------------+[-< NOTIFICACIONES >-]+--------------+<");
                int i = 1;
                for (String notif : notifications) {
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
        if (!manager.getChat().getName().equals(manager.getDefaultChat().getName())) {
            player.sendMessage(CHAT_PREFIX + "Te has unido al chat §a" + manager.getChat().getFormattedName() + "§f. §7(Jugadores: " + manager.getChat().getMembers().size() + ")");
        }
        manager.setChat(manager.getDefaultChat().getName());

        if (serverPlayer.getScoreboardManager().getType() == ScoreboardManager.ScoreboardType.ME) {
            serverPlayer.getScoreboardManager().update();
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            ServerPlayer serverPlayerOnline = new ServerPlayer(online.getUniqueId());
            if (serverPlayerOnline.getScoreboardManager().getType() == ScoreboardManager.ScoreboardType.SERVER) {
                serverPlayerOnline.getScoreboardManager().update();
            }
        }

        if (data.contains("isFirst")) {

            BookUtil.BookBuilder builder = BookUtil.writtenBook();

            List<BaseComponent[]> pages = new ArrayList<>();

            BookUtil.PageBuilder page = new BookUtil.PageBuilder();

            page.add("      §7Bienvenido a");
            page.newLine();
            page.add("   §9§lBuildTheEarth:");
            page.newLine();
            page.add("       §a§lCono Sur");
            page.newLine();
            page.newLine();
            page.add("   §8¿Qué te gustaría");
            page.newLine();
            page.add("         §8hacer?");
            page.newLine();
            page.newLine();
            page.add("      ");
            page.add(
                    BookUtil.TextBuilder.of("[CONSTRUIR]").onHover(BookUtil.HoverAction.showText("Haz click para elegir")).onClick(BookUtil.ClickAction.runCommand("/welcomeBook build")).build()
            );
            page.newLine();
            page.newLine();
            page.add("       ");
            page.add(
                    BookUtil.TextBuilder.of("[VISITAR]").onHover(BookUtil.HoverAction.showText("Haz click para elegir")).onClick(BookUtil.ClickAction.runCommand("/welcomeBook visit")).build()
            );

            pages.add(page.build());

            builder.pages(pages);

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    BookUtil.openPlayer(player, builder.build());
                }
            };
            runnable.runTaskLaterAsynchronously(plugin, 80);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player p = (Player) sender;

        if (args.length > 0) {
            if (args[0].equals("build")) {

                BookUtil.BookBuilder builder = BookUtil.writtenBook();

                List<BaseComponent[]> pages = new ArrayList<>();

                BookUtil.PageBuilder page = new BookUtil.PageBuilder();

                page.add("      ¡Perfecto!");
                page.newLine();
                page.newLine();
                page.add("Usa ");
                page.add(
                        BookUtil.TextBuilder.of("§a/p tutorial").onClick(BookUtil.ClickAction.runCommand("/p tutorial")).onHover(BookUtil.HoverAction.showText("Haz click para usar el comando")).build()
                );
                page.add("§r para empezar a construir.");

                pages.add(page.build());

                builder.pages(pages);

                BookUtil.openPlayer(p, builder.build());

            } else if (args[0].equals("visit")) {

                BookUtil.BookBuilder builder = BookUtil.writtenBook();

                List<BaseComponent[]> pages = new ArrayList<>();

                BookUtil.PageBuilder page = new BookUtil.PageBuilder();

                page.add("      §8¿Qué país te");
                page.newLine();
                page.add("    §8gustaría visitar?");
                page.newLine();
                page.newLine();
                page.add("      ");
                page.add(BookUtil.TextBuilder.of("[ARGENTINA]").onClick(BookUtil.ClickAction.runCommand("/tp 7344036.5 -2 -25497.5")).onHover(BookUtil.HoverAction.showText("Haz click para ir")).build());
                page.newLine();
                page.newLine();

                page.add("        ");
                page.add(BookUtil.TextBuilder.of("[BOLIVIA]").onClick(BookUtil.ClickAction.runCommand("/tp 0 0 0")).onHover(BookUtil.HoverAction.showText("Haz click para ir")).build());
                page.newLine();
                page.newLine();

                page.add("         ");
                page.add(BookUtil.TextBuilder.of("[CHILE]").onClick(BookUtil.ClickAction.runCommand("/tp -8287783 546 2846478")).onHover(BookUtil.HoverAction.showText("Haz click para ir")).build());
                page.newLine();
                page.newLine();

                page.add("       ");
                page.add(BookUtil.TextBuilder.of("[PARAGUAY]").onClick(BookUtil.ClickAction.runCommand("/tp 0 0 0")).onHover(BookUtil.HoverAction.showText("Haz click para ir")).build());
                page.newLine();
                page.newLine();

                page.add("         ");
                page.add(BookUtil.TextBuilder.of("[PERÚ]").onClick(BookUtil.ClickAction.runCommand("/tp 1139207 220 214115")).onHover(BookUtil.HoverAction.showText("Haz click para ir")).build());
                page.newLine();
                page.newLine();

                page.add("       ");
                page.add(BookUtil.TextBuilder.of("[URUGUAY]").onClick(BookUtil.ClickAction.runCommand("/tp -6736165 32 3346376")).onHover(BookUtil.HoverAction.showText("Haz click para ir")).build());
                page.newLine();
                page.newLine();

                pages.add(page.build());

                builder.pages(pages);

                BookUtil.openPlayer(p, builder.build());

            }
        }

        return true;
    }
}
