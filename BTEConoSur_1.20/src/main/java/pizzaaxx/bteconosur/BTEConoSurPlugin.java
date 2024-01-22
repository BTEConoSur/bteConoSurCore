package pizzaaxx.bteconosur;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.PeterMassmann.SQLManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.differentiation.JacobianFunction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import pizzaaxx.bteconosur.building.DivideCommand;
import pizzaaxx.bteconosur.building.worldedit.IncrementCommand;
import pizzaaxx.bteconosur.building.worldedit.Shortcuts;
import pizzaaxx.bteconosur.building.worldedit.WorldEditConnector;
import pizzaaxx.bteconosur.countries.CountriesRegistry;
import pizzaaxx.bteconosur.discord.*;
import pizzaaxx.bteconosur.events.*;
import pizzaaxx.bteconosur.gui.inventory.InventoryHandler;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardCommand;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardManager;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.projects.ProjectCreationRequestListener;
import pizzaaxx.bteconosur.projects.ProjectsCommand;
import pizzaaxx.bteconosur.projects.ProjectsRegistry;
import pizzaaxx.bteconosur.protection.WorldEditListener;
import pizzaaxx.bteconosur.terra.TerraConnector;
import pizzaaxx.bteconosur.terra.TpdirCommand;
import pizzaaxx.bteconosur.terra.TpllCommand;
import pizzaaxx.bteconosur.player.PlayerRegistry;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplay;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplayProvider;
import pizzaaxx.bteconosur.test.TestCities;
import pizzaaxx.bteconosur.utilities.*;
import pizzaaxx.bteconosur.utils.SHPUtils;
import pizzaaxx.bteconosur.utils.SatMapHandler;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static pizzaaxx.bteconosur.discord.DiscordConnector.BOT;
import static pizzaaxx.bteconosur.utils.ChatUtils.*;

public class BTEConoSurPlugin extends JavaPlugin implements ScoreboardDisplayProvider, ScoreboardDisplay {

    //--- PREFIX ---
    public static final String PREFIX = "§7[§2CONO §aSUR§7] §8» §r";
    public static final Component PREFIX_C = LegacyComponentSerializer.legacyAmpersand().deserialize(PREFIX);

    //--- WORLD LAYERS ---
    private final World[] worlds = new World[2];
    public World[] getWorlds() {
        return worlds;
    }

