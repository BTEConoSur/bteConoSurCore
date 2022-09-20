package pizzaaxx.bteconosur;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.Chat.ChatCommand;
import pizzaaxx.bteconosur.Chat.ChatEventsListener;
import pizzaaxx.bteconosur.Points.PointsContainer;
import pizzaaxx.bteconosur.ServerPlayer.Managers.ChatManager;
import pizzaaxx.bteconosur.ServerPlayer.Managers.ScoreboardManager;
import pizzaaxx.bteconosur.ServerPlayer.PlayerRegistry;
import pizzaaxx.bteconosur.commands.HelpCommand;
import pizzaaxx.bteconosur.commands.ScoreboardCommand;
import pizzaaxx.bteconosur.commands.*;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.CountryManager;
import pizzaaxx.bteconosur.country.cities.projects.Command.PFindCommand;
import pizzaaxx.bteconosur.country.cities.projects.Command.ProjectsCommand;
import pizzaaxx.bteconosur.country.cities.projects.Events.ProjectActionBar;
import pizzaaxx.bteconosur.country.cities.projects.Events.ProjectManageInventoryListener;
import pizzaaxx.bteconosur.country.cities.projects.Events.RequestResponse;
import pizzaaxx.bteconosur.country.cities.projects.Events.TabCompletions;
import pizzaaxx.bteconosur.country.cities.projects.GlobalProjectsManager;
import pizzaaxx.bteconosur.discord.fuzzyMatching.FuzzyMatchListenerHandler;
import pizzaaxx.bteconosur.discord.fuzzyMatching.listeners.BedrockListener;
import pizzaaxx.bteconosur.discord.fuzzyMatching.listeners.IPListener;
import pizzaaxx.bteconosur.discord.fuzzyMatching.listeners.PremiumListener;
import pizzaaxx.bteconosur.discord.slashCommands.*;
import pizzaaxx.bteconosur.discord.slashCommands.link.LinkUnlinkCommand;
import pizzaaxx.bteconosur.discord.slashCommands.link.LinkUnlinkMinecraftCommand;
import pizzaaxx.bteconosur.events.EventsCommand;
import pizzaaxx.bteconosur.item.ItemBuilder;
import pizzaaxx.bteconosur.join.Join;
import pizzaaxx.bteconosur.listener.AsyncPlayerPreLoginListener;
import pizzaaxx.bteconosur.listener.ProjectBlockPlacingListener;
import pizzaaxx.bteconosur.misc.Security;
import pizzaaxx.bteconosur.ranks.Donator;
import pizzaaxx.bteconosur.ranks.PromoteDemote;
import pizzaaxx.bteconosur.ranks.Streamer;
import pizzaaxx.bteconosur.teleport.OnTeleport;
import pizzaaxx.bteconosur.terramap.ImagesServer;
import pizzaaxx.bteconosur.testing.Fixing;
import pizzaaxx.bteconosur.testing.ReloadPlayer;
import pizzaaxx.bteconosur.testing.Testing;
import pizzaaxx.bteconosur.worldedit.*;
import pizzaaxx.bteconosur.worldguard.MovementHandler;
import pizzaaxx.bteconosur.worldguard.RegionEvents;
import pizzaaxx.bteconosur.worldguard.WorldGuardHelper;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.country.cities.projects.Command.ProjectsCommand.background;

public final class BteConoSur extends JavaPlugin implements PointsContainer {

    private final World mainWorld = Bukkit.getWorld("BTECS");

    public World getWorld() {
        return mainWorld;
    }

    public com.sk89q.worldedit.world.World getWEWorld() {
        return new BukkitWorld(mainWorld);
    }

    public static String key;

    private final PlayerRegistry playerRegistry = new PlayerRegistry(this);

    public PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    private LuckPerms luckPerms;

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    private final WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();

    public WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    private final RegionManager regionsManager = WorldGuardPlugin.inst().getRegionManager(mainWorld);

    public RegionManager getRegionsManager() {
        return regionsManager;
    }

    private CountryManager countryManager;

    public CountryManager getCountryManager() {
        return countryManager;
    }

    private final pizzaaxx.bteconosur.Chat.ChatManager chatManager = new pizzaaxx.bteconosur.Chat.ChatManager(this);

