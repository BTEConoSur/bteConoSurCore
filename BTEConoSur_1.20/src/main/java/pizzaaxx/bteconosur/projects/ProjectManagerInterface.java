package pizzaaxx.bteconosur.projects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.gui.ItemBuilder;
import pizzaaxx.bteconosur.gui.inventory.InventoryClickAction;
import pizzaaxx.bteconosur.gui.inventory.InventoryGUI;
import pizzaaxx.bteconosur.gui.inventory.PaginatedGUI;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplay;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplayProvider;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardManager;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static pizzaaxx.bteconosur.utils.ChatUtils.DARK_GRAY;
import static pizzaaxx.bteconosur.utils.ChatUtils.GRAY;

public class ProjectManagerInterface {

    public static final ItemStack INFO_HEAD = ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmEyYWZhN2JiMDYzYWMxZmYzYmJlMDhkMmM1NThhN2RmMmUyYmFjZGYxNWRhYzJhNjQ2NjJkYzQwZjhmZGJhZCJ9fX0=",
            StringUtils.transformToSmallCapital("Información"),
            null
    );

    public static final ItemStack TRANSER_HEAD = ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTFlOTRlMTEzMDIwMWFmMWEzMDYyZjYyZDQyZWNiYjU1ZDU5ZTRjYWJiMzQzNWEwZTAwNjJiYzAwOGNiMDk4NSJ9fX0=",
            StringUtils.transformToSmallCapital("Transferir"),
            null
    );

    public static final ItemStack MEMBERS_HEAD = ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19",
            StringUtils.transformToSmallCapital("Miembros"),
            null
    );

    public static final ItemStack REQUESTS_HEAD = ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZiMGNlNjczYjNmMjhjNDYxMGNlYTdjZTA0MmM4NTBlMzRjYzk4OGNiMGQ3YzgwMzk3OWY1MGRkMGYxNTczMSJ9fX0=",
            StringUtils.transformToSmallCapital("Solicitudes"),
            null
    );

    public static final ItemStack FINISH_HEAD = ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=",
            StringUtils.transformToSmallCapital("Finalizar"),
            null
    );

    public static final ItemStack LEAVE_HEAD = ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZiZjE0MDJhMDQwNjRjZWJhYTk2Yjc3ZDU0NTVlZTkzYjY4NTMzMmUyNjRjODBjYTM2NDE1ZGY5OTJmYjQ2YyJ9fX0=",
            StringUtils.transformToSmallCapital("Abandonar"),
            null
    );

    public static final ItemStack EMPTY_SLOT = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build();

    private final BTEConoSurPlugin plugin;
    private final Player player;
    private final String projectID;
    private final ScoreboardManager scoreboardManager;

    private final ItemStack[] original = new ItemStack[9];
    private final ScoreboardDisplay originalDisplay;

    public ProjectManagerInterface(@NotNull BTEConoSurPlugin plugin, @NotNull Player player, String projectID) {
        this.plugin = plugin;
        this.player = player;
        this.projectID = projectID;
        OnlineServerPlayer osp = (OnlineServerPlayer) plugin.getPlayerRegistry().get(player.getUniqueId());
        this.scoreboardManager = osp.getScoreboardManager();
        this.originalDisplay = scoreboardManager.getCurrentDisplay();
    }

    public void init() throws SQLException {
        scoreboardManager.setDisplay(this.getDisplay("Estás editando el proyecto §*§~" + projectID + "§]. Para §csalir§r usa §*/project manage§] nuevamente."));

        // save previous inventory
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null) {
                original[i] = item.clone();
            }
            original[i] = null;
        }

        Inventory inventory = player.getInventory();

        // INFO
        //
        // TRANSFER
        // MEMBERS
        // REQUESTS
        //
        // FINISH
        //
        // LEAVE

        inventory.setItem(0, INFO_HEAD);
        inventory.setItem(1, EMPTY_SLOT);
        inventory.setItem(2, TRANSER_HEAD);
        inventory.setItem(3, MEMBERS_HEAD);
        inventory.setItem(4, REQUESTS_HEAD);
        inventory.setItem(5, EMPTY_SLOT);
        inventory.setItem(6, FINISH_HEAD);
        inventory.setItem(7, EMPTY_SLOT);
        inventory.setItem(8, LEAVE_HEAD);
    }

    public void disable() {
        // restore previous inventory
        for (int i = 0; i < 9; i++) {
            ItemStack item = original[i];
            player.getInventory().setItem(i, item);
        }

        scoreboardManager.setTemporaryDisplay(originalDisplay);
    }

    public void onChangeSlot(int slot) {
        switch (slot) {
            case 0 -> {
                Project project = plugin.getProjectsRegistry().get(projectID);
                scoreboardManager.setTemporaryDisplay(project);
            }
            case 1 -> scoreboardManager.setTemporaryDisplay(this.getDisplay(""));
            case 2 -> scoreboardManager.setTemporaryDisplay(this.getDisplay("Transfiere el proyecto a otro jugador."));
            case 3 -> scoreboardManager.setTemporaryDisplay(this.getDisplay("Maneja los miembros de este proyecto."));
            case 4 -> scoreboardManager.setTemporaryDisplay(this.getDisplay("ve las solicitudes de unión a este proyecto."));
            case 5 -> scoreboardManager.setTemporaryDisplay(this.getDisplay(""));
            case 6 -> scoreboardManager.setTemporaryDisplay(this.getDisplay("Marca el proyecto como terminado"));
            case 7 -> scoreboardManager.setTemporaryDisplay(this.getDisplay(""));
            case 8 -> scoreboardManager.setTemporaryDisplay(this.getDisplay("Abandona el proyecto."));
        }
    }



    public void onManageClick(int slot) {
        switch (slot) {
            case 2 -> {

            }
            case 3 -> {
                PaginatedGUI gui = PaginatedGUI.fullscreen(
                        Component.text("Miembros"),
                        false
                );

                // add head of each member excluding owner
                Project project = plugin.getProjectsRegistry().get(projectID);
                for (UUID uuid : project.getMembers()) {

                    OfflineServerPlayer s = plugin.getPlayerRegistry().get(uuid);

                    List<Component> lore = s.getLore();
                    lore.add(
                            StringUtils.deserialize("§c[•] ")
                                    .append(Component.text(StringUtils.transformToSmallCapital("Haz click para quitar a este jugador del proyecto."), TextColor.color(GRAY)).decoration(TextDecoration.ITALIC, false))
                    );

                    gui.addItem(
                            ItemBuilder.head(
                                    uuid,
                                    "§a§l" + StringUtils.transformToSmallCapital(s.getName()),
                                    lore
                            ),
                            InventoryClickAction.EMPTY
                    );
                }
                plugin.getInventoryHandler().openInventory(player.getUniqueId(), gui);
            }
        }
    }

    @Contract(value = "_ -> new", pure = true)
    private @NotNull ScoreboardDisplay getDisplay(String text) {
        return new ScoreboardDisplay() {

            @Override
            public Component getTitle() {
                Project project = plugin.getProjectsRegistry().get(projectID);
                return Component.text(StringUtils.transformToSmallCapital("Proyecto " + project.getDisplayName()), Style.style(TextColor.color(project.getType().getColor().getRGB()), TextDecoration.BOLD));
            }

            @Override
            public List<Component> getLines() {
                List<String> wrappedLines = Arrays.stream(WordUtils.wrap(text, 22).split("\n")).toList();
                List<Component> list = new java.util.ArrayList<>(List.of(
                        Component.text("◆")
                                .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                                .append(Component.text("◆"))
                                .color(TextColor.color(DARK_GRAY)),

                        Component.empty()
                ));

                for (String line : wrappedLines) {
                    list.add(Component.text("  ")
                            .append(LegacyComponentSerializer.legacyAmpersand().deserialize(StringUtils.transformToSmallCapital(line).replace("*", "a").replace("~", "l").replace("]", "r"))));
                }

                list.addAll(
                        List.of(
                                Component.empty(),

                                Component.text("◆")
                                        .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                                        .append(Component.text("◆"))
                                        .color(TextColor.color(DARK_GRAY))
                        )
                );
                return list;
            }

            @Override
            public ScoreboardDisplayProvider getProvider() {
                return null;
            }

            @Override
            public boolean isSavable() {
                return false;
            }
        };
    }
}
