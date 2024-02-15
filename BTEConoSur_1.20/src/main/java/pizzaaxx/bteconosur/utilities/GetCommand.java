package pizzaaxx.bteconosur.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.Light;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.gui.Heads;
import pizzaaxx.bteconosur.gui.ItemBuilder;
import pizzaaxx.bteconosur.gui.inventory.InventoryClickAction;
import pizzaaxx.bteconosur.gui.inventory.PaginatedGUI;

import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class GetCommand implements CommandExecutor, Listener {

    private final BTEConoSurPlugin plugin;

    private final List<String> headNames = List.of(
            "§fCabeza de pistón",
            "§fCabeza de pistón pegajoso",
            "§fTrampilla de hierro",
            "§fChampiñón"
    );

    public GetCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getPlayerClickEvent().registerBlockingCondition(
                2,
                // player right clicked block with head
                event -> event.getAction() == Action.RIGHT_CLICK_BLOCK
                        && event.getClickedBlock() != null
                        && event.getClickedBlock().isBuildable()
                        && event.getItem() != null
                        && event.getItem().getType() == Material.PLAYER_HEAD
                        && event.getItem().getItemMeta().hasDisplayName()
                        && headNames.contains(((TextComponent) event.getItem().getItemMeta().displayName()).content()),
                event -> {
                    event.setCancelled(true);
                    ItemStack item = event.getItem();
                    assert item != null;
                    BlockFace blockFace = event.getBlockFace();
                    Block clickedBlock = event.getClickedBlock();
                    assert clickedBlock != null;
                    Block targetBlock = clickedBlock.getRelative(blockFace);
                    switch (((TextComponent) event.getItem().getItemMeta().displayName()).content()) {
                        case "§fCabeza de pistón" -> {
                            targetBlock.setType(Material.PISTON_HEAD);
                            PistonHead head = (PistonHead) targetBlock.getBlockData();
                            head.setFacing(blockFace);
                            targetBlock.setBlockData(head);
                        }
                        case "§fCabeza de pistón pegajoso" -> {
                            targetBlock.setType(Material.PISTON_HEAD);
                            PistonHead head = (PistonHead) targetBlock.getBlockData();
                            head.setFacing(blockFace);
                            head.setType(PistonHead.Type.STICKY);
                            targetBlock.setBlockData(head);
                        }
                        case "§fTrampilla de hierro" -> {
                            targetBlock.setType(Material.IRON_TRAPDOOR);
                            if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
                                TrapDoor trapDoor = (TrapDoor) targetBlock.getBlockData();
                                trapDoor.setHalf(blockFace == BlockFace.UP ? TrapDoor.Half.BOTTOM : TrapDoor.Half.TOP);
                                targetBlock.setBlockData(trapDoor);
                            } else {
                                TrapDoor trapDoor = (TrapDoor) targetBlock.getBlockData();
                                trapDoor.setHalf(TrapDoor.Half.BOTTOM);
                                trapDoor.setFacing(blockFace);
                                trapDoor.setOpen(true);
                                targetBlock.setBlockData(trapDoor);
                            }
                        }
                        case "§fChampiñón" -> {
                            targetBlock.setType(Material.RED_MUSHROOM_BLOCK);
                            MultipleFacing mushroom = (MultipleFacing) targetBlock.getBlockData();
                            // set all faces to false
                            mushroom.setFace(BlockFace.UP, false);
                            mushroom.setFace(BlockFace.DOWN, false);
                            mushroom.setFace(BlockFace.NORTH, false);
                            mushroom.setFace(BlockFace.EAST, false);
                            mushroom.setFace(BlockFace.SOUTH, false);
                            mushroom.setFace(BlockFace.WEST, false);
                            targetBlock.setBlockData(mushroom);
                        }
                    }
                }
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        PaginatedGUI gui = PaginatedGUI.fullscreen(
                Component.text("Bloques especiales"),
                true
        );
        for (int i = 0; i <= 15; i++) {
            ItemStack lightBlock = new ItemStack(Material.LIGHT);
            BlockDataMeta blockDataMeta = (BlockDataMeta) lightBlock.getItemMeta();
            Light light = (Light) blockDataMeta.getBlockData(Material.LIGHT);
            light.setLevel(i);
            blockDataMeta.setBlockData(light);
            lightBlock.setItemMeta(blockDataMeta);
            blockDataMeta.displayName(
                    Component.text("Bloque de luz (" + i + ")", WHITE)
            );

            gui.addItem(
                    lightBlock,
                    null
            );
        }

        gui.addItem(
                ItemBuilder.of(
                        Material.BARRIER
                ).name("§fBarrera").build(),
                null
        );

        gui.addItem(
                ItemBuilder.of(
                        Material.BARRIER
                ).name("§fVacío").build(),
                null
        );

        gui.addItem(
                ItemBuilder.head(
                        Heads.PISTON_VALUE,
                        "§fCabeza de pistón",
                        null
                ),
                InventoryClickAction.EMPTY
        );

        gui.addItem(
                ItemBuilder.head(
                        Heads.STICKY_PISTON_VALUE,
                        "§fCabeza de pistón pegajoso",
                        null
                ),
                InventoryClickAction.EMPTY
        );

        gui.addItem(
                ItemBuilder.head(
                        Heads.IRON_TRAPDOOR_VALUE,
                        "§fTrampilla de hierro",
                        null
                ),
                InventoryClickAction.EMPTY
        );

        gui.addItem(
                ItemBuilder.head(
                        Heads.MUSHROOM_VALUE,
                        "§fChampiñón",
                        null
                ),
                InventoryClickAction.EMPTY
        );

        plugin.getInventoryHandler().openInventory(
                player.getUniqueId(),
                gui
        );

        return true;
    }
}
