package pizzaaxx.bteconosur.server.player;

import org.bukkit.configuration.ConfigurationSection;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.projects.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pizzaaxx.bteconosur.country.OldCountry.countryNames;

public class ProjectsManager {

    private final ServerPlayer serverPlayer;
    private final DataManager data;
    private final Map<String, List<String>> projects = new HashMap<>();
    private final Map<String, Integer> finishedProjects = new HashMap<>();

    public ProjectsManager(ServerPlayer s) {
        data = s.getDataManager();
        serverPlayer = s;

        if (data.contains("projects")) {
            ConfigurationSection projectsSection = data.getConfigurationSection("projects");
            for (String country : countryNames) {
                if (projectsSection.contains(country)) {
                    projects.put(country, (List<String>) projectsSection.getList(country));
                }
            }
        }

        if (data.contains("finishedProjects")) {
            ConfigurationSection finishedProjectsSection = data.getConfigurationSection("finishedProjects");
            for (String country : countryNames) {
                if (finishedProjectsSection.contains(country)) {
                    finishedProjects.put(country, finishedProjectsSection.getInt(country));
                }
            }
        }
    }

    /**
     *
     * @return IDs de todos los proyectos del jugador.
     */
    public List<String> getAllProjects() {
        List<String> allProjects = new ArrayList<>();

        projects.forEach((key, value) -> allProjects.addAll(value));

        return allProjects;
    }

    public boolean hasProjectsIn(OldCountry country) {
        return projects.containsKey(country.getName());
    }

    public List<String> getProjects(OldCountry country) {
        return projects.get(country.getName());
    }

    public void addProject(Project project) {
        List<String> ps = projects.get(project.getCountry().getName());
        ps.add(project.getId());
        projects.put(project.getCountry().getName(), ps);
        data.set("projects", projects);
        data.save();
        serverPlayer.getGroupsManager().checkGroups();
        if (serverPlayer.getPlayer().isOnline()) {
            ScoreboardManager manager = serverPlayer.getScoreboardManager();
            if (manager.getType() == ScoreboardManager.ScoreboardType.ME) {
                manager.update();
            }
        }
    }

    public void removeProject(Project project) {
        projects.get(project.getCountry().getName()).remove(project.getId());
        data.set("projects", projects);
        data.save();
        serverPlayer.getGroupsManager().checkGroups();
        if (serverPlayer.getPlayer().isOnline()) {
            ScoreboardManager manager = serverPlayer.getScoreboardManager();
            if (manager.getType() == ScoreboardManager.ScoreboardType.ME) {
                manager.update();
            }
        }
        ChatManager cManager = serverPlayer.getChatManager();
        if (cManager.getChat().getName().equals("project_" + project.getId())) {
            cManager.setChat("global");
        }
        if (cManager.getDefaultChat().getName().equals("project_" + project.getId())) {
            cManager.setDefaultChat("global");
        }
    }

    public int getTotalFinishedProjects() {
        int total = 0;
        for (Map.Entry<String, Integer> entry : finishedProjects.entrySet()) {
            total = total + entry.getValue();
        }
        return total;
    }

    public int getFinishedProjects(OldCountry country) {
        return finishedProjects.get(country.getName());
    }

    public void addFinishedProject(OldCountry country) {
        int actual = finishedProjects.get(country.getName());
        finishedProjects.put(country.getName(), actual + 1);

        Map<String, Integer> save = new HashMap<>(finishedProjects);

        data.set("finishedProjects", save);
        data.save();
    }

    public int getTotalProjects() {
        return getAllProjects().size();
    }

    public Map<OldCountry, List<String>> getOwnedProjects() {
        Map<OldCountry, List<String>> ownedProjects = new HashMap<>();
        projects.forEach((country, ps) -> {
            List<String> owned = new ArrayList<>();
            ps.forEach(id -> {
                Project project = new Project(id);

                if (project.getOwner() == serverPlayer.getPlayer()) {
                    owned.add(id);
                }
            });
            ownedProjects.put(new OldCountry(country), owned);
        });
        return ownedProjects;
    }

    public List<String> getAllOwnedProjects() {
        List<String> owned = new ArrayList<>();

        for (Map.Entry<String, List<String>> list : projects.entrySet()) {
            for (String id : list.getValue()) {
                Project project = new Project(id);

                if (project.getOwner() == serverPlayer.getPlayer()) {
                    owned.add(id);
                }
            }
        }

        return owned;
    }

}
