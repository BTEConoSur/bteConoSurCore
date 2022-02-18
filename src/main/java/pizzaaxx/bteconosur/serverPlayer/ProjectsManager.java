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

    private final DataManager data;
    private final Map<Country, List<String>> projects = new HashMap<>();
    private final Map<Country, Integer> finishedProjects = new HashMap<>();

    public ProjectsManager(ServerPlayer s) {
        data = s.getDataManager();

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

        for (Map.Entry<Country, List<String>> entry : projects.entrySet()) {
            allProjects.addAll(entry.getValue());
        }

        return allProjects;
    }

    public List<String> getProjects(Country country) {
        return projects.get(country);
    }

    public void addProject(Project project) {
        projects.get(project.getCountry()).add(project.getId());
        data.set("projects", project);
        data.save();
    }

    public void removeProject(Project project) {
        projects.get(project.getCountry()).remove(project.getId());
        data.set("projects", project);
        data.save();
    }

    public int getAllFinishedProjects() {
        int total = 0;
        for (Map.Entry<Country, Integer> entry : finishedProjects.entrySet()) {
            total = total + entry.getValue();
        }
        return total;
    }

    public int getFinishedProjects(Country country) {
        return finishedProjects.get(country);
    }

}
