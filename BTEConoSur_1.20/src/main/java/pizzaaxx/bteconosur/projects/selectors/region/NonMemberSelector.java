package pizzaaxx.bteconosur.projects.selectors.region;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.projects.Project;

import java.util.Objects;
import java.util.UUID;

public class NonMemberSelector implements ProjectRegionSelector {

    private final UUID uuid;

    public NonMemberSelector(UUID uuid) {
        this.uuid = uuid;
    }


    @Override
    public boolean check(@NotNull Project project) {
        return !(project.getMembers().contains(uuid) || Objects.equals(project.getOwner(), uuid));
    }
}
