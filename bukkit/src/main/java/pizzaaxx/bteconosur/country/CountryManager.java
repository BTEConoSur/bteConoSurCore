package pizzaaxx.bteconosur.country;

import com.sk89q.worldedit.BlockVector2D;
import net.dv8tion.jda.api.JDA;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.HelpMethods.StringHelper;

import java.util.*;

public class CountryManager {

    private final BteConoSur plugin;
    private final JDA bot;
    private final Map<String, Country> registry = new HashMap<>();
    private final Map<String, String> abbreviations = new HashMap<>();

    public CountryManager(BteConoSur plugin, JDA bot) {
        this.plugin = plugin;
        this.bot = bot;
    }

    public void add(String name, String abbreviation, String displayName, boolean allowsProjects) {
        registry.put(name, new Country(plugin, name, abbreviation, displayName, allowsProjects, bot));
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

    public boolean isInsideAnyCountry(Location loc) {
        for (Country country : registry.values()) {
            if (country.isInside(loc)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInsideAnyCountry(@NotNull BlockVector2D vector) {
        return this.isInsideAnyCountry(new Location(plugin.getWorld(), vector.getX(), 100, vector.getZ()));
    }

    public Country get(@NotNull Location loc) {
        for (Country country : registry.values()) {
            if (country.isInside(loc)) {
                return country;
            }
        }
        return null;
    }

    public Country get(@NotNull BlockVector2D vector) {
        return this.get(new Location(plugin.getWorld(), vector.getX(), 100, vector.getZ()));
    }

    public List<Country> getAllCountries() {

        List<String> names = new ArrayList<>(registry.keySet());
        Collections.sort(names);

        List<Country> result = new ArrayList<>();
        for (String name : names) {
            result.add(registry.get(name));
        }

        return result;

    }

}
