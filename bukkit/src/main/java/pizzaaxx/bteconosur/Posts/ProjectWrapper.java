package pizzaaxx.bteconosur.Posts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldedit.BlockVector2D;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Projects.ProjectTag;
import pizzaaxx.bteconosur.Projects.ProjectType;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProjectWrapper {

    String getId();

    UUID getOwner();

    Set<UUID> getMembers();

    Country getCountry();

    Set<String> getCities();

    ProjectTag getTag();

    ProjectType getType();

    int getPoints();

    List<BlockVector2D> getRegionPoints();

    String getDisplayName();

    boolean hasPost();

    Post getPost();

    void updatePost() throws SQLException, JsonProcessingException;

}
