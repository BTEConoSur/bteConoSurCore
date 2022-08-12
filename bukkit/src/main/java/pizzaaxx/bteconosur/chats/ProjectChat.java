package pizzaaxx.bteconosur.chats;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.country.cities.projects.ProjectsRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProjectChat implements IChat {

    private final String id;
    private final ProjectsRegistry registry;
    private final Set<UUID> members = new HashSet<>();

    public ProjectChat(@NotNull Project project) {
        this.id = project.getId();
        this.registry = project.getRegistry();
    }

    public ProjectChat(@NotNull String id, @NotNull ProjectsRegistry registry) {
        this.id = id;
        this.registry = registry;
    }

    @Override
    public String getDisplayName() {
        return "Proyecto " + registry.get(id).getName();
    }

    @Override
    public String getDiscordEmoji() {
        return ":hammer_pick:";
    }

    @Override
    public Set<UUID> getMembers() {
        return members;
    }

    @Override
    public void sendMessage(String message, UUID member) {



    }

    @Override
    public void broadcast(String message) {



    }

    @Override
    public void broadcast(String message, boolean ignoreHidden) {

    }

    @Override
    public void receiveMember(UUID uuid) {

    }

    @Override
    public void sendMember(UUID uuid, IChat chat) {

    }
}
