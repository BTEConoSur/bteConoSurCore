package pizzaaxx.bteconosur.projects;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import jdk.jfr.StackTrace;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.gui.ItemBuilder;
import pizzaaxx.bteconosur.gui.inventory.ConfirmActionGUI;
import pizzaaxx.bteconosur.gui.inventory.InventoryClickAction;
import pizzaaxx.bteconosur.gui.inventory.InventoryGUI;
import pizzaaxx.bteconosur.gui.inventory.PaginatedGUI;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplay;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplayProvider;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardManager;
import pizzaaxx.bteconosur.utils.SQLUtils;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;
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
            case 4 -> scoreboardManager.setTemporaryDisplay(this.getDisplay("Ve las solicitudes de unión a este proyecto."));
            case 5 -> scoreboardManager.setTemporaryDisplay(this.getDisplay(""));
            case 6 -> scoreboardManager.setTemporaryDisplay(this.getDisplay("Marca el proyecto como terminado"));
            case 7 -> scoreboardManager.setTemporaryDisplay(this.getDisplay(""));
            case 8 -> scoreboardManager.setTemporaryDisplay(this.getDisplay("Abandona el proyecto."));
        }
    }



    public void onManageClick(int slot) {
        Project project = plugin.getProjectsRegistry().get(projectID);
        switch (slot) {
            case 2 -> {
                if (!project.getOwner().equals(player.getUniqueId())) {
                    player.sendActionBar("§cNo puedes hacer esto.");
                    return;
                }
                PaginatedGUI gui = PaginatedGUI.fullscreen(
                        Component.text("Transferir proyecto"),
                        false
                );
                // a project can only be transferred to online members of the project
                for (UUID uuid : project.getMembers()) {
                    OfflineServerPlayer s = plugin.getPlayerRegistry().get(uuid);
                    if (s.isOnline()) {
                        Consumer<InventoryClickEvent> eventConsumer = event -> {
                            try {
                                project.getEditor().transfer(uuid);
                                event.getWhoClicked().closeInventory();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage(PREFIX + "Ha ocurrido un error al transferir el proyecto.");
                            }
                        };

                        gui.addItem(
                                ItemBuilder.head(
                                        uuid,
                                        "§a§l" + StringUtils.transformToSmallCapital(s.getName()),
                                        List.of(
                                                Component.text("§a[•] ", TextColor.color(GRAY))
                                                        .append(Component.text(StringUtils.transformToSmallCapital("Haz click para transferir el proyecto a este jugador."), TextColor.color(GRAY)).decoration(TextDecoration.ITALIC, false))
                                        )
                                ),
                                InventoryClickAction.of(eventConsumer)
                        );
                    }
                }
                plugin.getInventoryHandler().openInventory(player.getUniqueId(), gui);
            }
            case 3 -> {
                if (!project.getOwner().equals(player.getUniqueId())) {
                    player.sendActionBar("§cNo puedes hacer esto.");
                    return;
                }
                PaginatedGUI gui = PaginatedGUI.fullscreen(
                        Component.text("Miembros"),
                        false
                );

                Consumer<InventoryClickEvent> addMembersConsumer = event -> {
                    PaginatedGUI addMembersGUI = PaginatedGUI.fullscreen(
                            Component.text("Añadir miembros"),
                            false
                    );

                    Bukkit.getOnlinePlayers().stream()
                                    .filter(player -> !project.getMembers().contains(player.getUniqueId()))
                                            .forEach(
                                                    player -> {
                                                        Consumer<InventoryClickEvent> eventConsumer = event1 -> {
                                                            try {
                                                                project.getEditor().addMember(player.getUniqueId());
                                                                event1.getWhoClicked().closeInventory();
                                                                this.onManageClick(3); // reopen inventory with updated members
                                                            } catch (SQLException e) {
                                                                e.printStackTrace();
                                                                event1.getWhoClicked().closeInventory();
                                                                event1.getWhoClicked().sendMessage(PREFIX + "Ha ocurrido un error al añadir al jugador al proyecto.");
                                                            }
                                                        };

                                                        addMembersGUI.addItem(
                                                                ItemBuilder.head(
                                                                        player.getUniqueId(),
                                                                        "§a§l" + StringUtils.transformToSmallCapital(player.getName()),
                                                                        List.of(
                                                                                Component.text("§a[•] ", TextColor.color(GRAY))
                                                                                        .append(Component.text(StringUtils.transformToSmallCapital("Haz click para añadir a este jugador al proyecto."), TextColor.color(GRAY)).decoration(TextDecoration.ITALIC, false))
                                                                        )
                                                                ),
                                                                InventoryClickAction.of(eventConsumer)
                                                        );
                                                    }
                                            );

                    plugin.getInventoryHandler().openInventory(player.getUniqueId(), addMembersGUI);
                };

                gui.addStaticItem(
                        49,
                        MEMBERS_HEAD,
                        InventoryClickAction.of(addMembersConsumer)
                );

                // add head of each member excluding owner
                for (UUID uuid : project.getMembers()) {

                    OfflineServerPlayer s = plugin.getPlayerRegistry().get(uuid);

                    List<Component> lore = s.getLore();
                    lore.add(
                            StringUtils.deserialize("§c[•] ")
                                    .append(Component.text(StringUtils.transformToSmallCapital("Haz click para quitar a este jugador del proyecto."), TextColor.color(GRAY)).decoration(TextDecoration.ITALIC, false))
                    );

                    Consumer<InventoryClickEvent> eventConsumer = event -> {
                        try {
                            project.getEditor().removeMember(uuid);
                            event.getWhoClicked().closeInventory();
                            this.onManageClick(3); // reopen inventory with updated members
                        } catch (SQLException e) {
                            e.printStackTrace();
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage(PREFIX + "Ha ocurrido un error al quitar al jugador del proyecto.");
                        }
                    };

                    gui.addItem(
                            ItemBuilder.head(
                                    uuid,
                                    "§a§l" + StringUtils.transformToSmallCapital(s.getName()),
                                    lore
                            ),
                            InventoryClickAction.of(eventConsumer)
                    );
                }
                plugin.getInventoryHandler().openInventory(player.getUniqueId(), gui);
            }
            case 4 -> {
                if (!project.getOwner().equals(player.getUniqueId())) {
                    player.sendActionBar("§cNo puedes hacer esto.");
                    return;
                }
                // get requests from database (table "project_join_requests") and display them in a paginated gui
                PaginatedGUI gui = PaginatedGUI.fullscreen(
                        Component.text("Solicitudes de unión"),
                        false
                );

                try (SQLResult result = plugin.getSqlManager().select(
                        "project_join_requests",
                        new SQLColumnSet("*"),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition("project_id", "=", projectID)
                        )
                ).retrieve()) {

                    ResultSet set = result.getResultSet();

                    while (set.next()) {
                        UUID uuid = SQLUtils.uuidFromBytes(set.getBytes("player_id"));
                        OfflineServerPlayer s = plugin.getPlayerRegistry().get(uuid);

                        List<Component> lore = s.getLore();
                        lore.add(
                                StringUtils.deserialize("§a[•] ")
                                        .append(Component.text(StringUtils.transformToSmallCapital("Haz click para aceptar a este jugador en el proyecto."), TextColor.color(GRAY)).decoration(TextDecoration.ITALIC, false))
                        );

                        Consumer<InventoryClickEvent> eventConsumer = event -> {
                            try {
                                project.getEditor().addMember(uuid);
                                plugin.getSqlManager().delete(
                                        "project_join_requests",
                                        new SQLANDConditionSet(
                                                new SQLOperatorCondition("project_id", "=", projectID),
                                                new SQLOperatorCondition("player_id", "=", uuid)
                                        )
                                ).execute();
                                event.getWhoClicked().closeInventory();
                                this.onManageClick(4); // reopen inventory with updated requests
                            } catch (SQLException e) {
                                e.printStackTrace();
                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage(PREFIX + "Ha ocurrido un error al aceptar la solicitud de unión.");
                            }
                        };

                        gui.addItem(
                                ItemBuilder.head(
                                        uuid,
                                        "§a§l" + StringUtils.transformToSmallCapital(s.getName()),
                                        lore
                                ),
                                InventoryClickAction.of(eventConsumer)
                        );
                    }

                    plugin.getInventoryHandler().openInventory(player.getUniqueId(), gui);

                } catch (SQLException e) {
                    e.printStackTrace();
                    player.sendMessage(PREFIX + "Ha ocurrido un error al cargar las solicitudes de unión.");
                }
            }
            case 6 -> {
                if (!project.getOwner().equals(player.getUniqueId())) {
                    player.sendActionBar("§cNo puedes hacer esto.");
                    return;
                }
                ConfirmActionGUI gui = new ConfirmActionGUI(
                        Component.text("¿Finalizar proyecto?"),
                        () -> {
                            try {
                                project.getEditor().finish();
                                player.closeInventory();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                player.closeInventory();
                                player.sendMessage(PREFIX + "Ha ocurrido un error al finalizar el proyecto.");
                            }
                        },
                        () -> {
                            player.closeInventory();
                            this.onManageClick(6);
                        }
                );
                plugin.getInventoryHandler().openInventory(player.getUniqueId(), gui);
            }
            case 8 -> {
                if (project.getOwner().equals(player.getUniqueId())) {
                    ConfirmActionGUI gui = new ConfirmActionGUI(
                            Component.text("¿Abandonar proyecto?"),
                            () -> {
                                try {
                                    project.getEditor().ownerLeave();
                                    player.closeInventory();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    player.closeInventory();
                                    player.sendMessage(PREFIX + "Ha ocurrido un error al abandonar el proyecto.");
                                }
                            },
                            () -> {
                                player.closeInventory();
                                this.onManageClick(8);
                            }
                    );
                    gui.setConfirmLore(
                            List.of(
                                    Component.text("Si abandonas el proyecto siendo el líder, se removerá a todos los miembros y el proyecto quedará disponible nuevamente.", TextColor.color(255, 0, 0))
                                            .decoration(TextDecoration.ITALIC, false)
                            )
                    );
                    plugin.getInventoryHandler().openInventory(player.getUniqueId(), gui);
                } else {
                    ConfirmActionGUI gui = new ConfirmActionGUI(
                            Component.text("¿Abandonar proyecto?"),
                            () -> {
                                try {
                                    project.getEditor().memberLeave(player.getUniqueId());
                                    player.closeInventory();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    player.closeInventory();
                                    player.sendMessage(PREFIX + "Ha ocurrido un error al abandonar el proyecto.");
                                }
                            },
                            () -> {
                                player.closeInventory();
                                this.onManageClick(8);
                            }
                    );
                    plugin.getInventoryHandler().openInventory(player.getUniqueId(), gui);
                }
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
