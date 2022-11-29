package pizzaaxx.bteconosur.WorldEdit.Assets;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Registry.Registry;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.DualMap;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static pizzaaxx.bteconosur.Utils.StringUtils.LOWER_CASE;

public class AssetsRegistry implements Registry<String, Asset> {

    private final BTEConoSur plugin;

    private final Map<String, Asset> assetsCache = new HashMap<>();
    private final Map<String, Long> deletionCache = new HashMap<>();

    private final DualMap<String, String> idsAndNames = new DualMap<>();

    public AssetsRegistry(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void init() throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "assets",
                new SQLColumnSet(
                        "id"
                ),
                new SQLConditionSet()
        ).retrieve();

        while (set.next()) {
            idsAndNames.put(set.getString("id"), set.getString("names"));
        }
    }

    @Override
    public boolean isLoaded(String id) {
        return assetsCache.containsKey(id);
    }

    @Override
    public void load(String id) {
        if (!this.isLoaded(id)) {
            try {
                assetsCache.put(id, new Asset(plugin, id));
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void unload(String id) {
        assetsCache.remove(id);
        deletionCache.remove(id);
    }

    @Override
    public boolean exists(String id) {
        return idsAndNames.containsK(id);
    }

    @Override
    public Asset get(String id) {
        if (this.exists(id)) {
            this.load(id);
            this.scheduleDeletion(id);
            return assetsCache.get(id);
        }
        return null;
    }

    @Override
    public Set<String> getIds() {
        return idsAndNames.getKs();
    }

    public Set<String> getNames() {
        return idsAndNames.getVs();
    }

    @Override
    public void scheduleDeletion(String id) {
        if (this.isLoaded(id)) {
            deletionCache.put(id, System.currentTimeMillis());
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (deletionCache.containsKey(id) && System.currentTimeMillis() - deletionCache.get(id) > 550000) {
                        unload(id);
                    }
                }
            };
            runnable.runTaskLaterAsynchronously(plugin, 12000);
        }
    }

    public String create(String name, @NotNull Clipboard clipboard, Vector origin, UUID creator) throws IOException, SQLException {
        String id = StringUtils.generateCode(8, this.getIds(), LOWER_CASE);
        File output = new File(plugin.getDataFolder(), "assets/" + id + ".schematic");
        clipboard.setOrigin(origin);
        ClipboardWriter writer = ClipboardFormat.SCHEMATIC.getWriter(Files.newOutputStream(output.toPath()));
        writer.write(clipboard, plugin.getWorldEditWorld().getWorldData());
        plugin.getSqlManager().insert(
                "assets",
                new SQLValuesSet(
                        new SQLValue(
                                "id", id
                        ),
                        new SQLValue(
                                "name", name
                        ),
                        new SQLValue(
                                "creator", creator
                        ),
                        new SQLValue(
                                "auto_rotate", false
                        )
                )
        ).execute();
        idsAndNames.put(id, name);
        return id;
    }

    public void delete(String id) {

    }
}
