package pizzaaxx.bteconosur;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pizzaaxx.bteconosur.chats.ChatCommand;
import pizzaaxx.bteconosur.chats.ChatRegistry;
import pizzaaxx.bteconosur.chats.Events;
import pizzaaxx.bteconosur.country.cities.CityRegistry;
import pizzaaxx.bteconosur.commands.HelpCommand;
import pizzaaxx.bteconosur.commands.ScoreboardCommand;
import pizzaaxx.bteconosur.commands.*;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.CountryManager;
import pizzaaxx.bteconosur.discord.fuzzyMatching.FuzzyMatchListenerHandler;
import pizzaaxx.bteconosur.discord.fuzzyMatching.listeners.BedrockListener;
import pizzaaxx.bteconosur.discord.fuzzyMatching.listeners.IPListener;
import pizzaaxx.bteconosur.discord.fuzzyMatching.listeners.PremiumListener;
import pizzaaxx.bteconosur.discord.slashCommands.*;
import pizzaaxx.bteconosur.discord.slashCommands.link.LinkUnlinkCommand;
import pizzaaxx.bteconosur.discord.slashCommands.link.LinkUnlinkMinecraftCommand;
import pizzaaxx.bteconosur.events.EventsCommand;
import pizzaaxx.bteconosur.helper.Pair;
import pizzaaxx.bteconosur.item.ItemBuilder;
import pizzaaxx.bteconosur.join.Join;
import pizzaaxx.bteconosur.listener.AsyncPlayerPreLoginListener;
import pizzaaxx.bteconosur.listener.ProjectBlockPlacingListener;
import pizzaaxx.bteconosur.misc.Security;
import pizzaaxx.bteconosur.projects.*;
import pizzaaxx.bteconosur.ranks.Donator;
import pizzaaxx.bteconosur.ranks.PromoteDemote;
import pizzaaxx.bteconosur.ranks.Streamer;
import pizzaaxx.bteconosur.server.player.ChatManager;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;
import pizzaaxx.bteconosur.server.player.ScoreboardManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;
import pizzaaxx.bteconosur.teleport.OnTeleport;
import pizzaaxx.bteconosur.testing.Fixing;
import pizzaaxx.bteconosur.testing.ReloadPlayer;
import pizzaaxx.bteconosur.testing.Testing;
import pizzaaxx.bteconosur.worldedit.*;
import pizzaaxx.bteconosur.worldguard.MovementHandler;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.country.OldCountry.countryNames;
import static pizzaaxx.bteconosur.discord.Bot.conoSurBot;
import static pizzaaxx.bteconosur.projects.ProjectsCommand.background;
import static pizzaaxx.bteconosur.ranks.PromoteDemote.lp;

public final class BteConoSur extends JavaPlugin {

    public static Map<String, CityRegistry> cityRegistries = new HashMap<>();

    public static World mainWorld = null;
    public static File pluginFolder = null;
    public static String key;
    public static ProjectRegistry projectRegistry;
    public static final ChatRegistry chatRegistry = new ChatRegistry();
    public static final Map<String, Guild> guilds = new HashMap<>();
    public static final Map<String, Map<String, String>> countryRoles = new HashMap<>();

    private final Configuration links = new Configuration(this, "link/links");

    private final CountryManager countryManager = new CountryManager(this);

    private final Map<String, Pair<String, String>> projectsMapping = new HashMap<>();

    private final PlayerRegistry playerRegistry = new PlayerRegistry(this);

    public PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    private final RegionManager regionsManager = WorldGuardPlugin.inst().getRegionManager(mainWorld);

    public RegionManager getRegionsManager() {
        return regionsManager;
    }

