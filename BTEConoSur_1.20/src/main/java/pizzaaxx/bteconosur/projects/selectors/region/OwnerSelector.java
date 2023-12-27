package pizzaaxx.bteconosur.projects.selectors.region;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.projects.Project;

import java.util.Objects;
import java.util.UUID;

public class OwnerSelector implements ProjectRegionSelector {

    private final UUID owner;

    public OwnerSelector(UUID owner) {
        this.owner = owner;
    }

    @Override
    public boolean check(@NotNull Project project) {
        return Objects.equals(project.getOwner(), owner);
    }
}
