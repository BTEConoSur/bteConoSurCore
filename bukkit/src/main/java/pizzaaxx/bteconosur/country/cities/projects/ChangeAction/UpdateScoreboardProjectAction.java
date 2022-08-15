package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.ServerPlayer.Managers.ScoreboardManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

import java.util.HashSet;
import java.util.Set;

public class UpdateScoreboardProjectAction implements ProjectAction {

    private final Project project;
    private final Set<Player> players = new HashSet<>();
    private final BteConoSur plugin;

    public UpdateScoreboardProjectAction(Project project) {
        this.project = project;
        this.plugin = project.getPlugin();
        players.addAll(project.getPlayersInsideProject());
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() {

        for (Player player : players) {

            ServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId());
            ScoreboardManager manager = s.getScoreboardManager();

            if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {

                manager.update();

            }

        }

    }
}
