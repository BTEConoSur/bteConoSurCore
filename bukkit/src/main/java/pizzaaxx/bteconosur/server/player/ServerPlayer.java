package pizzaaxx.bteconosur.server.player;

import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pizzaaxx.bteconosur.worldedit.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static pizzaaxx.bteconosur.BteConoSur.playerRegistry;
import static pizzaaxx.bteconosur.country.OldCountry.countryNames;

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
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (dataManager.contains("projectsManageCountries")) {
            return dataManager.getStringList("projectsManageCountries");
        }

        return new ArrayList<>();
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
        if (dataManager.contains("notificaciones")) {
            dataManager.getStringList("notificaciones").forEach(notif -> notifications.add(notif.replace("&", "§")));
        }
        return notifications;
    }
}
