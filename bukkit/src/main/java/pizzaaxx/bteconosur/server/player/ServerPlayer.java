package pizzaaxx.bteconosur.server.player;

import net.dv8tion.jda.api.entities.User;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.util.Tristate;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.worldedit.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static pizzaaxx.bteconosur.BteConoSur.playerRegistry;
import static pizzaaxx.bteconosur.ranks.PromoteDemote.lp;

public class ServerPlayer {

    private final UUID uuid;
    private final DataManager dataManager;
    private ChatManager chatManager;
    private PointsManager pointsManager;
    private ProjectsManager projectsManager;
    private GroupsManager groupsManager;
    private DiscordManager discordManager;
    private ScoreboardManager scoreboardManager;

    private final String actualCountry = "";

    // CONSTRUCTOR

    public ServerPlayer(UUID uuid, boolean storeManagers) {
        this.uuid = uuid;

        if (playerRegistry.exists(uuid)) {
            ServerPlayer origin = playerRegistry.get(uuid);
            this.dataManager = origin.getDataManager();

            if (origin.chatManager != null) {
                this.chatManager = origin.chatManager;
            }
            if (origin.projectsManager != null) {
                this.projectsManager = origin.projectsManager;
            }
            if (origin.scoreboardManager != null) {
                this.scoreboardManager = origin.scoreboardManager;
            }
            if (origin.groupsManager != null) {
                this.groupsManager = origin.groupsManager;
            }
            if (origin.pointsManager != null) {
                this.pointsManager = origin.pointsManager;
            }
            if (origin.discordManager != null) {
                this.discordManager = origin.discordManager;
            }

        } else {
            this.dataManager = new DataManager(this);
        }
        if (storeManagers) {
            loadManagers();
        }
    }

    public ServerPlayer(UUID uuid) {
        this(uuid, false);
    }

    public ServerPlayer(OfflinePlayer p) {
        this(p.getUniqueId());
    }

    public ServerPlayer(User user) throws Exception {
        this(getPlayerFromDiscordUser(user));
    }

    public static OfflinePlayer getPlayerFromDiscordUser(User user) throws Exception {
        if (DiscordManager.isLinked(user.getId())) {
            return DiscordManager.getFromID(user.getId());
        } else {
            throw new Exception();
        }
    }

    // MANAGERS

    public void loadManagers() {
        if (chatManager ==  null) {
            chatManager = new ChatManager(this);
        }
        if (projectsManager == null) {
            projectsManager = new ProjectsManager(this);
        }
        if (groupsManager == null) {
            groupsManager = new GroupsManager(this);
        }
        if (pointsManager == null) {
            pointsManager = new PointsManager(this);
        }
        if (discordManager == null) {
            discordManager = new DiscordManager(this);
        }
        if (scoreboardManager == null) {
            scoreboardManager = new ScoreboardManager(this);
        }
    }//x

