package pizzaaxx.bteconosur.WorldEdit.Assets;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
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
import java.util.UUID;

public class Asset {

    private final BTEConoSur plugin;

    private final String id;
    private String name;
    private final UUID creator;
    private boolean autoRotate;
    private final Clipboard clipboard;
    private final int xOffset;
    private final int zOffset;
    private final int yOffset;

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
            this.xOffset = set.getInt("x_offset");
            this.yOffset = set.getInt("y_offset");
            this.zOffset = set.getInt("z_offset");
            this.autoRotate = set.getBoolean("auto_rotate");
        } else {
            throw new IllegalArgumentException();
        }

        File file = new File(plugin.getDataFolder(), "assets/" + id + ".schematic");
        ClipboardFormat format = ClipboardFormat.findByFile(file);
        ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()));
        this. clipboard = reader.read(plugin.getWorldEditWorld().getWorldData());
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

    // --- SETTER ---

    public void setName(@NotNull String nuevoNombre) {
        try {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAutoRotate(boolean autoRotate) {
        try {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void paste(Player player, Vector vector) throws WorldEditException {
        LocalSession localSession = plugin.getWorldEdit().getLocalSession(player);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(plugin.getWorldEditWorld(), localSession.getBlockChangeLimit());

        WorldData worldData = plugin.getWorldEditWorld().getWorldData();
        Operation operation = new ClipboardHolder(this.clipboard, worldData)
                .createPaste(editSession, worldData)
                .to(vector)
                .ignoreAirBlocks(true)
                .build();
        Operations.complete(operation);

        localSession.remember(editSession);
    }
}
