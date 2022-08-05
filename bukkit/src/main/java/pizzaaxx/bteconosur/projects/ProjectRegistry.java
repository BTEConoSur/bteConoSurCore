package pizzaaxx.bteconosur.projects;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ProjectRegistry {

    private final Plugin plugin;
    private final Map<String, OldProject> projects = new HashMap<>();
    private final Map<String, Long> lastAccessed = new HashMap<>();

    public ProjectRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(OldProject project) {
        projects.put(project.getId(), project);
        startTimer(project);
    }

    public OldProject get(String id) {
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

    private void startTimer(OldProject project) {
        lastAccessed.put(project.getId(), System.currentTimeMillis());
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastAccessed.get(project.getId()) >= 500000) {
                    remove(project.getId());
                }
            }
        };
        runnable.runTaskLaterAsynchronously(plugin, 12000);
    }
}
