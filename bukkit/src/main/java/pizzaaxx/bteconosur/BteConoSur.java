package pizzaaxx.bteconosur;

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
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pizzaaxx.bteconosur.chats.ChatCommand;
import pizzaaxx.bteconosur.chats.ChatRegistry;
import pizzaaxx.bteconosur.chats.Events;
import pizzaaxx.bteconosur.commands.*;
import pizzaaxx.bteconosur.country.CountryRegistry;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.discord.commands.*;
import pizzaaxx.bteconosur.discord.slashCommands.OnlineCommand;
import pizzaaxx.bteconosur.discord.slashCommands.PlayerCommand;
import pizzaaxx.bteconosur.discord.slashCommands.WhereCommand;
import pizzaaxx.bteconosur.discord.slashCommands.link.LinkUnlinkCommand;
import pizzaaxx.bteconosur.discord.slashCommands.link.LinkUnlinkMinecraftCommand;
import pizzaaxx.bteconosur.events.EventsCommand;
import pizzaaxx.bteconosur.item.ItemBuilder;
import pizzaaxx.bteconosur.join.Join;
import pizzaaxx.bteconosur.listener.AsyncPlayerPreLoginListener;
import pizzaaxx.bteconosur.points.Scoreboard;
import pizzaaxx.bteconosur.presets.PresetsCommand;
import pizzaaxx.bteconosur.presets.PresetsEvent;
import pizzaaxx.bteconosur.projects.*;
import pizzaaxx.bteconosur.ranks.Donator;
import pizzaaxx.bteconosur.ranks.PrefixCommand;
import pizzaaxx.bteconosur.ranks.PromoteDemote;
import pizzaaxx.bteconosur.ranks.Streamer;
import pizzaaxx.bteconosur.server.player.ChatManager;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;
import pizzaaxx.bteconosur.server.player.ScoreboardManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;
import pizzaaxx.bteconosur.teleport.OnTeleport;
import pizzaaxx.bteconosur.teleport.PWarp;
import pizzaaxx.bteconosur.testing.Testing;
import pizzaaxx.bteconosur.worldedit.IncrementCommand;
import pizzaaxx.bteconosur.worldedit.Polywall;
import pizzaaxx.bteconosur.worldedit.ShortCuts;
import pizzaaxx.bteconosur.worldedit.TerraformCommand;
import pizzaaxx.bteconosur.worldguard.MovementHandler;
import pizzaaxx.bteconosur.yaml.Configuration;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.country.OldCountry.countryNames;
import static pizzaaxx.bteconosur.discord.Bot.conoSurBot;
import static pizzaaxx.bteconosur.projects.ProjectsCommand.background;
import static pizzaaxx.bteconosur.projects.ProjectsCommand.projectRequestsIDs;
import static pizzaaxx.bteconosur.ranks.PromoteDemote.lp;

public final class BteConoSur extends JavaPlugin {

    public static World mainWorld = null;
    public static File pluginFolder = null;
    public static String key;
    public static PlayerRegistry playerRegistry = new PlayerRegistry();
    public static ProjectRegistry projectRegistry;
    public static ChatRegistry chatRegistry = new ChatRegistry();
    public static Map<OldCountry, Guild> guilds = new HashMap<>();

    private final Configuration links = new Configuration(this, "link/links");

    private CountryRegistry countryRegistry;
    private Configuration configurationCountries;

