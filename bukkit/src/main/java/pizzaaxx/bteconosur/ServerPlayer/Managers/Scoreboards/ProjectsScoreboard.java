package pizzaaxx.bteconosur.ServerPlayer.Managers.Scoreboards;

import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.MemberProjectSelector;

import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.List;

public class ProjectsScoreboard implements ScoreboardType {

    private final List<String> lines = new ArrayList<>();
    private final String title;

    public ProjectsScoreboard(Location loc, BteConoSur plugin) {

        Project project = plugin.getProjectsManager().getProjectAt(loc, new MemberProjectSelector());

    }

    @Override
    public List<String> getLines() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }
}
