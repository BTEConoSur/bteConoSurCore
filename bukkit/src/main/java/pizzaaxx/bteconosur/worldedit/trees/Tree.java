package pizzaaxx.bteconosur.worldedit.trees;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pizzaaxx.bteconosur.configuration.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.getEditSession;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class Tree {

    public static String treePrefix = "§f[§2ÁRBOLES§f] §7>>§r ";

    private final String name;
    private final Integer xOffset;
    private final Integer yOffset;
    private final Integer zOffset;
    private final File schematic;

    // CONSTRUCTOR
    public Tree(String name) throws Exception {
        this.name = name;

        Configuration data = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "trees/data");
        if (data.contains(name)) {
            ConfigurationSection tree = data.getConfigurationSection(name);
            xOffset = tree.getInt("xOffset");
            yOffset = tree.getInt("yOffset");
            zOffset = tree.getInt("zOffset");

            schematic = new File(pluginFolder, "trees/schematics/" + data.get("schematic") + ".schematic");
        } else {
            throw new Exception("noSuchTree");
        }
    }

    public Tree(ItemStack item) throws Exception {
        if (item.getType() == Material.SAPLING) {
            if (item.getItemMeta().hasDisplayName() && ChatColor.stripColor(item.getItemMeta().getDisplayName()).startsWith("Árbol: ")) {
                String name = ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace("Árbol: ", "");

                this.name = name;

                Configuration data = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "trees/data");
                if (data.contains(name)) {
                    ConfigurationSection tree = data.getConfigurationSection(name);
                    xOffset = tree.getInt("xOffset");
                    yOffset = tree.getInt("yOffset");
                    zOffset = tree.getInt("zOffset");

                    schematic = new File(pluginFolder, "trees/schematics/" + data.get("schematic") + ".schematic");
                } else {
                    throw new Exception("noSuchTree");
                }
            } else {
                throw new Exception("invalidItem");
            }
        } else {
            throw new Exception("invalidItem");
        }
    }

    // GETTER

    public String getName() {
        return this.name;
    }

    // PLACE
    public EditSession place(Vector loc, Player player, EditSession editSession) {
        if (editSession == null) {
            editSession = getEditSession(player);
        }

        com.sk89q.worldedit.entity.Player actor = new BukkitPlayer((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"), ((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")).getServerInterface(), player);
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(actor);
        Clipboard clipboard;

        ClipboardFormat format = ClipboardFormat.SCHEMATIC;
        try {
            ClipboardReader reader = format.getReader(new FileInputStream(schematic));
            clipboard = reader.read(actor.getWorld().getWorldData());
            clipboard.setOrigin(new Vector(xOffset, yOffset, zOffset));
            Region region = clipboard.getRegion();

            // PASTE SCHEMATIC

            // Get Maxs and Mins
            int xMax = clipboard.getMaximumPoint().getBlockX();
            int yMax = clipboard.getMaximumPoint().getBlockY();
            int zMax = clipboard.getMaximumPoint().getBlockZ();

            // MASK
            Mask mask = localSession.getMask();
            if (mask == null) {
                ParserContext parserContext = new ParserContext();
                parserContext.setActor(actor);
                Extent extent = actor.getExtent();
                if (extent instanceof World) {
                    parserContext.setWorld((World) extent);
                }
                parserContext.setSession(WorldEdit.getInstance().getSessionManager().get(actor));

                mask = WorldEdit.getInstance().getMaskFactory().parseFromInput("0", parserContext);
            }

            // ROTATION
            int degrees = new Random().nextInt(4);

            // ORIGEN
            int xOg = loc.getBlockX();
            int zOg = loc.getBlockZ();

            for (BlockVector p : region) {
                if (clipboard.getBlock(p).getType() != 0) {
                    int x = loc.getBlockX() + p.getBlockX() - xMax + xOffset;
                    int y = loc.getBlockY() + p.getBlockY() - yMax + yOffset;
                    int z = loc.getBlockZ() + p.getBlockZ() - zMax + zOffset;

                    int x0 = x - xOg;
                    int z0 = z - zOg;
                    if (degrees == 1) {
                        x = z0 + xOg;
                        z = -x0 + zOg;
                    } else if (degrees == 2) {
                        x = -x0 + xOg;
                        z = -z0 + zOg;
                    } else if (degrees == 3) {
                        x = -z0 + xOg;
                        z = x0 + zOg;
                    }

                    Vector newVector = new Vector(x, y + clipboard.getDimensions().getBlockY() - 1, z);
                    if (mask.test(newVector) &&  getWorldGuard().canBuild(player, mainWorld.getBlockAt(newVector.getBlockX(), newVector.getBlockY(), newVector.getBlockZ()))) {
                        editSession.setBlock(newVector, clipboard.getBlock(p));
                    }
                }
            }
            return editSession;
        } catch (IOException | MaxChangedBlocksException | InputParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
