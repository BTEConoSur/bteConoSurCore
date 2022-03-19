package pizzaaxx.bteconosur.serverPlayer;

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
    private final Map<OldCountry, List<String>> projects = new HashMap<>();
    private final Map<OldCountry, Integer> finishedProjects = new HashMap<>();

    public ProjectsManager(ServerPlayer s) {
        data = s.getDataManager();
        serverPlayer = s;

        if (data.contains("projects")) {
            ConfigurationSection projectsSection = data.getConfigurationSection("projects");
            for (String country : countryNames) {
                if (projectsSection.contains(country)) {
                    projects.put(new OldCountry(country), (List<String>) projectsSection.getList(country));
                }
            }
        }

        if (data.contains("finishedProjects")) {
            ConfigurationSection finishedProjectsSection = data.getConfigurationSection("finishedProjects");
            for (String country : countryNames) {
                if (finishedProjectsSection.contains(country)) {
                    finishedProjects.put(new OldCountry(country), finishedProjectsSection.getInt(country));
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

    public List<String> getProjects(OldCountry country) {
        return projects.get(country);
    }

    public void addProject(Project project) {
        projects.get(project.getCountry()).add(project.getId());
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
        projects.get(project.getCountry()).remove(project.getId());
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
        for (Map.Entry<OldCountry, Integer> entry : finishedProjects.entrySet()) {
            total = total + entry.getValue();
        }
        return total;
    }

    public int getFinishedProjects(OldCountry country) {
        return finishedProjects.get(country);
    }

    public void addFinishedProject(OldCountry country) {
        int actual = finishedProjects.get(country);
        finishedProjects.put(country, actual + 1);
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
            ownedProjects.put(country, owned);
        });
        return ownedProjects;
    }

    public List<String> getAllOwnedProjects() {
        List<String> owned = new ArrayList<>();

        for (Map.Entry<OldCountry, List<String>> list : projects.entrySet()) {
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
