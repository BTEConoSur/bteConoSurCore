package pizzaaxx.bteconosur.country;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.HelpMethods.StringHelper;

import java.util.HashMap;
import java.util.Map;

public class CountryManager {

    private final Map<String, Country> registry = new HashMap<>();
    private final Map<String, String> abbreviations = new HashMap<>();

    public void add(String name, String abbreviation) {
        registry.put(name, new Country());
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