    public pizzaaxx.bteconosur.Chat.ChatManager getChatManager() {
        return chatManager;
    }

    private final GlobalProjectsManager projectsManager = new GlobalProjectsManager(this);

    public GlobalProjectsManager getProjectsManager() {
        return projectsManager;
    }

    private final Configuration maxPoints = new Configuration(this, "maxPoints");

    private final WorldEditHelper worldEditHelper = new WorldEditHelper(this);

    public WorldEditHelper getWorldEditHelper() {
        return worldEditHelper;
    }

    private final RegionEvents regionEventsManager = new RegionEvents(this);

    public RegionEvents getRegionEventsManager() {
        return regionEventsManager;
    }

    private final WorldGuardHelper worldGuardHelper = new WorldGuardHelper(this);

    public WorldGuardHelper getWorldGuardHelper() {
        return worldGuardHelper;
    }

    private static class GlobalPointsComparator implements Comparator<UUID> {

        private final BteConoSur plugin;

        public GlobalPointsComparator(BteConoSur plugin) {
            this.plugin = plugin;
        }

        @Override
        public int compare(UUID u1, UUID u2) {
            int p1 = plugin.getPlayerRegistry().get(u1).getPointsManager().getPoints(plugin);
            int p2 = plugin.getPlayerRegistry().get(u2).getPointsManager().getPoints(plugin);
            return Integer.compare(p1, p2);
        }
    }

