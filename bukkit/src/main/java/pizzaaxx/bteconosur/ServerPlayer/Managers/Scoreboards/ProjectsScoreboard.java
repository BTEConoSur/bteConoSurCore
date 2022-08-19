package pizzaaxx.bteconosur.ServerPlayer.Managers.Scoreboards;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.MemberProjectSelector;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.NoProjectsFoundException;

import java.util.ArrayList;
import java.util.List;

public class ProjectsScoreboard implements ScoreboardType {

    private final List<String> lines = new ArrayList<>();
    private final String title;

    public ProjectsScoreboard(@NotNull ServerPlayer s, Location loc, @NotNull BteConoSur plugin) {
        String titleTemp;
        try {
            Project project = plugin.getProjectsManager().getProjectAt(loc, new MemberProjectSelector(s.getId(),true, plugin));

            ChatColor color;
            switch (project.getDifficulty()) {
                case DIFICIL:
                    color = ChatColor.RED;
                    lines.add("§aDificultad: §fDifícil");
                    break;
                case INTERMEDIO:
                    color = ChatColor.YELLOW;
                    lines.add("§aDificultad: §fIntermedio");
                    break;
                default:
                    color = ChatColor.GREEN;
                    lines.add("§aDificultad: §fFácil");
                    break;
            }

            titleTemp = color + project.getName();

            lines.add("§aPaís: §f" + project.getCountry().getDisplayName());



        } catch (NoProjectsFoundException e) {

            lines.add("§cNo disponible.");
            titleTemp = "§4PROYECTO";

        }

        title = titleTemp;
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
