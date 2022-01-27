package pizzaaxx.bteconosur.worldedit.trees;

import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.SchematicReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.LegacyWorldData;
import com.sk89q.worldedit.world.registry.WorldData;
import javafx.scene.transform.Transform;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.yaml.YamlManager;

import javax.sound.sampled.Clip;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static pizzaaxx.bteconosur.bteConoSur.mainWorld;
import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;

public class Tree {

    private String name;
    private Integer xOffset;
    private Integer yOffset;
    private Integer zOffset;
    private File schematic;

    // CONSTRUCTOR
    public Tree(String name) throws Exception {
        this.name = name;

        if (new YamlManager(pluginFolder, "trees/data.yml").getValue(name) != null) {
            Map<String, Object> data = (Map<String, Object>) new YamlManager(pluginFolder, "trees/data.yml").getValue(name);
            xOffset = (Integer) data.get("xOffset");
            yOffset = (Integer) data.get("yOffset");
            zOffset = (Integer) data.get("zOffset");

            schematic = new File(pluginFolder, "trees/schematics/" + (String) data.get("schematic") + ".schematic");
        } else {
            throw new Exception("noSuchTree");
        }
    }

    // PLACE
    public void place(Vector loc, Player player) {
        com.sk89q.worldedit.entity.Player actor = new BukkitPlayer((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"), ((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")).getServerInterface(), player);
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(actor);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession((World) new BukkitWorld(mainWorld), localSession.getBlockChangeLimit());
        Clipboard clipboard;

        ClipboardFormat format = ClipboardFormat.SCHEMATIC;
        try {
            ClipboardReader reader = format.getReader(new FileInputStream(schematic));
            clipboard = reader.read(actor.getWorld().getWorldData());
            clipboard.setOrigin(new Vector(xOffset, yOffset, zOffset));
            ClipboardHolder holder = new ClipboardHolder(clipboard, actor.getWorld().getWorldData());
            Region region = clipboard.getRegion();

            // PASTE SCHEMATIC

            // Get Maxs and Mins
            int xMax = clipboard.getMaximumPoint().getBlockX();
            int yMax = clipboard.getMaximumPoint().getBlockY();
            int zMax = clipboard.getMaximumPoint().getBlockZ();

            for (BlockVector p : region) {
                int x = loc.getBlockX() + p.getBlockX() - xMax + xOffset;
                int y = loc.getBlockY() + p.getBlockY() - yMax + yOffset;
                int z = loc.getBlockZ() + p.getBlockZ() - zMax + zOffset;
                Vector newVector = new Vector(x, y + clipboard.getDimensions().getBlockY() - 1, z);
                editSession.setBlock(newVector, clipboard.getBlock(p));
            }
            localSession.remember(editSession);
        } catch (IOException | MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }
}
