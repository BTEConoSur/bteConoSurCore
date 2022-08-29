package pizzaaxx.bteconosur.country.cities.projects.ProjectSelector;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.*;

public class AlphabeticProjectSelector implements IProjectSelector {

    @Override
    public Project select(@NotNull Collection<Project> projects) throws NotInsideProjectException {

        if (!projects.isEmpty()) {
            Map<String, Project> map = new HashMap<>();

            for (Project project : projects) {
                map.put(project.getId(), project);
            }

            List<String> ids = new ArrayList<>(map.keySet());
            Collections.sort(ids);

            return map.get(ids.get(0));
        }
        throw new NotInsideProjectException();
    }

}
