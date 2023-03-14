package pizzaaxx.bteconosur.Projects.Finished;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.monst.polylabel.PolyLabel;
import com.sk89q.worldedit.BlockVector2D;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Posts.Post;
import pizzaaxx.bteconosur.Projects.ProjectWrapper;
import pizzaaxx.bteconosur.Projects.ProjectTag;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.Scoreboard.ScoreboardDisplay;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FinishedProject implements ProjectWrapper, ScoreboardDisplay {

    private final BTEConoSur plugin;
    private final String id;
    private final long finishedDate;
    private final boolean sentForm;
    private final String name;
    private final Country country;
    private final Set<String> cities;
    private final ProjectType type;
    private final int points;
    private final Set<UUID> members;
    private final UUID owner;
    private final ProjectTag tag;
    private final List<BlockVector2D> regionPoints;
    private Post post;

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
            this.name = set.getString("name");
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

            String tagString = set.getString("tag");

            this.tag = (tagString == null ? null : ProjectTag.valueOf(tagString));

            this.regionPoints = new ArrayList<>();
            List<Object> rawCoords = plugin.getJSONMapper().readValue(set.getString("region_points"), ArrayList.class);
            for (Object obj : rawCoords) {
                Map<String, Double> coords = (Map<String, Double>) obj;
                this.regionPoints.add(new BlockVector2D(coords.get("x"), coords.get("z")));
            }

            try {
                post = new Post(plugin, id);
            } catch (IllegalArgumentException e) {
                post = null;
            }

        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean isClaimed() {
        return true;
    }

    public long getFinishedDate() {
        return finishedDate;
    }

    public boolean isSentForm() {
        return sentForm;
    }

    public String getName() {
        return name;
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

    @Override
    public String getDisplayName() {
        return (this.hasPost() ? this.getPost().getName() : this.getName());
    }

    @Override
    public boolean hasPost() {
        return post != null;
    }

    @Override
    public Post getPost() {
        return post;
    }

    @Override
    public Location getTeleportLocation() {

        Double[][][] points = new Double[1][regionPoints.size()][2];

        int counter = 0;
        for (BlockVector2D vector : regionPoints) {
            Double[] coords = new Double[] {vector.getX(), vector.getZ()};
            points[0][counter] = coords;
            counter++;
        }

        PolyLabel.Result result = PolyLabel.polyLabel(points, 1);

        return plugin.getWorld().getHighestBlockAt((int) Math.floor(result.getX()), (int) Math.floor(result.getY())).getLocation();

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void updatePost() throws SQLException, JsonProcessingException {
        try {
            post = new Post(plugin, id);
        } catch (IllegalArgumentException e) {
            post = null;
        }
    }

    @Override
    public String getScoreboardTitle() {
        return "§a§l" + this.getDisplayName();
    }

    @Override
    public List<String> getScoreboardLines() {

        List<String> lines = new ArrayList<>();

        lines.add("§fTipo: " + type.getDisplayName() + " (" + points + " puntos)");
        lines.add("§fLíder: " + plugin.getPlayerRegistry().get(owner).getName());
        lines.add("§fMiembros: " + members.size());
        lines.add(" ");
        lines.add("§8Terminado el " + new SimpleDateFormat("dd/MM/yy").format(new Date(finishedDate)));

        return lines;
    }

    @Override
    public String getScoreboardType() {
        return "tour";
    }
}
