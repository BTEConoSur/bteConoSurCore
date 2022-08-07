package pizzaaxx.bteconosur.chats;

import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.HashMap;
import java.util.Map;

public class ChatManager {

    private final Map<String, Chat> registry = new HashMap<>();

    private final BteConoSur plugin;

    public ChatManager(BteConoSur plugin) {
        this.plugin = plugin;
    }

    public Chat createChat(Country country) {

        if (!registry.containsKey(country.getName())) {
            registry.put(country.getName(), new Chat(country.getName()));
        }

    }

    public Chat createChat(Project project) {
        if (!registry.containsKey(project.getId())) {
            registry.put(project.getId(), new Chat(project.getName()));
        }


    }

}