    public ScoreboardManager getScoreboardManager() {
        if (scoreboardManager == null) {
            ScoreboardManager manager = new ScoreboardManager(this);
            scoreboardManager = manager;
            return manager;
        }
        return scoreboardManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public DiscordManager getDiscordManager() {
        if (discordManager == null) {
            DiscordManager manager = new DiscordManager(this);
            discordManager = manager;
            return manager;
        }
        return discordManager;
    }

    public ChatManager getChatManager() {
        if (chatManager == null) {
            ChatManager manager = new ChatManager(this);
            chatManager = manager;
            return manager;
        }
        return chatManager;
    }

    public ProjectsManager getProjectsManager() {
        if (projectsManager == null) {
            ProjectsManager manager = new ProjectsManager(this);
            projectsManager = manager;
            return manager;
        }
        return projectsManager;
    }

    public GroupsManager getGroupsManager() {
        if (groupsManager == null) {
            GroupsManager manager = new GroupsManager(this);
            groupsManager = manager;
            return manager;
        }
        return groupsManager;
    }


    public PointsManager getPointsManager() {
        if (pointsManager == null) {
            PointsManager manager = new PointsManager(this);
            pointsManager = manager;
            return manager;
        }
        return pointsManager;
    }

    /////////

    public UUID getId() {
        return uuid;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    // NAMES AND TEXTS

    public String getLore() {
        List<String> lines = new ArrayList<>();

        lines.add("-[ §a§l" + getName() + " §r]-");
        ProjectsManager projectsManager = getProjectsManager();
        lines.add("§aProyectos activos: §r" + projectsManager.getTotalProjects());
        lines.add("§aProyectos terminados: §r" + projectsManager.getTotalFinishedProjects());

        PointsManager pointsManager = getPointsManager();
        if (pointsManager.getMaxPoints() != null && pointsManager.getMaxPoints().getValue() != null) {
            lines.add("§aPuntos:§r");

            pointsManager.getSorted().forEach((country, points) -> lines.add("· " + StringUtils.capitalize(country.getName().replace("peru", "perú")) + ": " + pointsManager.getPoints(country)));

        }

        DiscordManager discordManager = getDiscordManager();
        if (discordManager.isLinked()) {
            lines.add("§aDiscord: §r" + discordManager.getName() + "#" + discordManager.getDiscriminator());
        }

        return String.join("\n", lines);
    }

    public String getLoreWithoutTitle() {
        List<String> lines = new ArrayList<>();

        ProjectsManager projectsManager = getProjectsManager();
        lines.add("§aProyectos activos: §r" + projectsManager.getTotalProjects());
        lines.add("§aProyectos terminados: §r" + projectsManager.getTotalFinishedProjects());

        PointsManager pointsManager = getPointsManager();
        if (pointsManager.getMaxPoints() != null && pointsManager.getMaxPoints().getValue() != null) {
            lines.add("§aPuntos:§r");

            pointsManager.getSorted().forEach((country, points) -> lines.add("· " + StringUtils.capitalize(country.getName().replace("peru", "perú")) + ": " + pointsManager.getPoints(country)));

        }

        DiscordManager discordManager = getDiscordManager();
        if (discordManager.isLinked()) {
            lines.add("§aDiscord: §r" + discordManager.getName() + "#" + discordManager.getDiscriminator());
        }

        return String.join("\n", lines);
    }

    public String getName() {
        return dataManager.getString("name");
    }

    public List<Tree> getTreeGroup(String name) {
        if (dataManager.contains("treegroups")) {
            ConfigurationSection treeGroups = dataManager.getConfigurationSection("treegroups");
            if (treeGroups.contains(name)) {
                List<Tree> trees = new ArrayList<>();
                treeGroups.getStringList(name).forEach(tree -> {
                    try {
                        trees.add(new Tree(tree));
                    } catch (Exception exception) {
                        Bukkit.getConsoleSender().sendMessage("No se pudo encontrar el árbol \"" + name + "\".");
                    }
                });
                return trees;
            }
            return null;
        }
        return null;
    }

    public List<String> getPermissionCountries() {
        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            Player p = Bukkit.getPlayer(uuid);
            List<String> permissionCountries = new ArrayList<>();
            if (p.hasPermission("bteconosur.projects.manage.country.ar")) {
                permissionCountries.add("argentina");
            }
            if (p.hasPermission("bteconosur.projects.manage.country.bo")) {
                permissionCountries.add("bolivia");
            }
            if (p.hasPermission("bteconosur.projects.manage.country.cl")) {
                permissionCountries.add("chile");
            }
            if (p.hasPermission("bteconosur.projects.manage.country.pe")) {
                permissionCountries.add("peru");
            }
            if (p.hasPermission("bteconosur.projects.manage.country.py")) {
                permissionCountries.add("paraguay");
            }
            if (p.hasPermission("bteconosur.projects.manage.country.uy")) {
                permissionCountries.add("uruguay");
            }
            return permissionCountries;
        } else {

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            UserManager userManager = lp.getUserManager();
            CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(uuid);

            List<String> permissionCountries = new ArrayList<>();
            userFuture.thenAccept(
                    user -> {
                        QueryOptions options = lp.getContextManager().getQueryOptions(player);
                        CachedPermissionData data = user.getCachedData().getPermissionData(options);

                        // TODO FIX THIS

                        if (data.checkPermission("bteconosur.projects.manage.country.ar").asBoolean()) {
                            permissionCountries.add("argentina");
                        }
                        if (data.checkPermission("bteconosur.projects.manage.country.bo").asBoolean()) {
                            permissionCountries.add("bolivia");
                        }
                        if (data.checkPermission("bteconosur.projects.manage.country.cl").asBoolean()) {
                            permissionCountries.add("chile");
                        }
                        if (data.checkPermission("bteconosur.projects.manage.country.pe").asBoolean()) {
                            permissionCountries.add("peru");
                        }
                        if (data.checkPermission("bteconosur.projects.manage.country.py").asBoolean()) {
                            permissionCountries.add("paraguay");
                        }
                        if (data.checkPermission("bteconosur.projects.manage.country.uy").asBoolean()) {
                            permissionCountries.add("uruguay");
                        }

                    }
            );
            permissionCountries.forEach(country -> Bukkit.getConsoleSender().sendMessage(country));
            return permissionCountries;
        }
    }

    // NOTIFICATIONS

    public void sendNotification(String message) {
        DiscordManager manager = getDiscordManager();
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if (p.isOnline()) {
            ((Player) p).sendMessage(message.replace("**", "").replace("`", ""));
        } else if (manager.isLinked()) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    manager.loadUser();
                    manager.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(ChatColor.stripColor("**Notificación:** " + message)).queue());
                }
            };
            runnable.runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("bteConoSur"));
        } else {
            List<String> notifications = dataManager.getStringList("notificaciones");
            notifications.add(message.replace("§", "&").replace("**", "").replace("`", ""));
            dataManager.set("notificaciones", notifications);
            dataManager.save();
        }
    }

    public List<String> getNotifications() {
        List<String> notifications = new ArrayList<>();
        dataManager.getStringList("notificaciones").forEach(notif -> notifications.add(notif.replace("&", "§")));
        return notifications;
    }
}
