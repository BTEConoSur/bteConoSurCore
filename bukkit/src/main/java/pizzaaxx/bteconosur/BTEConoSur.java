package pizzaaxx.bteconosur;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
import pizzaaxx.bteconosur.Cities.CityManager;
import pizzaaxx.bteconosur.Cities.Commands.CitiesCommand;
import pizzaaxx.bteconosur.Cities.Events.CityEnterEvent;
import pizzaaxx.bteconosur.Commands.*;
import pizzaaxx.bteconosur.Commands.Managing.DeletePlayerDataCommand;
import pizzaaxx.bteconosur.Configuration.Configuration;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Countries.CountryManager;
import pizzaaxx.bteconosur.Discord.Link.LinkCommand;
import pizzaaxx.bteconosur.Events.JoinEvent;
import pizzaaxx.bteconosur.Events.PreLoginEvent;
import pizzaaxx.bteconosur.Events.QuitEvent;
import pizzaaxx.bteconosur.Events.TeleportEvent;
import pizzaaxx.bteconosur.Inventory.InventoryHandler;
import pizzaaxx.bteconosur.Player.PlayerRegistry;
import pizzaaxx.bteconosur.Projects.ProjectRegistry;
import pizzaaxx.bteconosur.Regions.RegionListenersHandler;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.SQLManager;
import pizzaaxx.bteconosur.WorldEdit.Commands.IncrementCommand;
import pizzaaxx.bteconosur.WorldEdit.Commands.PolywallsCommand;
import pizzaaxx.bteconosur.WorldEdit.Shortcuts;
import pizzaaxx.bteconosur.WorldEdit.WorldEditHandler;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BTEConoSur extends JavaPlugin implements ChatHolder, Prefixable {

    private World mainWorld;

    public World getWorld() {
        return mainWorld;
    }

    private com.sk89q.worldedit.world.World worldEditWorld;

    public com.sk89q.worldedit.world.World getWorldEditWorld() {
        return this.worldEditWorld;
    }

    private final ObjectMapper mapper = new ObjectMapper();

    public ObjectMapper getJSONMapper() {
        return mapper;
    }

    private SQLManager sqlManager;

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    private PlayerRegistry playerRegistry;

    public PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    private JDA bot;

    public JDA getBot() {
        return bot;
    }

    private final CountryManager countryManager = new CountryManager(this);

    public CountryManager getCountryManager() {
        return countryManager;
    }

    private WorldGuardPlugin worldGuard;

    public WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    private RegionManager regionManager;

    public RegionManager getRegionManager() {
        return regionManager;
    }

    private final CityManager cityManager = new CityManager(this);

    public CityManager getCityManager() {
        return cityManager;
    }

    private final ProjectRegistry projectRegistry = new ProjectRegistry(this);

    public ProjectRegistry getProjectRegistry() {
        return projectRegistry;
    }

    private final WorldEditHandler worldEditHandler = new WorldEditHandler(this);

    public WorldEditHandler getWorldEdit() {
        return worldEditHandler;
    }

    private final InventoryHandler inventoryHandler = new InventoryHandler(this);

    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
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
            this.error("Plugin starting stopped. Database connection failed.");
            return;
        }
        this.log("Database connection established.");

        this.log("Starting player registry...");
        this.playerRegistry = new PlayerRegistry(this);

        mainWorld = Bukkit.getWorld("BTECS");
        worldEditWorld = new BukkitWorld(mainWorld);
        worldGuard = WorldGuardPlugin.inst();
        regionManager = WorldGuardPlugin.inst().getRegionManager(mainWorld);

        this.log("Registering events...");

        RegionListenersHandler regionListenersHandler = new RegionListenersHandler(this);
        regionListenersHandler.registerEnter(
                input -> input.startsWith("city_") && !input.endsWith("_urban"),
                new CityEnterEvent(this)
        );

        GetCommand getCommand = new GetCommand(this);

        this.registerListeners(
                this,
                regionListenersHandler,
                new PreLoginEvent(this),
                new JoinEvent(this),
                new QuitEvent(this),
                new ChatEventsListener(this),
                new Shortcuts(this),
                this.inventoryHandler,
                new TeleportEvent(),
                getCommand
        );

        this.log("Starting chats...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.addToChat(player.getUniqueId(), true);
        }

        // --- COUNTRIES ---
        this.log("Starting country manager...");
        try {
            countryManager.init();
        } catch (SQLException | JsonProcessingException e) {
            this.error("Plugin starting stopped. Country manager startup failed.");
            return;
        }

        // --- CITIES ---
        this.log("Starting city manager...");
        try {
            cityManager.init();
        } catch (SQLException e) {
            this.error("Plugin starting stopped. City manager startup failed.");
            return;
        }

        // --- PROJECTS ---
        this.log("Starting project registry...");
        try {
            projectRegistry.init();
        } catch (SQLException e) {
            this.error("Plugin starting stopped. Project registry startup failed.");
            return;
        }

        LinkCommand linkCommand = new LinkCommand(this);

        // --- DISCORD ---
        Configuration discordConfig = new Configuration(this, "discord/token");
        String token = discordConfig.getString("token");

        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        jdaBuilder.addEventListeners(
            linkCommand
        );
        jdaBuilder.setStatus(OnlineStatus.ONLINE);
        jdaBuilder.setActivity(Activity.playing("bteconosur.com"));

        try  {
            bot = jdaBuilder.build().awaitReady();
        } catch (InterruptedException e) {
            this.error("Plugin starting stopped. Bot startup failed.");
            return;
        }

        // --- COMMANDS ---
        this.log("Registering commands...");
        getCommand("city").setExecutor(new CitiesCommand(this));
        getCommand("increment").setExecutor(new IncrementCommand(this));
        getCommand("link").setExecutor(linkCommand);
        getCommand("unlink").setExecutor(linkCommand);
        getCommand("tpdir").setExecutor(new TpdirCommand(this));
        getCommand("deleteplayerdata").setExecutor(new DeletePlayerDataCommand(this));
        getCommand("height").setExecutor(new HeightCommand(this));
        getCommand("googleMaps").setExecutor(new GoogleMapsCommand(this));
        getCommand("banners").setExecutor(new BannersCommand());
        getCommand("get").setExecutor(getCommand);
        getCommand("/polywalls").setExecutor(new PolywallsCommand(this));
        getCommand("pwarp").setExecutor(new PWarpsCommand(this));

        EmbedBuilder startEmbed = new EmbedBuilder();
        startEmbed.setColor(Color.GREEN);
        startEmbed.setTitle("¡El servidor está online!");
        startEmbed.setDescription(":link: **IP:** bteconosur.com");
        MessageEmbed embed = startEmbed.build();
        for (Country country : countryManager.getAllCountries()) {
            country.getGlobalChatChannel().sendMessageEmbeds(embed).queue();
            country.getCountryChatChannel().sendMessageEmbeds(embed).queue();

        }
    }

    @Override
    public void onDisable() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle("El servidor se ha apagado.");
        embedBuilder.setDescription("Te esperamos cuando vuelva a estar disponible.");
        MessageEmbed embed = embedBuilder.build();
        for (Country country : countryManager.getAllCountries()) {
            country.getGlobalChatChannel().sendMessageEmbeds(embed).queue();
            country.getCountryChatChannel().sendMessageEmbeds(embed).queue();
        }
        bot.shutdown();
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

    private void registerListeners(BTEConoSur plugin, @NotNull Listener ... listeners) {
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
    public void moveToChat(UUID uuid, @NotNull ChatHolder newHolder) {
        this.removeFromChat(uuid, false);
        newHolder.addToChat(uuid, false);
    }

    @Override
    public void removeFromChat(UUID uuid, boolean disableDiscord) {
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
        if (!disableDiscord) {
            for (Country country : countryManager.getAllCountries()) {
                country.getGlobalChatChannel().sendMessage(":heavy_minus_sign: **" + name + "** ha salido del chat.").queue();
            }
        }
    }

    @Override
    public void addToChat(UUID uuid, boolean disableDiscord) {
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
        if (!disableDiscord) {
            for (Country country : countryManager.getAllCountries()) {
                country.getGlobalChatChannel().sendMessage(":heavy_plus_sign: **" + name + "** ha entrado al chat.").queue();
            }
        }
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
