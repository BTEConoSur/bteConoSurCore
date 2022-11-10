package pizzaaxx.bteconosur;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.Chat.ChatHolder;
import pizzaaxx.bteconosur.Chat.ChatMessage;
import pizzaaxx.bteconosur.Chat.Components.ChatMessageComponent;
import pizzaaxx.bteconosur.Chat.Components.HoverAction;
import pizzaaxx.bteconosur.Chat.Events.ChatEventsListener;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Events.JoinEvent;
import pizzaaxx.bteconosur.Events.PreLoginEvent;
import pizzaaxx.bteconosur.Player.PlayerRegistry;
import pizzaaxx.bteconosur.SQL.SQLManager;
import pizzaaxx.bteconosur.WorldEdit.Shortcuts;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BTEConoSur extends JavaPlugin implements ChatHolder, Prefixable {

    private SQLManager sqlManager;

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    private PlayerRegistry playerRegistry;

    public PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    private final World mainWorld = Bukkit.getWorld("BTECS");

    public World getWorld() {
        return mainWorld;
    }

    @Override
    public void onEnable() {
        this.log("BUILD THE EARTH: CONO SUR");
        this.log("Developed by PIZZAAXX");
        this.log(" ");
        this.log("Starting plugin...");

        this.log("Starting database connection...");
        try {
            sqlManager = new SQLManager(this);
        } catch (SQLException e) {
            this.log("Plugin starting stopped. Database connection failed.");
            return;
        }
        this.log("Database connection established.");
        this.log("Starting player registry...");
        this.playerRegistry = new PlayerRegistry(this);

        this.log("Registering events...");
        this.registerListeners(
                this,
                new PreLoginEvent(this),
                new JoinEvent(this),
                new ChatEventsListener(this),
                new Shortcuts(this)
        );

        this.log("Starting chats...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.add(player.getUniqueId());
        }
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(this.getPrefix() + message);
    }

    public void warn(String message) {
        Bukkit.getConsoleSender().sendMessage(this.getPrefix() + "§e" + message);
    }

    public void error(String message) {
        Bukkit.getConsoleSender().sendMessage(this.getPrefix() + "§c" + message);
    }

    private void registerListeners(BTEConoSur plugin, Listener @NotNull ... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }

    // --- CHAT ---

    private final Set<UUID> players = new HashSet<>();

    @Override
    public String getChatID() {
        return "global";
    }

    @Override
    public String getChatEmoji() {
        return ":globe_with_meridians:";
    }

    @Override
    public String getChatDisplayName() {
        return "Global";
    }

    @Override
    public void move(UUID uuid, @NotNull ChatHolder newHolder) {
        this.remove(uuid);
        newHolder.add(uuid);
    }

    @Override
    public void remove(UUID uuid) {
        players.remove(uuid);
        String name = Bukkit.getPlayer(uuid).getName();
        for (UUID player : players) {
            this.getPlayerRegistry().get(player).getChatManager().sendMessage(
                    new ChatMessage(
                            new ChatMessageComponent(
                                    name,
                                    ChatColor.GREEN,
                                    new HoverAction(
                                            name,
                                            ChatColor.GREEN
                                    )
                            ),
                            new ChatMessageComponent(
                                    " ha salido del chat.",
                                    ChatColor.GRAY
                            )
                    )
            );
        }
    }

    @Override
    public void add(UUID uuid) {
        String name = Bukkit.getPlayer(uuid).getName();
        ChatMessage message = new ChatMessage(
                new ChatMessageComponent(
                        name,
                        ChatColor.GREEN,
                        new HoverAction(
                                name,
                                ChatColor.GREEN
                        )
                ),
                new ChatMessageComponent(
                        " se ha unido al chat.",
                        ChatColor.GRAY
                )
        );
        for (UUID player : players) {
            this.getPlayerRegistry().get(player).getChatManager().sendMessage(
                  message
            );
        }
        players.add(uuid);
    }

    @Override
    public void sendMessage(UUID sender, @NotNull ChatMessage message) {
        String name = Bukkit.getPlayer(sender).getName();
        ChatMessage chatMessage = new ChatMessage(
                new ChatMessageComponent("<"),
                new ChatMessageComponent(
                        name,
                        ChatColor.GREEN,
                        new HoverAction(
                                name,
                                ChatColor.GREEN
                        )
                ),
                new ChatMessageComponent("> ", ChatColor.WHITE)
        );
        chatMessage.append(message.getChatComponents());
        for (UUID player : players) {
            this.getPlayerRegistry().get(player).getChatManager().sendMessage(
                    chatMessage
            );
        }
    }

    @Override
    public void broadcast(ChatMessage message) {

    }

    @Override
    public void broadcast(ChatMessage message, boolean ignoreHidden) {

    }

    @Override
    public String getPrefix() {
        return "§f[§2CONO §aSUR§f] §7>> §f";
    }
}
