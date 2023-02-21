package pizzaaxx.bteconosur.Projects.Finished;

import com.sk89q.worldedit.BlockVector2D;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Projects.ProjectTag;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderExpression;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderSet;
import pizzaaxx.bteconosur.Showcases.Showcase;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FinishedProject {

    private final BTEConoSur plugin;
    private final String id;
    private final long finishedDate;
    private final boolean sentForm;
    private final String originalName;
    private final Country country;
    private final Set<String> cities;
    private final ProjectType type;
    private final int points;
    private final Set<UUID> members;
    private final UUID owner;
    private final ProjectTag tag;
    private final List<BlockVector2D> regionPoints;
    private final String formalName;
    private final String postID;
    private final List<Showcase> showcases;

    public long getFinishedDate() {
        return finishedDate;
    }

    public boolean isSentForm() {
        return sentForm;
    }

    public String getOriginalName() {
        return originalName;
    }

    public Country getCountry() {
        return country;
    }

    public Set<String> getCities() {
        return cities;
    }

    public ProjectType getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public UUID getOwner() {
        return owner;
    }

    public ProjectTag getTag() {
        return tag;
    }

    public List<BlockVector2D> getRegionPoints() {
        return regionPoints;
    }

    public String getFormalName() {
        return formalName;
    }

    public String getPostID() {
        return postID;
    }

    public List<Showcase> getShowcases() {
        return showcases;
    }

    public FinishedProject(@NotNull BTEConoSur plugin, String id) throws SQLException, IOException {
        this.plugin = plugin;
        this.id = id;
        ResultSet set = plugin.getSqlManager().select(
                "finished_projects",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", id
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.finishedDate = set.getTimestamp("finished_date").getTime();
            this.sentForm = set.getBoolean("sent_form");
            this.originalName = set.getString("original_name");
            this.country = plugin.getCountryManager().get(set.getString("country"));
            this.cities = plugin.getJSONMapper().readValue(set.getString("cities"), HashSet.class);
            this.type = country.getProjectType(set.getString("type"));
            this.points = set.getInt("points");

            this.members = new HashSet<>();
            Set<String> rawMembers = plugin.getJSONMapper().readValue(set.getString("members"), HashSet.class);
            for (String rawUUID : rawMembers) {
                this.members.add(UUID.fromString(rawUUID));
            }

            this.owner = plugin.getSqlManager().getUUID(set, "owner");
            this.tag = ProjectTag.valueOf(set.getString("tag"));

            this.regionPoints = new ArrayList<>();
            List<Object> rawCoords = plugin.getJSONMapper().readValue(set.getString("region_points"), ArrayList.class);
            for (Object obj : rawCoords) {
                Map<String, Double> coords = (Map<String, Double>) obj;
                this.regionPoints.add(new BlockVector2D(coords.get("x"), coords.get("z")));
            }

            this.formalName = set.getString("formal_name");
            this.postID = set.getString("post_id");

            this.showcases = new ArrayList<>();
            ResultSet showcaseSet = plugin.getSqlManager().select(
                    "showcases",
                    new SQLColumnSet(
                            "message_id"
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "project_id", "=", this.id
                            )
                    ),
                    new SQLOrderSet(
                            new SQLOrderExpression(
                                    "date", SQLOrderExpression.Order.ASC
                            )
                    )
            ).retrieve();

            while (showcaseSet.next()) {
                this.showcases.add(new Showcase(showcaseSet.getString("message_id"), this.id));
            }

        } else {
            throw new IllegalArgumentException();
        }
    }
}