    @Override
    public void onEnable() {

        getLogger().info("Enabling  BTE Cono Sur!");

        registerListeners(
                new Join(playerRegistry),
                new ProjectActionBar(),
                new OnTeleport(),
                new PresetsEvent(),
                new pFind(),
                new PresetsEvent(),
                new ShortCuts(playerRegistry),
                new Events(),
                new Scoreboard(),
                new GetCommand(),
                new PrefixCommand(),
                new LobbyCommand(this),
                new EventsCommand(),
                new ProjectManageInventoryListener(this),
                new AsyncPlayerPreLoginListener(playerRegistry, this),
                new MovementHandler()
        );

        getLogger().info("Registering commands...");
        getCommand("project").setExecutor(new ProjectsCommand());
        getCommand("nightvision").setExecutor(new NightVisionCommand());
        getCommand("promote").setExecutor(new PromoteDemote());
        getCommand("prefix").setExecutor(new PrefixCommand());
        getCommand("chat").setExecutor(new ChatCommand());
        getCommand("nickname").setExecutor(new NickNameCommand());
        getCommand("test").setExecutor(new Testing(this));
        getCommand("demote").setExecutor(new PromoteDemote());
        getCommand("project").setTabCompleter(new TabCompletions());
        getCommand("presets").setExecutor(new PresetsCommand());
        getCommand("googlemaps").setExecutor(new GoogleMapsCommand());
        getCommand("increment").setExecutor(new IncrementCommand(playerRegistry));
        getCommand("pwarp").setExecutor(new PWarp());
        getCommand("/polywalls").setExecutor(new Polywall());
        getCommand("treegroup").setExecutor(new pizzaaxx.bteconosur.worldedit.trees.Events());
        getCommand("/treecover").setExecutor(new pizzaaxx.bteconosur.worldedit.trees.Events());
        getCommand("donator").setExecutor(new Donator());
        getCommand("streamer").setExecutor(new Streamer());
        getCommand("streaming").setExecutor(new StreamingCommand());
        getCommand("get").setExecutor(new GetCommand());
        getCommand("scoreboard").setExecutor(new Scoreboard());
        getCommand("tpdir").setExecutor(new TpDirCommand());
        getCommand("event").setExecutor(new EventsCommand());
        getCommand("lobby").setExecutor(new LobbyCommand(this));
        getCommand("assets").setExecutor(new LobbyCommand(this));
        getCommand("event").setExecutor(new EventsCommand());
        getCommand("manageevent").setExecutor(new EventsCommand());
        getCommand("help").setExecutor(new HelpCommand(this));
        TerraformCommand terraformExecutor = new TerraformCommand();
        getCommand("terraform").setExecutor(terraformExecutor);
        getCommand("/terraform").setExecutor(terraformExecutor);

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

        registerDiscordListener(builder,
                new RequestResponse(),
                new Events(),
                new ScoreboardCommand(),
                new OnlineCommand(),
                new WhereCommand(),
                new pizzaaxx.bteconosur.discord.slashCommands.ModsCommand(),
                new pizzaaxx.bteconosur.discord.slashCommands.SchematicCommand(),
                new LinkUnlinkCommand(links, this),
                new pizzaaxx.bteconosur.discord.slashCommands.ProjectCommand(),
                new PlayerCommand(new Configuration(this, "discord/groupEmojis")),
                new pizzaaxx.bteconosur.discord.slashCommands.ScoreboardCommand()
        );

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
        countryNames.forEach(name -> guilds.put(new OldCountry(name), conoSurBot.getGuildById(guildsSection.getString(name))));

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

        countryRegistry = new CountryRegistry();
        configurationCountries = new Configuration(this, "countries.yml");

        /*
        configurationCountries.getConfigurationSection("countries")
                        .getKeys(false)
                                .forEach(path -> {
                                    ConfigurationSection section = configurationCountries.getConfigurationSection(path);

                                    countryRegistry.add(
                                            new Country(
                                                    section.getString("name"),
                                                    section.getString("icon"),
                                                    section.getString("prefix"),
                                                    section.getString("request_channel_id"),
                                                    section.getString("abbreviation"),
                                                    section.getString("guild_id")
                                            )
                                    );
                                });
        */

        getLogger().info("Starting automatic scoreboards checker sequence...");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ScoreboardManager::checkAutoScoreboards, 300, 300);

        projectRegistry = new ProjectRegistry(this);

        getLogger().info("Loading projects requests...");

        Configuration requestsIDs = new Configuration(this, "projectsRequests");
        for (String requestID : requestsIDs.getKeys(false)) {
            projectRequestsIDs.put(requestID, requestsIDs.getString(requestID));
            requestsIDs.set(requestID, null);
        }
        requestsIDs.save();

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerRegistry.add(new ServerPlayer(player));
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling  BTE Cono Sur!");

        EmbedBuilder online = new EmbedBuilder();
        online.setColor(new Color(255, 0, 0));
        online.setTitle("El servidor ha sido apagado.");
        online.setDescription("Te esperamos cuando vuelva a estar disponible.");

        if (!projectRequestsIDs.isEmpty()) {
            Configuration requestsIDs = new Configuration(this, "projectsRequests");
            requestsIDs.set("", projectRequestsIDs);
            requestsIDs.save();
        }

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
