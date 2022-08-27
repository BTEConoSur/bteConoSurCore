package pizzaaxx.bteconosur.ServerPlayer.Managers.Scoreboards;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.Chat.ChatException;
import pizzaaxx.bteconosur.ServerPlayer.Managers.GroupsManager;
import pizzaaxx.bteconosur.ServerPlayer.Managers.PointsManager;
import pizzaaxx.bteconosur.ServerPlayer.Managers.ProjectsManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerScoreboard implements ScoreboardType {

    private final String title;
    private final List<String> lines = new ArrayList<>();

    public PlayerScoreboard(@NotNull ServerPlayer s) {

        title = "§a§n" + s.getChatManager().getDisplayName();

        lines.add(" ");

        GroupsManager gManager = s.getGroupsManager();
        lines.add("§aRango: §f" + gManager.getPrimaryGroup().getAsPrefix());
        if (!gManager.getSecondaryGroups().isEmpty()) {
            for (GroupsManager.SecondaryGroup group : gManager.getSecondaryGroups()) {
                lines.add("§7- §f" + group.getAsPrefix());
            }
        }

        if (s.getDiscordManager().isLinked()) {
            lines.add("§aDiscord: §f" + s.getDiscordManager().getName() + "#" + s.getDiscordManager().getDiscriminator());
        }

        try {
            lines.add("§aChat: §f" + s.getChatManager().getChat().getDisplayName());
        } catch (ChatException e) {
            lines.add("§aChat: §cError");
        }

        PointsManager pointsManager = s.getPointsManager();

        if (pointsManager.getTotalPoints() > 0) {
            lines.add("§aPuntos:");
            for (Map.Entry<Country, Integer> entry : pointsManager.getSorted().entrySet()) {
                Country country = entry.getKey();
                lines.add("- " + country.getDisplayName() + ": " + entry.getValue());
            }
        }

        ProjectsManager projectsManager = s.getProjectsManager();

        if (projectsManager.getTotalProjects() > 0) {
            lines.add("§aProyectos activos: §f" + projectsManager.getTotalProjects());
        }
        if (projectsManager.getTotalFinishedProjects() > 0) {
            lines.add("§aProyectos terminados: §f" + projectsManager.getTotalFinishedProjects());
        }

    }

    @Override
    public List<String> getLines() {
        return lines;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
