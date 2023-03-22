package pizzaaxx.bteconosur.BuildEvents;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Projects.ProjectType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BuildEvent {

    public enum Status {
        EDITED, POSTED, ACTIVE, FINISHED
    }

    private final BTEConoSur plugin;
    private final Country country;
    private final Status status;
    private String name;
    private final Set<UUID> members;
    private ProjectType minimumUnlockedType;
    private int pointsGiven;
    private String image;
    private String postChannelID;
    private String description;
    private long from;
    private long to;

    public BuildEvent(@NotNull BTEConoSur plugin, @NotNull ResultSet set) throws SQLException {
        this.plugin = plugin;
        this.country = plugin.getCountryManager().get(set.getString("country"));
        this.status = Status.valueOf(set.getString("status"));
        this.name = set.getString("name");
        this.members = new HashSet<>();
        List<String> uuidsRaw = plugin.getJSONMapper().readValue(set.getString("members"), ArrayList.class);
        for (String uuidRaw : uuidsRaw) {
            members.add(UUID.fromString(uuidRaw));
        }
        String minimumUnlockedTypeString = set.getString("minimum_difficulty_unlocked");
        minimumUnlockedType = (minimumUnlockedTypeString != null ? country.getProjectType(minimumUnlockedTypeString) : null);
        pointsGiven = set.getInt("points_given");
        image = set.getString("image");
        postChannelID = set.getString("post_channel_id");
        description = set.getString("description");
        from = set.getTimestamp("date_from").getTime();
        to = set.getTimestamp("date_to").getTime();
    }

}
