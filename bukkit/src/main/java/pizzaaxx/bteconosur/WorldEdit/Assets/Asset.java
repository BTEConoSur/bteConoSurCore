package pizzaaxx.bteconosur.WorldEdit.Assets;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;
import org.bukkit.Rotation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Asset {

    private final BTEConoSur plugin;

    private final String id;
    private String name;
    private final UUID creator;
    private boolean autoRotate;
    private Set<String> tags;
    private Clipboard clipboard;

    public Asset(@NotNull BTEConoSur plugin, String id) throws SQLException, IOException {
        this.plugin = plugin;
        this.id = id;

        ResultSet set = plugin.getSqlManager().select(
                "assets",
                new SQLColumnSet(
                        "*"
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "id","=", this.id
                        )
                )
        ).retrieve();

        if (set.next()) {
            this.name = set.getString("name");
            this.creator = plugin.getSqlManager().getUUID(set, "creator");
            this.autoRotate = set.getBoolean("auto_rotate");
            this.tags = plugin.getJSONMapper().readValue(set.getString("tags"), HashSet.class);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void loadSchematic() throws IOException {
        if (this.clipboard == null) {
            File file = new File(plugin.getDataFolder(), "assets/" + id + ".schematic");
            ClipboardFormat format = ClipboardFormat.SCHEMATIC;
            ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()));
            this.clipboard = reader.read(plugin.getWorldEditWorld().getWorldData());
        }
    }

    // --- GETTER ---

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAutoRotate() {
        return autoRotate;
    }

    public UUID getCreator() {
        return creator;
    }

    public Set<String> getTags() {
        return tags;
    }

    // --- SETTER ---

    public void setName(@NotNull String nuevoNombre) throws SQLException {
        plugin.getSqlManager().update(
                "assets",
                new SQLValuesSet(
                        new SQLValue(
                                "name", nuevoNombre
                        )
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", this.id
                        )
                )
        ).execute();
        this.name = nuevoNombre;
    }

    public void setAutoRotate(boolean autoRotate) throws SQLException {
        plugin.getSqlManager().update(
                "assets",
                new SQLValuesSet(
                        new SQLValue(
                                "auto_rotate", autoRotate
                        )
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", this.id
                        )
                )
        ).execute();
        this.autoRotate = autoRotate;
    }

    public void setTags(Set<String> tags) throws SQLException {
        plugin.getSqlManager().update(
                "assets",
                new SQLValuesSet(
                        new SQLValue(
                                "tags", tags
                        )
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", this.id
                        )
                )
        ).execute();
        this.tags = tags;
    }

    public void paste(Player player, Vector vector, double rotation) throws WorldEditException {
        LocalSession localSession = plugin.getWorldEdit().getLocalSession(player);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(plugin.getWorldEditWorld(), localSession.getBlockChangeLimit());

        WorldData worldData = plugin.getWorldEditWorld().getWorldData();
        ClipboardHolder holder = new ClipboardHolder(this.clipboard, worldData);
        Transform transform = new AffineTransform().rotateY(rotation);
        holder.setTransform(transform);
        Operation operation = holder
                .createPaste(editSession, worldData)
                .to(vector)
                .ignoreAirBlocks(true)
                .build();
        Operations.complete(operation);

        localSession.remember(editSession);
    }
}
