package pizzaaxx.bteconosur;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.PeterMassmann.SQLManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.building.DivideCommand;
import pizzaaxx.bteconosur.building.worldedit.WorldEditConnector;
import pizzaaxx.bteconosur.countries.CountriesRegistry;
import pizzaaxx.bteconosur.discord.CommandHolder;
import pizzaaxx.bteconosur.discord.DiscordConnector;
import pizzaaxx.bteconosur.discord.LinkCommand;
import pizzaaxx.bteconosur.discord.LinkRegistry;
import pizzaaxx.bteconosur.events.JoinEvent;
import pizzaaxx.bteconosur.events.LoginEvent;
import pizzaaxx.bteconosur.events.PreLoginEvent;
import pizzaaxx.bteconosur.events.QuitEvent;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardManager;
import pizzaaxx.bteconosur.terra.TpllCommand;
import pizzaaxx.bteconosur.inventory.InventoryHandler;
import pizzaaxx.bteconosur.player.PlayerRegistry;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplay;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplayProvider;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.discord.DiscordConnector.BOT;
import static pizzaaxx.bteconosur.utils.ChatUtils.DARK_GRAY;
import static pizzaaxx.bteconosur.utils.ChatUtils.GRAY;

public class BTEConoSurPlugin extends JavaPlugin implements ScoreboardDisplayProvider, ScoreboardDisplay {

    //--- PREFIX ---
    public static final String PREFIX = "§7[§2CONO §aSUR§7] §8» §r";
    public static final Component PREFIX_C = LegacyComponentSerializer.legacyAmpersand().deserialize(PREFIX);

    //--- WORLD LAYERS ---
    private final World[] worlds = new World[2];
    public World[] getWorlds() {
        return worlds;
    }

    //--- WORLDGUARD ---
    private final WorldGuardPlugin worldGuardPlugin = WorldGuardPlugin.inst();
    public WorldGuardPlugin getWorldGuard() {
        return worldGuardPlugin;
    }

    //--- WORLDEDIT ---
    private final WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("worldedit");
    public WorldEditPlugin getWorldEdit() {
        return worldEditPlugin;
    }

    public static WorldEditConnector WORLDEDIT_CONNECTOR;

    //--- SQL ---
    private SQLManager sqlManager;
    public SQLManager getSqlManager() {
        return sqlManager;
    }

    //--- JSON ---
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ObjectMapper getJsonMapper() {
        return objectMapper;
    }

    //--- INVENTORY ---
    private final InventoryHandler inventoryHandler = new InventoryHandler(this);
    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    //--- PLAYER REGISTRY ---
    private PlayerRegistry playerRegistry;
    public PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    //--- SCOREBOARD ---
    private ScheduledTask updateAutoScoreboardsTask;

    //--- DISCORD ---
    private DiscordConnector discordConnector = new DiscordConnector(this);
    public DiscordConnector getDiscordHandler() {
        return discordConnector;
    }

    private LinkRegistry linkRegistry;
    public LinkRegistry getLinkRegistry() {
        return linkRegistry;
    }

    //--- COUNTRIES ---
    private CountriesRegistry countriesRegistry;
    public CountriesRegistry getCountriesRegistry() {
        return countriesRegistry;
    }

    @Override
    public void onEnable() {

        this.log("Initiating BuildTheEarth: Cono Sur");

        this.getDataFolder().mkdir();

        //--- LOAD WORLD LAYERS ---
        this.log("Loading worlds...");
        worlds[0] = Bukkit.createWorld(WorldCreator.name("BTE_CS_1"));
        worlds[1] = Bukkit.createWorld(WorldCreator.name("BTE_CS_2"));

        //--- WORLDEDIT ---
        this.log("Loading WorldEdit connector...");
        WORLDEDIT_CONNECTOR = new WorldEditConnector(this);

        //--- CONNECT TO SQL ---
        this.log("Connecting to database...");
        File databaseFile = new File(this.getDataFolder(), "database.json");
        try {
            JsonNode databaseNode = this.getJsonMapper().readTree(databaseFile);
            sqlManager = new SQLManager(
                    databaseNode.path("url").asText(),
                    databaseNode.path("username").asText(),
                    databaseNode.path("password").asText()
            );
        } catch (IOException e) {
            this.error("An error occurred while connecting to the database. Stopping plugin initialization.");
            return;
        }

        //--- SHARED STUFF ---
        LinkCommand linkCommand = new LinkCommand(this);

        //--- REGISTER COMMANDS ---
        this.log("Registering commands...");
        this.registerCommand("tpll", new TpllCommand(this));
        this.registerCommand("divide", new DivideCommand(this));
        this.registerCommand("link", linkCommand);

        //--- REGISTER LISTENERS ---
        this.log("Registering listeners...");
        this.registerListeners(
                this.inventoryHandler,
                new QuitEvent(this),
                new PreLoginEvent(this),
                new LoginEvent(this),
                new JoinEvent(this)
        );

        //--- PLAYER REGISTRY ---
        this.playerRegistry = new PlayerRegistry(this);

        //--- SCOREBOARDS ---
        PROVIDERS.put("server", this);
        AUTO_PROVIDERS.add("server");
        this.startAutoScoreboardsTimer();

        //--- DISCORD ---
        try {

            File discordFile = new File(this.getDataFolder(), "discord.json");
            String token = this.objectMapper.readTree(discordFile).path("token").asText();

            this.discordConnector.registerListeners(
                    linkCommand
            );

            this.discordConnector.startBot(token);
            this.linkRegistry = new LinkRegistry(this);

            List<CommandHolder> holders = new ArrayList<>();
            for (Object listener : BOT.getRegisteredListeners()) {
                if (listener instanceof CommandHolder holder) {
                    holders.add(holder);
                }
            }

            List<CommandData> datas = new ArrayList<>();
            for (CommandHolder holder : holders) {
                datas.addAll(Arrays.asList(holder.getCommandData()));
            }

            BOT.retrieveCommands().queue(
                    commands -> {
                        // CHECK OLD COMMANDS
                        for (Command command : commands) {
                            if (datas.stream().noneMatch(data -> data.getName().equals(command.getName()))) {
                                command.delete().queue();
                            }
                        }

                        // CHECK NEW COMMANDS
                        for (CommandData data : datas) {
                            if (commands.stream().noneMatch(command -> command.getName().equals(data.getName()))) {
                                BOT.upsertCommand(data).queue();
                            }
                        }

                        // UPDATE EXISTING
                        for (CommandData data : datas) {
                            for (Command command : commands) {
                                if (command.getName().equals(data.getName())) {
                                    if (!DiscordConnector.compareCommands(command, data)) {
                                        BOT.upsertCommand(data).queue();
                                    }
                                }
                            }
                        }
                    }
            );

        } catch (IOException | InterruptedException e) {
            this.error("An error occurred while starting the Discord bot. Stopping plugin initialization.");
            return;
        }

        //--- COUNTRIES ---
        this.countriesRegistry = new CountriesRegistry(this);
    }