    @Override
    public void onEnable() {

        getLogger().info("Enabling  BTE Cono Sur!");

        SelectionCommands selectionCommands = new SelectionCommands(this);

        WhereCommand whereCommand = new WhereCommand(this);
        registerListeners(
                new Join(playerRegistry, this),
                new ProjectActionBar(),
                new OnTeleport(),
                new PresetsCommand(),
                new PFindCommand(),
                new ShortCuts(playerRegistry, selectionCommands),
                new Events(),
                new ScoreboardCommand(),
                new GetCommand(),
                new PrefixCommand(),
                new LobbyCommand(this),
                new EventsCommand(),
                new ProjectManageInventoryListener(this),
                new AsyncPlayerPreLoginListener(playerRegistry, this),
                new MovementHandler(),
                new ProjectBlockPlacingListener(),
                new Security(),
                selectionCommands,
                whereCommand
        );

        getLogger().info("Registering commands...");
        getCommand("/divide").setExecutor(new DivideCommand());
        getCommand("project").setExecutor(new ProjectsCommand());
        getCommand("nightvision").setExecutor(new NightVisionCommand());
        getCommand("promote").setExecutor(new PromoteDemote());
        getCommand("prefix").setExecutor(new PrefixCommand());
        getCommand("chat").setExecutor(new ChatCommand());
        getCommand("nickname").setExecutor(new NickNameCommand());
        getCommand("testing").setExecutor(new Testing());
        getCommand("demote").setExecutor(new PromoteDemote());
        getCommand("project").setTabCompleter(new TabCompletions());
        getCommand("presets").setExecutor(new PresetsCommand());
        getCommand("googlemaps").setExecutor(new GoogleMapsCommand());
        getCommand("increment").setExecutor(new IncrementCommand(playerRegistry));
        getCommand("pwarp").setExecutor(new PWarpCommand());
        getCommand("/polywalls").setExecutor(new Polywall());
        getCommand("donator").setExecutor(new Donator());
        getCommand("streamer").setExecutor(new Streamer());
        getCommand("streaming").setExecutor(new StreamingCommand());
        getCommand("get").setExecutor(new GetCommand());
        getCommand("scoreboard").setExecutor(new ScoreboardCommand());
        getCommand("tpdir").setExecutor(new TpDirCommand());
        getCommand("event").setExecutor(new EventsCommand());
        getCommand("lobby").setExecutor(new LobbyCommand(this));
        getCommand("assets").setExecutor(new LobbyCommand(this));
        getCommand("event").setExecutor(new EventsCommand());
        getCommand("manageevent").setExecutor(new EventsCommand());
        getCommand("help").setExecutor(new HelpCommand(new Configuration(this, "help")));
        TerraformCommand terraformExecutor = new TerraformCommand();
        getCommand("terraform").setExecutor(terraformExecutor);
        getCommand("welcomeBook").setExecutor(new Join(playerRegistry, this));
        getCommand("banner").setExecutor(new BannersCommand());
        getCommand("height").setExecutor(new HeightCommand());
        getCommand("fix").setExecutor(new Fixing(this));
        getCommand("/selundo").setExecutor(selectionCommands);
        getCommand("/selredo").setExecutor(selectionCommands);
        getCommand("reloadPlayer").setExecutor(new ReloadPlayer());


        pluginFolder = Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder();
        mainWorld = Bukkit.getWorld("BTECS");

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

        // DISCORD BOT
        JDABuilder builder = JDABuilder.createDefault(new Configuration(this, "discord/token").getString("token"));
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
                new RequestResponse(),
                new Events(),
                new OnlineCommand(),
                whereCommand,
                new pizzaaxx.bteconosur.discord.slashCommands.ModsCommand(),
                new pizzaaxx.bteconosur.discord.slashCommands.SchematicCommand(),
                new LinkUnlinkCommand(links, this),
                new pizzaaxx.bteconosur.discord.slashCommands.ProjectCommand(),
                new PlayerCommand(new Configuration(this, "discord/groupEmojis")),
                new pizzaaxx.bteconosur.discord.slashCommands.ScoreboardCommand(),
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
            conoSurBot = builder.build().awaitReady();
            getCommand("btecsUpdateSlashCommands").setExecutor(new UpdateSlashCommands(conoSurBot));
            getCommand("link").setExecutor(new LinkUnlinkMinecraftCommand(conoSurBot, this));
            getCommand("unlink").setExecutor(new LinkUnlinkMinecraftCommand(conoSurBot, this));
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        Configuration guildsSection = new Configuration(this, "discord/guilds");
        countryNames.forEach(name -> guilds.put(name, conoSurBot.getGuildById(guildsSection.getString(name))));

        Configuration configuration = new Configuration(this, "config");
        Config config = new Config(configuration);
        getCommand("btecs_reload").setExecutor(config);

        key = new Configuration(this, "key").getString("key");

        // LUCKPERMS

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lp = provider.getProvider();
        }

