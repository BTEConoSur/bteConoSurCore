package pizzaaxx.bteconosur.country.cities.projects.ChangeAction;

import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.server.player.ScoreboardManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

public class UpdateScoreboardProjectAction implements ProjectAction {

    private final Project project;

    public UpdateScoreboardProjectAction(Project project) {
        this.project = project;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void exec() {

        for (Player player : project.getPlayersInsideProject()) {

            ServerPlayer s = project.getPlugin().getPlayerRegistry().get(player.getUniqueId());
            ScoreboardManager manager = s.getScoreboardManager();

            if (manager.getType() == ScoreboardManager.ScoreboardType.PROJECT) {

                manager.update();

            }

        }

    }
}
