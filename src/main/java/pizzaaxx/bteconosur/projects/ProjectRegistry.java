package pizzaaxx.bteconosur.projects;

import java.util.HashMap;
import java.util.Map;

public class ProjectRegistry {

    private final Map<String, Project> projects = new HashMap<>();

    public void a() {

    }

    public void register(Project project) {
        projects.put(project.getId(), project);
    }

    public Project get(String id) {
        return projects.get(id);
    }

    public void remove(String id) {
        projects.remove(id);
    }

    public boolean exist(String id) {
        return projects.containsKey(id);
    }

}
