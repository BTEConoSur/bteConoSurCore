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
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.projects.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class ScoreboardManager {

    private final ServerPlayer serverPlayer;
    private final DataManager data;
    private final ConfigurationSection scoreboard;
    private ScoreboardType type;
    private boolean auto;
    private boolean hidden;

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

                        ChatColor color;
                        if (project.getDifficulty().equals("facil")) {
                            color = ChatColor.GREEN;
                        } else if (project.getDifficulty().equals("intermedio")) {
                            color = ChatColor.YELLOW;
                        } else {
                            color = ChatColor.RED;
                        }

                        lines.add(" ");
                        lines.add("§aDificultad: §f" + StringUtils.capitalize(project.getDifficulty().replace("facil", "fácil").replace("dificil", "difícil")));
                        lines.add("§aPaís: §f" + StringUtils.capitalize(project.getOldCountry()));
                        if (project.getTag() != null) {
                            lines.add("§aEtiqueta: §f" + StringUtils.capitalize(project.getTag().replace("_", " ")));
                        }
                        if (project.getOwnerOld() != null) {
                            lines.add("§aLíder: §f" + new ServerPlayer(project.getOwnerOld()).getName());
                        }
                        if (project.getMembersOld() != null) {
                            lines.add("§aMiembros: §f");
                            int i = 0;
                            for (OfflinePlayer member : project.getMembersOld()) {
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

                        poManager.getSorted().forEach((country, points) -> lines.add("- " + StringUtils.capitalize(country.getCountry().replace("peru", "perú")) + ": " + poManager.getPoints(country)));
                    }

                    lines.add("§aProyectos activos: §f" + prManager.getTotalProjects());
                    lines.add("§aProyectos terminados: §f" + prManager.getTotalFinishedProjects());
                    break;
                case TOP:
                    Country country = new Country(((Player) serverPlayer.getPlayer()).getLocation());

                    if (!country.getCountry().equals("global") && !country.getCountry().equals("argentina")) {

                        int i = 1;
                        for (ServerPlayer s : country.getScoreboard()) {

                            ChatColor c = ChatColor.WHITE;

                            if (s.getBuilderRank(country) != null) {
                                String bRank = s.getBuilderRank(country);
                                switch (bRank) {
                                    case "builder":
                                        c = ChatColor.BLUE;
                                        break;
                                    case "avanzado":
                                        c = ChatColor.DARK_BLUE;
                                        break;
                                    case "veterano":
                                        c = ChatColor.YELLOW;
                                        break;
                                    case "maestro":
                                        c = ChatColor.GOLD;
                                        break;
                                }
                            }
                            lines.add(i + ". §a" + s.getPoints(country) + " §7- " + c + s.getName());
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
