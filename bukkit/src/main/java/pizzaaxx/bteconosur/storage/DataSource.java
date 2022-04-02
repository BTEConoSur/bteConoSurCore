package pizzaaxx.bteconosur.storage;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class DataSource {

    private final Map<String, String> source;

    public DataSource(Map<String, String> source) {
        this.source = source;
    }

    public <T> T get(String name, Class<T> clazz) {
        return clazz.cast(get(name));
    }

    public String get(String name) {
        return source.get(name);
    }

    public static DataSource fromYml(ConfigurationSection section) {
        Map<String, String> source = new HashMap<>();

        section.getKeys(false)
                .forEach(key -> source.put(key, section.getString(key)));

        return new DataSource(source);
    }

}
