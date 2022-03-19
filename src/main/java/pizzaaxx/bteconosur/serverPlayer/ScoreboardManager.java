package pizzaaxx.bteconosur.serverPlayer;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.projects.Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class ScoreboardManager {

    public static List<ScoreboardType> scoreboardsOrder = Arrays.asList(ScoreboardType.SERVER, ScoreboardType.ME, ScoreboardType.PROJECT, ScoreboardType.TOP);
    private final ServerPlayer serverPlayer;
    private final DataManager data;
    private final ConfigurationSection scoreboard;
    private ScoreboardType type;
    private boolean auto;
    private boolean hidden;

    public static void checkAutoScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ServerPlayer s = new ServerPlayer(player);
            ScoreboardManager manager = s.getScoreboardManager();
            if (manager.isAuto()) {
                if (scoreboardsOrder.indexOf(manager.getType()) + 1 != scoreboardsOrder.size()) {
                    manager.setType(scoreboardsOrder.get(scoreboardsOrder.indexOf(manager.getType()) + 1));
                } else {
                    manager.setType(scoreboardsOrder.get(0));
                }
            }
        }
    }

    public enum ScoreboardType {
        ME, PROJECT, SERVER, TOP
    }

    public ScoreboardManager(ServerPlayer s) {
        serverPlayer = s;
        data = s.getDataManager();

        if (data.contains("scoreboard")) {
            scoreboard = data.getConfigurationSection("scoreboard");
            type = ScoreboardType.valueOf((scoreboard.getString("type", ScoreboardType.SERVER.toString())).toUpperCase());
            auto = scoreboard.getBoolean("auto", true);
            hidden = scoreboard.getBoolean("hidden", false);
        } else {
            scoreboard = data.createSection("scoreboard");
            type = ScoreboardType.SERVER;
            auto = true;
            hidden = false;
        }

    }

    public void update() {
        Player p = (Player) serverPlayer.getPlayer();
        if (!hidden) {
            List<String> lines = new ArrayList<>();
            String title = null;

            switch (type) {
                case SERVER:
                    title = "§3§lBTE Cono Sur";

                    lines.add("§aIP: §fbteconosur.com");
                    lines.add(" ");

                    lines.add("§aJugadores: §f" + Bukkit.getOnlinePlayers().size() + "/20");

                    int arg = 0;
                    int bol = 0;
                    int chi = 0;
                    int par = 0;
                    int per = 0;
                    int uru = 0;
                    RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Set<ProtectedRegion> regions = regionManager.getApplicableRegions(player.getLocation()).getRegions();
                        if (regions.contains(regionManager.getRegion("argentina"))) {
                            arg++;
                        } else if (regions.contains(regionManager.getRegion("bolivia"))) {
                            bol++;
                        } else if (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp"))) {
                            chi++;
                        } else if (regions.contains(regionManager.getRegion("paraguay"))) {
                            par++;
                        } else if (regions.contains(regionManager.getRegion("peru"))) {
                            per++;
                        } else if (regions.contains(regionManager.getRegion("uruguay"))) {
                            uru++;
                        }
                    }
                    lines.add("§aArgentina: §f" + arg);
                    lines.add("§aBolivia: §f" + bol);
                    lines.add("§aChile: §f" + chi);
                    lines.add("§aParaguay: §f" + par);
                    lines.add("§aPerú: §f" + per);
                    lines.add("§aUruguay: §f" + uru);
                    break;
                case PROJECT:
                    try {
                        Project project = new Project(p.getLocation());

                        ChatColor c;
                        switch (project.getDifficulty()) {
                            case FACIL:
                                c = ChatColor.GREEN;
                                break;
                            case INTERMEDIO:
                                c = ChatColor.YELLOW;
                                break;
                            default:
                                c = ChatColor.RED;
                                break;
                        }
                        title = c + project.getName(true);

                        lines.add(" ");
                        lines.add("§aDificultad: §f" + StringUtils.capitalize(project.getDifficulty().toString().toLowerCase().replace("facil", "fácil").replace("dificil", "difícil")));
                        lines.add("§aPaís: §f" + StringUtils.capitalize(project.getCountry().getName()));
                        if (project.getTag() != null) {
                            lines.add("§aEtiqueta: §f" + StringUtils.capitalize(project.getTag().toString().replace("_", " ")));
                        }
                        if (project.getOwner() != null) {
                            lines.add("§aLíder: §f" + new ServerPlayer(project.getOwner()).getName());
                        }
                        if (!project.getMembers().isEmpty()) {
                            lines.add("§aMiembros: §f");
                            int i = 0;
                            for (OfflinePlayer member : project.getMembers()) {
                                lines.add("- " + new ServerPlayer(member).getName());
                                i++;
                                if (i >= 9) {
                                    lines.add("etc...");
                                }
                            }
                        }
                    } catch (Exception e) {
                        lines.add(" ");
                        lines.add("§cNo disponible.");
                    }
                    break;
                case ME:
                    ChatManager chatManager = serverPlayer.getChatManager();
                    if (chatManager.hasNick()) {
                        title = ChatColor.GREEN + serverPlayer.getChatManager().getNick();
                    } else {
                        title = ChatColor.GREEN + serverPlayer.getName();
                    }

                    lines.add(" ");

                    ChatManager cManager = serverPlayer.getChatManager();
                    GroupsManager gManager = serverPlayer.getGroupsManager();
                    DiscordManager dManager = serverPlayer.getDiscordManager();
                    PointsManager poManager = serverPlayer.getPointsManager();
                    ProjectsManager prManager = serverPlayer.getProjectsManager();

                    lines.add("§aRango: §f" + cManager.getMainPrefix());
                    if (gManager.getSecondaryGroups().size() > 0) {
                        lines.add("§aOtros rangos:§f");
                        cManager.getSecondaryPrefixes().forEach(prefix -> lines.add("- " + prefix));
                    }

                    if (dManager.isLinked()) {
                        lines.add("§aDiscord: §f" + dManager.getName() + "#" + dManager.getDiscriminator());
                    } else {
                        lines.add("§aDiscord: §fN/A");
                    }

                    ChatColor color;
                    color = cManager.isHidden() ? ChatColor.DARK_GRAY : ChatColor.WHITE;
                    lines.add("§aChat: " + color + cManager.getChat().getFormattedName());
                    if (poManager.getMaxPoints().getValue() > 0) {
                        lines.add("§aPuntos:§f");

                        poManager.getSorted().forEach((country, points) -> lines.add("- " + StringUtils.capitalize(country.getName().replace("peru", "perú")) + ": " + poManager.getPoints(country)));
                    }

                    lines.add("§aProyectos activos: §f" + prManager.getTotalProjects());
                    lines.add("§aProyectos terminados: §f" + prManager.getTotalFinishedProjects());
                    break;
                case TOP:
                    OldCountry country = new OldCountry(((Player) serverPlayer.getPlayer()).getLocation());

                    if (!country.getName().equals("global") && !country.getName().equals("argentina")) {

                        int i = 1;
                        for (PointsManager pointsManager : country.getScoreboard()) {

                            ChatColor c = ChatColor.WHITE;

                            if (pointsManager.getPoints(country) >= 15) {
                                int points = pointsManager.getPoints(country);
                                if (points >= 1000) {
                                    c = ChatColor.GOLD;
                                } else if (points >= 500) {
                                    c = ChatColor.YELLOW;
                                } else if (points >= 150) {
                                    c = ChatColor.DARK_BLUE;
                                } else {
                                    c = ChatColor.BLUE;
                                }
                            }
                            lines.add(i + ". §a" + pointsManager.getPoints(country) + " §7- " + c + pointsManager.getServerPlayer().getName());
                            i++;
                        }

                    } else {

                        lines.add("§cNo disponible.");
                    }
                    break;
            }
            if (Netherboard.instance().getBoard(p).getLines().values() != lines) {
                BPlayerBoard board = Netherboard.instance().createBoard(p, title);
                board.setAll(lines.toArray(new String[0]));
            }
        } else {
            if (Netherboard.instance().hasBoard(p)) {
                Netherboard.instance().getBoard(p).clear();
            }
        }
    }

    public boolean toggleHidden() {
        hidden = !hidden;
        update();
        save();
        return hidden;
    }

    public boolean toggleAuto() {
        auto = !auto;
        save();
        return auto;
    }

    public boolean isAuto() {
        return auto;
    }

    public boolean isHidden() {
        return hidden;
    }

    public ScoreboardType getType() {
        return type;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        update();
    }

    public void setType(ScoreboardType type) {
        this.type = type;
        update();
    }

    public void save() {
        scoreboard.set("type", type.toString().toLowerCase());
        scoreboard.set("hidden", hidden);
        scoreboard.set("auto", auto);
        data.set("scoreboard", scoreboard);
        data.save();
    }
}