    private final com.sk89q.worldedit.world.World[] weWorlds = new com.sk89q.worldedit.world.World[2];
    public com.sk89q.worldedit.world.World[] getWEWorlds() {
        return weWorlds;
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
    private BukkitTask updateAutoScoreboardsTask;

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

    //--- SHAPEFILE ---
    private final Map<String, Map<Integer, SimpleFeature>> shapefiles = new HashMap<>();
    public Map<Integer, SimpleFeature> getShapefile(String name) {
        return shapefiles.get(name);
    }

    //--- PROJECTS ---
    private ProjectsRegistry projectsRegistry;
    public ProjectsRegistry getProjectsRegistry() {
        return projectsRegistry;
    }

    //--- REGION LISTENER ---
    private final RegionListener regionListener = new RegionListener();
    public RegionListener getRegionListener() {
        return regionListener;
    }

    // --- PROTECTION ---
    private final PlayerClickEvent playerClickEvent = new PlayerClickEvent(this);
    public PlayerClickEvent getPlayerClickEvent() {
        return playerClickEvent;
    }

    // --- SATMAP ---
    private SatMapHandler satMapHandler;
    public SatMapHandler getSatMapHandler() {
        return satMapHandler;
    }

    @Override
    public void onEnable() {

        this.log("Initiating BuildTheEarth: Cono Sur");

        this.getDataFolder().mkdir();

        //--- LOAD WORLD LAYERS ---
        this.log("Loading worlds...");
        worlds[0] = Bukkit.createWorld(WorldCreator.name("BTE_CS_1"));
        worlds[1] = Bukkit.createWorld(WorldCreator.name("BTE_CS_2"));

        weWorlds[0] = BukkitAdapter.adapt(worlds[0]);
        weWorlds[1] = BukkitAdapter.adapt(worlds[1]);

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
            sqlManager.registerClassParser(
                    Polygon.class,
                    (polygon, insideJson) -> {
                        List<List<Integer>> coordinates = new ArrayList<>();
                        for (Coordinate coordinate : polygon.getCoordinates()) {
                            coordinates.add(
                                    List.of(
                                            (int) coordinate.getX(),
                                            (int) coordinate.getY()
                                    )
                            );
                        }

                        if (insideJson) {
                            return sqlManager.parse(coordinates);
                        } else {
                            return "PolygonFromText('POLYGON((" + coordinates.stream().map(coord -> coord.get(0) + " " + coord.get(1)).collect(Collectors.joining(",")) + "))')";
                        }
                    }
            );
        } catch (IOException | SQLException e) {
            this.error("An error occurred while connecting to the database. Stopping plugin initialization.");
            return;
        }

        //--- SHARED STUFF ---
        LinkCommand linkCommand = new LinkCommand(this);
        UnlinkCommand unlinkCommand = new UnlinkCommand(this);
        ProjectsCommand projectsCommand = new ProjectsCommand(this);

        //--- REGISTER COMMANDS ---
        this.log("Registering commands...");
        this.registerCommand("tpll", new TpllCommand(this));
        this.registerCommand("divide", new DivideCommand(this));
        this.registerCommand("link", linkCommand);
        this.registerCommand("unlink", unlinkCommand);
        this.registerCommand("tpdir", new TpdirCommand(this));
        this.registerCommand("sc", new ScoreboardCommand(this));
        this.registerCommand("project", projectsCommand);
        this.registerCommand("nightvision", new NightVisionCommand());
        this.registerCommand("reloadplayer", new ReloadPlayerCommand(this));
        this.registerCommand("height", new HeightCommand());
        this.registerCommand("increment", new IncrementCommand(this));
        this.registerCommand("enderchest", new EnderChestCommand());
        this.registerCommand("hat", new HatCommand());
        this.registerCommand("back", new BackCommand());
        this.registerCommand("clearinventory", new ClearInventoryCommand());
        this.registerCommand("jump", new JumpCommand());

        //--- REGISTER LISTENERS ---
        this.log("Registering listeners...");
        this.registerListeners(
                new QuitEvent(this),
                new PreLoginEvent(this),
                new LoginEvent(this),
                new JoinEvent(this),
                new Shortcuts(this),
                new TestCities(this),
                new TeleportEvent(),
                projectsCommand,
                inventoryHandler,
                playerClickEvent
        );

        //--- PLAYER REGISTRY ---
        this.log("Loading player registry...");
        this.playerRegistry = new PlayerRegistry(this);

        //--- SATMAP ---
        this.log("Loading SatMap handler...");
        try {
            this.satMapHandler = new SatMapHandler(this);
        } catch (IOException e) {
            this.error("An error occurred while loading the SatMap handler. Stopping plugin initialization.");
            return;
        }

        //--- SHAPEFILES ---
        this.log("Loading shapefiles...");
        try {
            shapefiles.put("argentina", SHPUtils.getCityFeatures(this, "argentina"));
            shapefiles.put("bolivia", SHPUtils.getCityFeatures(this, "bolivia"));
            shapefiles.put("chile", SHPUtils.getCityFeatures(this, "chile"));
            shapefiles.put("paraguay", SHPUtils.getCityFeatures(this, "paraguay"));
            shapefiles.put("peru", SHPUtils.getCityFeatures(this, "peru"));
            shapefiles.put("uruguay", SHPUtils.getCityFeatures(this, "uruguay"));
        } catch (IOException e) {
            this.error("An error occurred while loading shapefiles. Stopping plugin initialization.");
            return;
        }

        //--- COUNTRIES ---
        this.log("Loading countries registry...");
        this.countriesRegistry = new CountriesRegistry(this);

        //--- DISCORD ---
        this.log("Starting Discord bot...");
        try {

            File discordFile = new File(this.getDataFolder(), "discord.json");
            String token = this.objectMapper.readTree(discordFile).path("token").asText();

            this.discordConnector.registerListeners(
                    linkCommand,
                    unlinkCommand,
                    new ProjectCreationRequestListener(this)
            );

            this.discordConnector.startBot(token);
            this.linkRegistry = new LinkRegistry(this);

            List<DiscordCommandHolder> holders = new ArrayList<>();
            for (Object listener : BOT.getRegisteredListeners()) {
                if (listener instanceof DiscordCommandHolder holder) {
                    holders.add(holder);
                }
            }

            List<CommandData> datas = new ArrayList<>();
            for (DiscordCommandHolder holder : holders) {
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
        this.log("Discord bot started successfully.");

        //--- PROJECTS ---
        this.log("Loading projects registry...");
        try {
            this.projectsRegistry = new ProjectsRegistry(this);
            this.projectsRegistry.init();
            this.regionListener.registerRegionEnterListener(
                    id -> id.startsWith("project_"),
                    (id, uuid) -> {
                        String projectId = id.replace("project_", "");
                        Project project = this.projectsRegistry.get(projectId);
                        if (project != null) {
                            Player player = Bukkit.getPlayer(uuid);
                            assert player != null;
                            player.sendActionBar(
                                    Component.text(StringUtils.transformToSmallCapital(project.getDisplayName()), Style.style(TextColor.color(project.getType().getColor().getRGB())))
                                            .append(Component.text(" - ", Style.style(TextColor.color(DARK_GRAY))))
                                            .append(
                                                    (project.isClaimed()
                                                            ? Component.text(StringUtils.transformToSmallCapital(this.playerRegistry.get(project.getOwner()).getName()), Style.style(TextColor.color(GRAY)))
                                                            : Component.text(StringUtils.transformToSmallCapital("Disponible"), Style.style(TextColor.color(GREEN)))
                                                    )
                                            )
                            );
                            OnlineServerPlayer serverPlayer = (OnlineServerPlayer) this.playerRegistry.get(uuid);
                            ScoreboardManager manager = serverPlayer.getScoreboardManager();
                            try {
                                if (manager.getType().equals("project")) manager.setDisplay(project);
                            } catch (SQLException ignored) {}
                        }
                    }
            );
            this.regionListener.registerRegionLeaveListener(
                    id -> id.startsWith("project_"),
                    (id, uuid) -> {
                        OnlineServerPlayer serverPlayer = (OnlineServerPlayer) this.playerRegistry.get(uuid);
                        ScoreboardManager manager = serverPlayer.getScoreboardManager();
                        try {
                            if (manager.getType().equals("project")) manager.setDisplay(projectsRegistry.getDisplay(serverPlayer.getPlayer()));
                        } catch (SQLException ignored) {}
                    }
            );
            this.regionListener.registerRegionEnterListener(
                    id -> id.startsWith("city_"),
                    (id, uuid) -> {
                        String[] split = id.split("_");
                        String countryName = split[1];
                        int cityId = Integer.parseInt(split[2]);
                        Player player = Bukkit.getPlayer(uuid);
                        assert player != null;
                        player.sendActionBar(
                                Component.text(StringUtils.transformToSmallCapital("¡Bienvenido a "), Style.style(TextColor.color(GRAY)))
                                        .append(Component.text(StringUtils.transformToSmallCapital(this.countriesRegistry.get(countryName).get(cityId).getName()), Style.style(TextColor.color(GREEN), TextDecoration.BOLD)))
                                        .append(Component.text("!", Style.style(TextColor.color(GRAY))))
                        );
                    }
            );
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            this.error("An error occurred while loading the projects registry. Stopping plugin initialization.");
            return;
        }

        //--- SCOREBOARDS ---
        PROVIDERS.put("server", this);
        PROVIDERS.put("project", this.projectsRegistry);
        AUTO_PROVIDERS.add("server");
        AUTO_PROVIDERS.add("project");
        this.startAutoScoreboardsTimer();

        this.getWorldEdit().getWorldEdit().getEventBus().register(new WorldEditListener(this));

        this.registerListeners(regionListener);

        for (Player player : Bukkit.getOnlinePlayers()) {
            OnlineServerPlayer s = (OnlineServerPlayer) this.getPlayerRegistry().get(player.getUniqueId());
            s.getScoreboardManager().startBoard();
            this.playerClickEvent.registerProtector(player.getUniqueId());
        }

    }

    @Override
    public void onDisable() {
        this.updateAutoScoreboardsTask.cancel();
        try {
            this.discordConnector.stopBot();
        } catch (InterruptedException e) {
            this.error("An error occurred while stopping the Discord bot.");
        }
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

    public void registerListeners(Listener @NotNull ... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    public boolean canBuild(UUID uuid, double x, double z) {
        return projectsRegistry.canBuildAt(uuid, x, z);
    }

    public World getWorld(double y) {
        if (y < 2032) {
            return this.getWorlds()[0];
        } else {
            return this.getWorlds()[1];
        }
    }

    public com.sk89q.worldedit.world.World getWEWorld(double y) {
        if (y < 2032) {
            return this.getWEWorlds()[0];
        } else {
            return this.getWEWorlds()[1];
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
        this.updateAutoScoreboardsTask = Bukkit.getScheduler().runTaskTimer(
                this,
                () -> {
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
                5 * 20
        );
    }

    /**
     * Teleports a player to the specified coordinates. This method won't wait until the terrain is generated.
     * @param player The player to teleport
     * @param x The X coordinate
     * @param z The Z coordinate
     */
    public void teleportForced(@NotNull Player player, double x, double z) {
        double height = TerraConnector.getHeight((int) x, (int) z).join();
        World world = this.getWorld(height);
        double finalHeight = world.getHighestBlockYAt((int) x, (int) z);
        player.teleportAsync(
                new Location(
                        world,
                        x,
                        finalHeight,
                        z
                )
        );
    }

    /**
     * Teleports a player to the specified coordinates. This method will wait until the terrain is generated.
     * @param player The player to teleport
     * @param x The X coordinate
     * @param z The Z coordinate
     * @param loadingMessage The message to send while the terrain is loading
     * @param successMessage The message to send when the player is teleported
     */
    public void teleportAsync(
            Player player,
            double x,
            double z,
            @Nullable String loadingMessage,
            @Nullable String successMessage
    ) {
        CompletableFuture<Double> heightFuture = TerraConnector.getHeight((int) x, (int) z);
        heightFuture.thenAccept(height -> {
            World world = this.getWorld(height);
            double finalHeight = world.getHighestBlockYAt((int) x, (int) z);

            Location location = new Location(
                    world,
                    x,
                    finalHeight,
                    z
            );

            if (!location.isChunkLoaded()) {
                if (loadingMessage != null) player.sendMessage(loadingMessage);
            }
            player.teleportAsync(
                    location
            ).whenCompleteAsync(
                    (a, b) -> {
                        if (successMessage != null) player.sendMessage(successMessage);
                    }
            );
        });
    }
}
