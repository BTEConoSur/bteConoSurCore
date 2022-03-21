package pizzaaxx.bteconosur.projects;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProjectRegistry {

    private final Map<String, Project> projects = new HashMap<>();
    private final Map<String, Date> lastAccessed = new HashMap<>();

    public void register(Project project) {
        projects.put(project.getId(), project);
        startTimer(project);
    }

    public Project get(String id) {
        if (projects.containsKey(id)) {
           startTimer(projects.get(id));
        }
        return projects.get(id);
    }

    public void remove(String id) {
        projects.remove(id);
        lastAccessed.remove(id);
    }

    public boolean exists(String id) {
        return projects.containsKey(id);
    }

    private void startTimer(Project project) {
        lastAccessed.put(project.getId(), new Date(System.currentTimeMillis()));
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (new Date(System.currentTimeMillis()).getTime() - lastAccessed.get(project.getId()).getTime() >= 500000) {
                    remove(project.getId());
                }
            }
        };
        runnable.runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("bteConoSur"), 12000);
    }
}