    @Override
    public void onDisable() {
        this.updateAutoScoreboardsTask.cancel();
        this.discordConnector.stopBot();
    }

    public void log(@NotNull Object o) {
        Bukkit.getConsoleSender().sendMessage(PREFIX + o);
    }


    public void warn(@NotNull Object o) {
        Bukkit.getConsoleSender().sendMessage(PREFIX + "§e" + o);
    }


    public void error(@NotNull Object o) {
        Bukkit.getConsoleSender().sendMessage(PREFIX + "§c" + o);
    }

    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = this.getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
        }
    }

    private void registerListeners(Listener @NotNull ... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    public boolean canBuild(UUID uuid, double x, double z) {
        return true;
    }

    public World getWorld(double y) {
        if (y < 2032) {
            return this.getWorlds()[0];
        } else {
            return this.getWorlds()[1];
        }
    }

    @Override
    public Component getTitle() {
        return Component.text(StringUtils.transformToSmallCapital("Build"), TextColor.color(172, 92, 76))
                .append(Component.text(StringUtils.transformToSmallCapital("The"), TextColor.color(153, 153, 153)))
                .append(Component.text(StringUtils.transformToSmallCapital("E"), TextColor.color(77, 77, 177)))
                .append(Component.text(StringUtils.transformToSmallCapital("a"), TextColor.color(107, 148, 216)))
                .append(Component.text(StringUtils.transformToSmallCapital("r"), TextColor.color(237, 240, 246)))
                .append(Component.text(StringUtils.transformToSmallCapital("t"), TextColor.color(161, 194, 99)))
                .append(Component.text(StringUtils.transformToSmallCapital("h"), TextColor.color(217, 197, 154)))
                .append(Component.text(StringUtils.transformToSmallCapital(": "), TextColor.color(153, 153, 153)))
                .append(Component.text(StringUtils.transformToSmallCapital("Cono "), TextColor.color(53, 141, 47)))
                .append(Component.text(StringUtils.transformToSmallCapital("Sur"), TextColor.color(142, 222, 79)))
                .decorate(TextDecoration.BOLD);
    }

    @Override
    public List<Component> getLines() {
        return List.of(
                Component.text("◆")
                        .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                        .append(Component.text("◆"))
                        .color(TextColor.color(DARK_GRAY)),

                Component.text(StringUtils.transformToSmallCapital("  Jugadores"), Style.style(TextDecoration.BOLD)),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("Online: "), TextColor.color(GRAY)))
                        .append(Component.text(Bukkit.getOnlinePlayers().size(), TextColor.color(191, 242, 233))),
                Component.text("    ▪ ", Style.style(TextColor.color(DARK_GRAY)))
                        .append(Component.text(StringUtils.transformToSmallCapital("Registrados: "), TextColor.color(GRAY)))
                        .append(Component.text(this.playerRegistry.getIds().size(), TextColor.color(191, 242, 233))),

                Component.text("◆")
                        .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                        .append(Component.text("◆"))
                        .color(TextColor.color(DARK_GRAY))
        );
    }

    @Override
    public ScoreboardDisplayProvider getProvider() {
        return this;
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    @Override
    public ScoreboardDisplay getDisplay(Player player) {
        return this;
    }

    @Override
    public String getIdentifier() {
        return "server";
    }

    private void startAutoScoreboardsTimer() {
        this.updateAutoScoreboardsTask = Bukkit.getAsyncScheduler().runAtFixedRate(
                this,
                scheduledTask -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        OnlineServerPlayer s = (OnlineServerPlayer) this.getPlayerRegistry().get(player.getUniqueId());
                        ScoreboardManager manager = s.getScoreboardManager();
                        if (manager.isAuto()) {
                            ScoreboardDisplayProvider provider = ScoreboardDisplayProvider.getNext(manager.getType());
                            try {
                                manager.setDisplay(
                                        provider.getDisplay(player)
                                );
                            } catch (SQLException e) {
                                this.warn("Error updating scoreboard display. (UUID: " + player.getUniqueId() + ")");
                                e.printStackTrace();
                            }
                        }
                    }
                },
                0,
                5,
                TimeUnit.SECONDS
        );
    }
}
