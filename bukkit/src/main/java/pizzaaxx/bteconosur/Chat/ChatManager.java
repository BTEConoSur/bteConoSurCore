package pizzaaxx.bteconosur.Chat;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.cities.projects.GlobalProjectsManager;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager {

    private final Map<String, IChat> registry = new HashMap<>();

    private final BteConoSur plugin;

    public ChatManager(BteConoSur plugin) {
        this.plugin = plugin;
    }

    public boolean exists(@NotNull String id) {
        return registry.containsKey(id);
    }

    public void register(IChat chat) {
        registry.put(chat.getId(), chat);
    }

    public CountryChat getChat(@NotNull Country country) {

        if (!exists("country_" + country.getName())) {
            register(
                    new CountryChat(country)
            );
        }
        return (CountryChat) registry.get("country_" + country.getName());

    }

    public GlobalChat getGlobalChat() {
        if (!exists("global")) {
            register(
                    new GlobalChat(plugin)
            );
        }
        return (GlobalChat) registry.get("global");
    }

    public ProjectChat getChat(@NotNull Project project) {
        if (!exists("project_" + project.getId())) {
            register(
                    new ProjectChat(project)
            );
        }
        return (ProjectChat) registry.get("project_" + project.getId());
    }

    public IChat getChat(String id) throws ChatException {
        if (exists(id)) {
            return registry.get(id);
        } else {

            if (id.startsWith("project_")) {

                if (plugin.getProjectsManager().exists(id.replace("project_", ""))) {

                    Project project = plugin.getProjectsManager().getFromId(id.replace("project_", ""));

                    register(
                            new ProjectChat(project)
                    );

                }
                return registry.get(id);

            }

        }
        throw new ChatException(ChatException.Type.IdNotFound);
    }

    public void closeChat(@NotNull IChat chat) {

        for (UUID uuid : chat.getMembers()) {
            chat.sendMember(uuid, getGlobalChat());
        }
        registry.remove(chat.getId());

    }

    public void remove(@NotNull IChat chat) {
        registry.remove(chat.getId());
    }

}
