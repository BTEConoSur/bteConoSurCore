package pizzaaxx.bteconosur.ServerPlayer.Managers;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.Chat.ChatException;
import pizzaaxx.bteconosur.Points.PointsContainer;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.projects.OldProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pizzaaxx.bteconosur.country.OldCountry.countryNames;

public class ProjectsManager {

    private final ServerPlayer serverPlayer;
    private final DataManager data;
    private final Map<Country, List<String>> projects = new HashMap<>();
    private final Map<Country, Integer> finishedProjects = new HashMap<>();

    public ProjectsManager(@NotNull ServerPlayer s) {
        data = s.getDataManager();
        serverPlayer = s;

        if (data.contains("projects")) {
            ConfigurationSection projectsSection = data.getConfigurationSection("projects");
            for (String country : countryNames) {
                if (projectsSection.contains(country)) {
                    projects.put(s.getPlugin().getCountryManager().get(country), projectsSection.getStringList(country));
                }
            }
        }

        if (data.contains("finishedProjects")) {
            ConfigurationSection finishedProjectsSection = data.getConfigurationSection("finishedProjects");
            for (String country : countryNames) {
                if (finishedProjectsSection.contains(country)) {
                    finishedProjects.put(s.getPlugin().getCountryManager().get(country), finishedProjectsSection.getInt(country));
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

    public boolean hasProjectsIn(Country country) {
        return projects.containsKey(country);
    }

    public List<String> getProjects(Country country) {
        return projects.getOrDefault(country, new ArrayList<>());
    }

    public void addProject(@NotNull Project project) {
        List<String> ps = projects.getOrDefault(project.getCountry(), new ArrayList<>());
        ps.add(project.getId());
        projects.put(project.getCountry(), ps);
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

    public void removeProject(@NotNull Project project) {
        projects.getOrDefault(project.getCountry(), new ArrayList<>()).remove(project.getId());
        data.set("projects", projects);
        data.save();
        serverPlayer.getGroupsManager().checkGroups();
        if (serverPlayer.getPlayer().isOnline()) {
            ScoreboardManager manager = serverPlayer.getScoreboardManager();
            if (manager.getType() == ScoreboardManager.ScoreboardType.ME) {
                manager.update();
            }
        }
        ChatManager chatManager = serverPlayer.getChatManager();

        try {
            if (chatManager.getChat().getId().equals("project_" + project.getId())) {
                chatManager.setChat(serverPlayer.getPlugin().getChatManager().getGlobalChat());
            }
        } catch (ChatException e) {
            chatManager.setGlobal();
        }

        try {
            if (chatManager.getDefaultChat().getId().equals("project_" + project.getId())) {
                chatManager.setDefaultChat(serverPlayer.getPlugin().getChatManager().getGlobalChat());
            }
        } catch (ChatException e) {
            chatManager.setDefaultChat(serverPlayer.getPlugin().getChatManager().getGlobalChat());
        }
    }

    public int getTotalFinishedProjects() {
        int total = 0;
        for (Integer finished : finishedProjects.values()) {
            total += finished;
        }
        return total;
    }

    public int getFinishedProjects(@NotNull PointsContainer container) {
        if (container instanceof BteConoSur) {
            return getTotalFinishedProjects();
        } else {
            return finishedProjects.getOrDefault((Country) container, 0);
        }
    }

    public void addFinishedProject(@NotNull Country country) {
        int actual = finishedProjects.getOrDefault(country, 0);
        finishedProjects.put(country, actual + 1);

        Map<Country, Integer> save = new HashMap<>(finishedProjects);

        data.set("finishedProjects", save);
        data.save();
    }

    public int getTotalProjects() {
        return getAllProjects().size();
    }

    public Map<Country, List<String>> getOwnedProjects() {
        Map<Country, List<String>> ownedProjects = new HashMap<>();
        projects.forEach((country, ps) -> {
            List<String> owned = new ArrayList<>();
            ps.forEach(id -> {
                Project project = serverPlayer.getPlugin().getProjectsManager().getFromId(id);

                if (project.getOwner() == serverPlayer.getPlayer().getUniqueId()) {
                    owned.add(id);
                }
            });
            ownedProjects.put(country, owned);
        });
        return ownedProjects;
    }

    public List<String> getAllOwnedProjects() {
        List<String> owned = new ArrayList<>();

        for (Map.Entry<Country, List<String>> list : projects.entrySet()) {
            for (String id : list.getValue()) {
                Project project = serverPlayer.getPlugin().getProjectsManager().getFromId(id);

                if (project.getOwner() == serverPlayer.getPlayer().getUniqueId()) {
                    owned.add(id);
                }
            }
        }
        return owned;
    }

}
