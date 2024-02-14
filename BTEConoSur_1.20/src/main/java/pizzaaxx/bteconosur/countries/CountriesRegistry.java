package pizzaaxx.bteconosur.countries;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.SQLResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.chat.Chat;
import pizzaaxx.bteconosur.chat.ChatProvider;
import pizzaaxx.bteconosur.terra.TerraCoords;
import pizzaaxx.bteconosur.utils.registry.BaseRegistry;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CountriesRegistry extends BaseRegistry<Country, String> implements ChatProvider {

    public CountriesRegistry(BTEConoSurPlugin plugin) {
        super(
                plugin,
                () -> {
                    List<String> names = new ArrayList<>();
                    try (SQLResult result = plugin.getSqlManager().select(
                            "countries",
                            new SQLColumnSet("name"),
                            new SQLANDConditionSet()
                    ).retrieve()) {
                        ResultSet set = result.getResultSet();
                        while (set.next()) {
                            names.add(set.getString("name"));
                        }
                    } catch (SQLException e) {
                        plugin.error("Could not load countries registry.");
                    }
                    return names;
                },
                name -> {
                    try {
                        return new Country(plugin, name);
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                        plugin.error("Error loading country instance. (Name: " + name + ")");
                    }
                    return null;
                },
                false
        );
    }

    public List<Country> getCountries() {
        List<Country> countries = new ArrayList<>(super.cacheMap.values());
        countries.sort(Comparator.comparing(Country::getName));
        return countries;
    }

    @Nullable
    public Country getCountryByAbbreviation(String abbreviation) {
        for (Country country : super.cacheMap.values()) {
            if (country.getAbbreviation().equals(abbreviation)) {
                return country;
            }
        }
        return null;
    }

    @Nullable
    public Country getCountryByGuildId(String guildId) {
        for (Country country : super.cacheMap.values()) {
            if (country.getGuildID().equals(guildId)) {
                return country;
            }
        }
        return null;
    }

    @Nullable
    public Country getCountryAt(Location location) {
        for (Country country : super.cacheMap.values()) {
            if (country.contains(
                    location.getBlockX(),
                    location.getBlockZ()
            )) {
                return country;
            }
        }
        return null;
    }

    @Nullable
    public Country getCountryAt(TerraCoords coords) {
        for (Country country : super.cacheMap.values()) {
            if (country.contains(
                    coords.getX(),
                    coords.getZ()
            )) {
                return country;
            }
        }
        return null;
    }

    // getcountryat from x and z
    @Nullable
    public Country getCountryAt(double x, double z) {
        for (Country country : super.cacheMap.values()) {
            if (country.contains(
                    x,
                    z
            )) {
                return country;
            }
        }
        return null;
    }

    @Override
    public @Nullable Chat getChat(String id) {
        return this.get(id);
    }

    @Override
    public @NotNull String getProviderId() {
        return "country";
    }

    @Override
    public @NotNull List<? extends Chat> getAvailableForPlayer(Player player) {
        return this.getCountries();
    }
}
