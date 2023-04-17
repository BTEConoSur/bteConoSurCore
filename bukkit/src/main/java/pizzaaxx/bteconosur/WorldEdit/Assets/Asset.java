package pizzaaxx.bteconosur.WorldEdit.Assets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Asset implements AssetHolder {

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
                new SQLANDConditionSet(
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

    public Clipboard getClipboard() {
        return clipboard;
    }

    // --- GETTER ---


    public BTEConoSur getPlugin() {
        return plugin;
    }

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
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", this.id
                        )
                )
        ).execute();
        this.name = nuevoNombre;
    }

    public void setAutoRotate(boolean autoRotate) throws SQLException, JsonProcessingException {
        plugin.getSqlManager().update(
                "assets",
                new SQLValuesSet(
                        new SQLValue(
                                "auto_rotate", autoRotate
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "id", "=", this.id
                        )
                )
        ).execute();
        this.autoRotate = autoRotate;
        for (ServerPlayer s : plugin.getPlayerRegistry().getLoadedPlayers()) {
            s.getWorldEditManager().checkAssetsGroups();
        }
    }

    public void setTags(Set<String> tags) throws SQLException {
        plugin.getSqlManager().update(
                "assets",
                new SQLValuesSet(
                        new SQLValue(
                                "tags", tags
                        )
                ),
                new SQLANDConditionSet(
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

        Vector origin = clipboard.getOrigin();
        plugin.log("X: " + origin.getBlockX() + " /// Y: " + origin.getBlockY() + " /// Z: " + origin.getBlockZ());

        Vector dimensions = clipboard.getDimensions();
        plugin.log("X: " + dimensions.getBlockX() + " /// Y: " + dimensions.getBlockY() + " /// Z: " + dimensions.getBlockZ());

        for (int x = 0; x < dimensions.getBlockX(); x++) {
            for (int y = 0; y < dimensions.getBlockY(); y++) {
                for (int z = 0; z < dimensions.getBlockZ(); z++) {

                    if (plugin.getWorldGuard().canBuild(player, new Location(plugin.getWorld(), vector.getX(), vector.getY(), vector.getZ()))) {
                        Vector blockVector = new Vector(x, y, z);
                        Vector targetVector = vector.add(blockVector).subtract(clipboard.getOrigin()).transform2D(rotation, vector.getBlockX(), vector.getBlockZ(), 0, 0);

                        plugin.log("X: " + targetVector.getBlockX() + " /// Y: " + targetVector.getBlockY() + " /// Z: " + targetVector.getBlockZ());

                        BaseBlock block = clipboard.getBlock(blockVector);
                        if (block.getId() == 0) {
                            continue;
                        }

                        editSession.setBlock(
                                targetVector,
                                block
                        );
                    }
                }
            }
        }

        localSession.remember(editSession);
    }

    @Override
    public Asset select() {
        return this;
    }
}
