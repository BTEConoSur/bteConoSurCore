package pizzaaxx.bteconosur.serverPlayer;

import org.bukkit.configuration.ConfigurationSection;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.projects.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pizzaaxx.bteconosur.country.Country.countryNames;

public class ProjectsManager {

    private final ServerPlayer serverPlayer;
    private final DataManager data;
    private final Map<Country, List<String>> projects = new HashMap<>();
    private final Map<Country, Integer> finishedProjects = new HashMap<>();

    public ProjectsManager(ServerPlayer s) {
        data = s.getDataManager();
        serverPlayer = s;

        if (data.contains("projects")) {
            ConfigurationSection projectsSection = data.getConfigurationSection("projects");
            for (String country : countryNames) {
                if (projectsSection.contains(country)) {
                    projects.put(new Country(country), (List<String>) projectsSection.getList(country));
                }
            }
        }

        if (data.contains("finishedProjects")) {
            ConfigurationSection finishedProjectsSection = data.getConfigurationSection("finishedProjects");
            for (String country : countryNames) {
                if (finishedProjectsSection.contains(country)) {
                    finishedProjects.put(new Country(country), finishedProjectsSection.getInt(country));
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

    public List<String> getProjects(Country country) {
        return projects.get(country);
    }

    public void addProject(Project project) {
        projects.get(project.getCountry()).add(project.getId());
        data.set("projects", project);
        data.save();
        serverPlayer.getGroupsManager().checkGroups();
    }

    public void removeProject(Project project) {
        projects.get(project.getCountry()).remove(project.getId());
        data.set("projects", project);
        data.save();
        serverPlayer.getGroupsManager().checkGroups();
    }

    public int getTotalFinishedProjects() {
        int total = 0;
        for (Map.Entry<Country, Integer> entry : finishedProjects.entrySet()) {
            total = total + entry.getValue();
        }
        return total;
    }

    public int getFinishedProjects(Country country) {
        return finishedProjects.get(country);
    }

    public int getTotalProjects() {
        return getAllProjects().size();
    }

    public Map<Country, List<String>> getOwnedProjects() {
        Map<Country, List<String>> ownedProjects = new HashMap<>();
        projects.forEach((country, ps) -> {
            List<String> owned = new ArrayList<>();
            ps.forEach(id -> {
                try {
                    Project project = new Project(id);

                    if (project.getOwnerOld() == serverPlayer.getPlayer()) {
                        owned.add(id);
                    }
                } catch (Exception ignored) {}
            });
            ownedProjects.put(country, owned);
        });
        return ownedProjects;
    }

}