    @Override
    public void checkMaxPoints(UUID uuid) {

        java.util.List<UUID> uuids = new ArrayList<>();
        for (String key : maxPoints.getStringList("max")) {
            uuids.add(UUID.fromString(key));
        }
        if (!uuids.contains(uuid)) {
            uuids.add(uuid);
        }
        uuids.sort(new GlobalPointsComparator(this));

        List<String> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add(uuids.get(i).toString());
        }
        maxPoints.set("max", result);
        maxPoints.save();

    }

    @Override
    public @NotNull List<UUID> getMaxPoints() {
        List<UUID> max = new ArrayList<>();
        for (String key : maxPoints.getStringList("max")) {
            max.add(UUID.fromString(key));
        }
        return max;
    }

    private JDA bot;

    public JDA getBot() {
        return bot;
    }

    private String gatewayId;

    public String getGatewayId() {
        return gatewayId;
    }

    public TextChannel getGateway() {
        return bot.getTextChannelById(gatewayId);
    }

    @Override
    public void onEnable() {

        getLogger().info("¡Iniciando BTE Cono Sur!");

        projectsManager.initialize();


        WhereCommand whereCommand = new WhereCommand(this);
        Configuration teleportsConfig = new Configuration(this, "teleports");
        SelectionCommands selectionCommands = new SelectionCommands(this);


        registerListeners(
                new Join(playerRegistry, this),
                new ProjectActionBar(this),
                new OnTeleport(),
                new PresetsCommand(this),
                new PFindCommand(this),
                new ShortCuts(playerRegistry, selectionCommands),
                new ChatEventsListener(this),
                new ScoreboardCommand(this),
                new GetCommand(this),
                new PrefixCommand(this),
                new LobbyCommand(this, teleportsConfig),
                new EventsCommand(),
                new ProjectManageInventoryListener(this),
                new AsyncPlayerPreLoginListener(playerRegistry, this),
                new MovementHandler(this),
                new ProjectBlockPlacingListener(this),
                new Security(),
                selectionCommands,
                whereCommand
        );

        getLogger().info("Registering commands...");
        getCommand("/divide").setExecutor(new DivideCommand(this));
        getCommand("project").setExecutor(new ProjectsCommand(this));
        getCommand("nightvision").setExecutor(new NightVisionCommand());
        getCommand("promote").setExecutor(new PromoteDemote(this));
        getCommand("prefix").setExecutor(new PrefixCommand(this));
        getCommand("chat").setExecutor(new ChatCommand(this));
        getCommand("nickname").setExecutor(new NickNameCommand(this));
        getCommand("testing").setExecutor(new Testing());
        getCommand("demote").setExecutor(new PromoteDemote(this));
        getCommand("project").setTabCompleter(new TabCompletions(this));
        getCommand("presets").setExecutor(new PresetsCommand(this));
        getCommand("googlemaps").setExecutor(new GoogleMapsCommand());
        getCommand("increment").setExecutor(new IncrementCommand(playerRegistry));
        getCommand("pwarp").setExecutor(new PWarpCommand(this));
        getCommand("/polywalls").setExecutor(new Polywall(this));
        getCommand("donator").setExecutor(new Donator());
        getCommand("streamer").setExecutor(new Streamer());
        getCommand("streaming").setExecutor(new StreamingCommand(this));
        getCommand("get").setExecutor(new GetCommand(this));
        getCommand("scoreboard").setExecutor(new ScoreboardCommand(this));
        getCommand("tpdir").setExecutor(new TpDirCommand(this));
        getCommand("event").setExecutor(new EventsCommand());
        getCommand("lobby").setExecutor(new LobbyCommand(this, teleportsConfig));
        getCommand("assets").setExecutor(new LobbyCommand(this, teleportsConfig));
        getCommand("event").setExecutor(new EventsCommand());
        getCommand("manageevent").setExecutor(new EventsCommand());
        getCommand("help").setExecutor(new HelpCommand(new Configuration(this, "help")));
        getCommand("terraform").setExecutor(new TerraformCommand(this));
        getCommand("welcomeBook").setExecutor(new Join(playerRegistry, this));
        getCommand("banner").setExecutor(new BannersCommand());
        getCommand("height").setExecutor(new HeightCommand());
        getCommand("fix").setExecutor(new Fixing(this));
        getCommand("/selundo").setExecutor(selectionCommands);
        getCommand("/selredo").setExecutor(selectionCommands);
        getCommand("reloadPlayer").setExecutor(new ReloadPlayer());

        createDirectories(
                "",
                "projects",
                "playerData",
                "link",
                "pending_projects",
                "projectTags",
                "discord",
                "chat",
                "points",
                "trees/schematics"
        );

        // GUI

        background = ItemBuilder.of(Material.STAINED_GLASS_PANE, 1, 15)
                .name(" ")
                .build();

        Configuration discordConfig = new Configuration(this, "discord/config");

        gatewayId = discordConfig.getString("gateway");

        // DISCORD BOT
        JDABuilder builder = JDABuilder.createDefault(discordConfig.getString("token"));
        builder.setActivity(Activity.playing("IP: bteconosur.com"));
        builder.setStatus(OnlineStatus.ONLINE);

        FuzzyMatchListenerHandler handler = new FuzzyMatchListenerHandler();
        handler.registerListener("cual es la ip?", new IPListener(), FuzzyMatchListenerHandler.MatchType.PARTIAL, 4, "ip");
        handler.registerListener("como entro al server?", new IPListener(), FuzzyMatchListenerHandler.MatchType.PARTIAL, 4);
        handler.registerListener("como entro al servidor?", new IPListener(), FuzzyMatchListenerHandler.MatchType.PARTIAL, 4);
        handler.registerListener("el server es premium?", new PremiumListener(), FuzzyMatchListenerHandler.MatchType.WHOLE, 4, "server");
        handler.registerListener("el servidor es premium?", new PremiumListener(), FuzzyMatchListenerHandler.MatchType.WHOLE, 4, "servidor");
        handler.registerListener("necesito premium?", new PremiumListener(), FuzzyMatchListenerHandler.MatchType.WHOLE, 4);
        handler.registerListener("es premium?", new PremiumListener(), FuzzyMatchListenerHandler.MatchType.WHOLE, 4);
        handler.registerListener("sirve bedrock?", new BedrockListener(), FuzzyMatchListenerHandler.MatchType.WHOLE, 4);
        handler.registerListener("se puede con bedrock?", new BedrockListener(), FuzzyMatchListenerHandler.MatchType.WHOLE, 4);
        handler.registerListener("se puede entrar con bedrock?", new BedrockListener(), FuzzyMatchListenerHandler.MatchType.WHOLE, 4);
        handler.registerListener("bedrock sirve?", new BedrockListener(), FuzzyMatchListenerHandler.MatchType.WHOLE, 4);
        handler.registerListener("puedo entrar al servidor con bedrock?", new BedrockListener(), FuzzyMatchListenerHandler.MatchType.WHOLE, 4);
        handler.registerListener("puedo entrar con bedrock al servidor?", new BedrockListener(), FuzzyMatchListenerHandler.MatchType.WHOLE, 4);


        registerDiscordListener(builder,
                handler,
                new RequestResponse(this),
                new ChatEventsListener(this),
                new OnlineCommand(),
                whereCommand,
                new pizzaaxx.bteconosur.discord.slashCommands.ModsCommand(),
                new pizzaaxx.bteconosur.discord.slashCommands.SchematicCommand(),
                new LinkUnlinkCommand(new Configuration(this, "link/links"), this),
                new pizzaaxx.bteconosur.discord.slashCommands.ProjectCommand(),
                new PlayerCommand(new Configuration(this, "discord/groupEmojis")),
                new pizzaaxx.bteconosur.discord.slashCommands.ScoreboardCommand(this),
                new FindColorCommand(this),
                new ProjectTagCommand(),
                new pizzaaxx.bteconosur.discord.slashCommands.HelpCommand(new Configuration(this, "help")),
                new EventCommand(),
                new ShortCommands(),
                new PatternCommand(this)
        );

        whereCommand.updateHeads();

        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES);
        try {
            bot = builder.build().awaitReady();
            getCommand("btecsUpdateSlashCommands").setExecutor(new UpdateSlashCommands(bot));
            getCommand("link").setExecutor(new LinkUnlinkMinecraftCommand(bot, this));
            getCommand("unlink").setExecutor(new LinkUnlinkMinecraftCommand(bot, this));
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        key = new Configuration(this, "key").getString("key");

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }

        EmbedBuilder online = new EmbedBuilder();
        online.setColor(new Color(0, 255, 42));
        online.setTitle("¡El servidor ya está online!");
        online.setDescription("\uD83D\uDD17 **IP:** bteconosur.com");
        getGateway().sendMessageEmbeds(online.build()).queue();

        getLogger().info("Iniciando la secuancia de chqueo de scoreboards automáticos.");

        BteConoSur plugin = this;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                ScoreboardManager.checkAutoScoreboards(plugin);
            }
        };

        runnable.runTaskTimer(this, 300, 300);

        countryManager = new CountryManager(this, bot);
        countryManager.add("argentina", "ar", "Argentina", false);
        countryManager.add("bolivia", "bo", "Bolivia", true);
        countryManager.add("chile", "cl", "Chile", true);
        countryManager.add("paraguay", "py", "Paraguay", true);
        countryManager.add("peru", "pe", "Perú", true);
        countryManager.add("uruguay", "uy", "Uruguay", true);

        // Initialize TerramapServer

        ImagesServer imgServer = new ImagesServer(this);

    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling  BTE Cono Sur!");

        EmbedBuilder online = new EmbedBuilder();
        online.setColor(new Color(255, 0, 0));
        online.setTitle("El servidor ha sido apagado.");
        online.setDescription("Te esperamos cuando vuelva a estar disponible.");

        gateway.sendMessageEmbeds(online.build()).queue();

        bot.shutdown();
    }

    public String getPlayerName(UUID uuid) {
        return playerRegistry.get(uuid).getName();
    }

    public void broadcast(String message) {
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            ChatManager chatManager = this.getPlayerRegistry().get(p.getUniqueId()).getChatManager();
            if (!(chatManager.isHidden())) {
                p.sendMessage(message);
            }
        }
    }

    public void broadcast(BaseComponent message) {
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            ChatManager chatManager = this.getPlayerRegistry().get(p.getUniqueId()).getChatManager();
            if (!(chatManager.isHidden())) {
                p.sendMessage(message);
            }
        }
    }

    private void registerListeners(Listener @NotNull ... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager()
                    .registerEvents(listener, this);
        }
    }

    private void registerDiscordListener(JDABuilder builder, EventListener @NotNull ... listeners) {
        for (EventListener listener : listeners) {
            builder.addEventListeners(listener);
        }
    }

    private void createDirectories(String @NotNull ... names) {
        for (String name : names) {
            File file = new File(getDataFolder(), name);
            file.mkdirs();
        }
    }

}
