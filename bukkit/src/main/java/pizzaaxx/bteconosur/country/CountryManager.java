package pizzaaxx.bteconosur.country;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.HelpMethods.StringHelper;
import pizzaaxx.bteconosur.configuration.Configuration;

import java.util.HashMap;
import java.util.Map;

public class CountryManager {

    private final Plugin plugin;
    private final Map<String, Country> registry = new HashMap<>();
    private final Map<String, String> abbreviations = new HashMap<>();

    public CountryManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void add(String name, String abbreviation) {
        registry.put(name, new Country(new Configuration(plugin, "countries/" + name + "/config"), plugin, name));
        abbreviations.put(abbreviation, name);
    }

    public Country get(@NotNull String name) {

        if (name.matches("[a-zA-Z]{2}")) {
            if (abbreviations.containsKey(name)) {
                String newName = abbreviations.get(name);
                return registry.get(newName);
            }
            return null;
        } else {
            return registry.get(StringHelper.removeAccents(name));
        }

    }

    public boolean exists(@NotNull String name) {

        if (name.matches("[a-zA-Z]{2}")) {
            return abbreviations.containsKey(name);
        } else {
            return registry.containsKey(StringHelper.removeAccents(name));
        }

    }

}