        EmbedBuilder online = new EmbedBuilder();
        online.setColor(new Color(0, 255, 42));
        online.setTitle("¡El servidor ya está online!");
        online.setDescription("\uD83D\uDD17 **IP:** bteconosur.com");

        gateway.sendMessageEmbeds(online.build()).queue();

        chatRegistry.register("global");
        chatRegistry.register("argentina");
        chatRegistry.register("bolivia");
        chatRegistry.register("chile");
        chatRegistry.register("paraguay");
        chatRegistry.register("peru");
        chatRegistry.register("uruguay");

        getLogger().info("Starting automatic scoreboards checker sequence...");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ScoreboardManager::checkAutoScoreboards, 300, 300);

        projectRegistry = new ProjectRegistry(this);

        getLogger().info("Loading projects requests...");

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerRegistry.add(new ServerPlayer(player));
            ServerPlayer s = playerRegistry.get(player.getUniqueId());
            ChatManager manager = s.getChatManager();
            manager.setChat(manager.getChat().getName());
        }

        Configuration roles = new Configuration(this, "countryRoles");
        for (String key : roles.getKeys(false)) {

            ConfigurationSection section = roles.getConfigurationSection(key);

            Map<String, String> rolesById = new HashMap<>();
            for (String name : section.getKeys(false)) {

                rolesById.put(name, section.getString(name));
            }

            countryRoles.put(key, rolesById);
        }

        File countriesFolder = new File(this.getDataFolder(), "countries");
        File[] countries = countriesFolder.listFiles();

        for (File country : countries) {

            File citiesFolder = new File(country, "cities");
            File[] cities = citiesFolder.listFiles();

            for (File city : cities) {

                File projectsFolder = new File(city, "projects");
                File[] projects = projectsFolder.listFiles();

                for (File project : projects) {

                    String id = project.getName().replace(".yml", "");
                    String cityName = city.getName();
                    String countryName = country.getName();

                    projectsMapping.put(id, new Pair<>(countryName, cityName));

                }

            }

        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling  BTE Cono Sur!");

        EmbedBuilder online = new EmbedBuilder();
        online.setColor(new Color(255, 0, 0));
        online.setTitle("El servidor ha sido apagado.");
        online.setDescription("Te esperamos cuando vuelva a estar disponible.");

        gateway.sendMessageEmbeds(online.build()).queue();

        conoSurBot.shutdown();


    }

    public static void broadcast(String message) {
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            ChatManager chatManager = new ServerPlayer(p).getChatManager();
            if (!(chatManager.isHidden())) {
                p.sendMessage(message);
            }
        }
    }

    public static void broadcast(BaseComponent message) {
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            ChatManager chatManager = new ServerPlayer(p).getChatManager();
            if (!(chatManager.isHidden())) {
                p.sendMessage(message);
            }
        }
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager()
                    .registerEvents(listener, this);
        }
    }

    private void registerDiscordListener(JDABuilder builder, EventListener... listeners) {
        for (EventListener listener : listeners) {
            builder.addEventListeners(listener);
        }
    }

    private void createDirectories(String... names) {
        for (String name : names) {
            File file = new File(getDataFolder(), name);
            file.mkdirs();
        }
    }

}
