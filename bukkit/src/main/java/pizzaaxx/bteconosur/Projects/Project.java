package pizzaaxx.bteconosur.Projects;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Project {

    private final BTEConoSur plugin;

    private final String id;
    private final String name;
    private final Country country;
    private final City city;
    private final boolean pending;
    private final ProjectType type;
    private final Set<UUID> members;
    private final UUID owner;
    private final ProjectTag tag;

    public Project(@NotNull BTEConoSur plugin, String id) throws SQLException, IOException {
        this.plugin = plugin;
        this.id = id;

        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "*"
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", id
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.name = set.getString("name");

            this.country = plugin.getCountryManager().get(set.getString("country"));

            this.city = plugin.getCityManager().get(set.getString("city"));

            this.pending = set.getBoolean("pending");



            this.members = new HashSet<>();
            Set<String> uuids = plugin.getJSONMapper().readValue(set.getString("members"), HashSet.class);
            for (String uuid : uuids) {
                members.add(UUID.fromString(uuid));
            }

            this.owner = UUID.nameUUIDFromBytes(IOUtils.toByteArray(set.getBinaryStream("string")));

            this.tag = ProjectTag.valueOf(set.getString("tag").toUpperCase());

        } else {
            throw new IllegalArgumentException();
        };
    }

}
