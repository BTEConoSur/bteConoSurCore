package pizzaaxx.bteconosur.country;

import java.util.HashMap;
import java.util.Map;

public class CountryRegistry {

    private final Map<String, Country> countries = new HashMap<>();
    private final Map<String, String> guilds = new HashMap<>();

    public void add(Country country) {
        countries.put(country.getName(), country);
        guilds.put(country.getGuildId(), country.getName());
    }

    public void remove(String id) {
        Country country = countries.get(id);
        guilds.remove(country.getGuildId());
        countries.remove(country.getName());
    }

    public Country get(String id) {
        return countries.get(id);
    }

    public boolean exists(String id) {
        return countries.containsKey(id);
    }

}
