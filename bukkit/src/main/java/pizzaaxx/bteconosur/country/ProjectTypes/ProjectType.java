package pizzaaxx.bteconosur.country.ProjectTypes;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.ProjectTypes.Points.ProjectPointType;
import pizzaaxx.bteconosur.country.ProjectTypes.Points.ProjectPointsTypeParseException;
import pizzaaxx.bteconosur.country.ProjectTypes.Points.ProjectTypeParseException;

import java.util.HashMap;
import java.util.Map;

public class ProjectType {

    private final String id;
    private final BteConoSur plugin;
    private final Country country;
    private final int maxMembers;
    private final String displayName;
    private final Map<String, Integer> unlocks = new HashMap<>();
    private final ProjectPointType pointType;

    public ProjectType(@NotNull ConfigurationSection section, BteConoSur plugin, Country country) throws ProjectPointsTypeParseException, ProjectTypeParseException {
        this.id = section.getName();
        this.plugin = plugin;
        this.country = country;

        this.maxMembers = section.getInt("maxMembers", 10);
        this.displayName = section.getString("displayName", id);


        if (section.contains("unlock")) {
            ConfigurationSection unlockSection = section.getConfigurationSection("unlock");
            for (String key : unlockSection.getKeys(false)) {
                unlocks.put(key, unlockSection.getInt(key));
            }
        }

        if (section.contains("points")) {
            pointType = ProjectPointType.getInstance(section.getConfigurationSection("points"));
        } else {
            throw new ProjectTypeParseException("Missing \"points\" field on project type configuration.");
        }
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public BteConoSur getPlugin() {
        return plugin;
    }

    public Country getCountry() {
        return country;
    }

    public boolean isUnlocked(@NotNull Map<String, Integer> finished) {

        if (unlocks.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, Integer> entry : finished.entrySet()) {
            if (unlocks.get(entry.getKey()) <= entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public ProjectPointType getPointType() {
        return pointType;
    }
}
