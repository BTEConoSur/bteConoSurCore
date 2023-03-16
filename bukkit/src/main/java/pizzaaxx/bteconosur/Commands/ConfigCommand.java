package pizzaaxx.bteconosur.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Chat;
import pizzaaxx.bteconosur.Chat.ProjectChat;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Inventory.CustomSlotsPaginatedGUI;
import pizzaaxx.bteconosur.Inventory.InventoryGUI;
import pizzaaxx.bteconosur.Inventory.ItemBuilder;
import pizzaaxx.bteconosur.Player.Managers.ChatManager;
import pizzaaxx.bteconosur.Player.Managers.ScoreboardManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Projects.ProjectTag;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLJSONArrayCondition;
import pizzaaxx.bteconosur.SQL.Conditions.SQLORConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigCommand implements CommandExecutor {

    // PAIS / CHAT / CHAT DEFAULT / OCULTAR CHAT / VISION NOCTURNA / OCULTAR SCOREBOARD / SCOREBOARD AUTOMATICO

    private final BTEConoSur plugin;

    public ConfigCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void openConfigMenu(@NotNull Player p) throws SQLException {

        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());

        ChatManager manager = s.getChatManager();

        InventoryGUI gui = new InventoryGUI(
                4,
                "Configuración"
        );

        {
            gui.setItem(
                    ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkYWExZTNlZDk0ZmYzZTMzZTFkNGM2ZTQzZjAyNGM0N2Q3OGE1N2JhNGQzOGU3NWU3YzkyNjQxMDYifX19",
                            "§aPaís",
                            Collections.singletonList("El país determina el prefijo que aparece en el chat y en el menú de tab.")
                    ),
                    10
            );
            gui.setItem(
                    ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjAyYWYzY2EyZDVhMTYwY2ExMTE0MDQ4Yjc5NDc1OTQyNjlhZmUyYjFiNWVjMjU1ZWU3MmI2ODNiNjBiOTliOSJ9fX0=",
                            "§aChat actual",
                            null
                            ),
                    11
            );
            gui.setItem(
                    ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWY2NDAyMjY1ZGFjYWNhM2FiNjMxNDNhOWM5NTA4YzcxOTFkYTgzMDEwZTk3NzA3YjQ3ZmIxMDkyY2ZhOTg3YSJ9fX0=",
                            "§aChat predeterminado",
                            Collections.singletonList(
                                    "El chat predeterminado es el chat al que te unirás automáticamente cada vez que entres al servidor."
                            )
                    ),
                    12
            );
            gui.setItem(
                    ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJmMDU4ZGRjMjk2OTEzMzI1OTFhYzU1YTBmZDczZjQzMjAxMTc5ODJjZmRiY2U3OTY5OTQxY2ZhOGVkOGM2YiJ9fX0=",
                            "§aOcultar/Mostrar chat",
                            null
                    ),
                    13
            );
            gui.setItem(
                    ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTU4NjlmZDYzNTZkYjA3M2JhZGFlNzZkMTQzNTVkZjM1NGI5NzZjOWExMWIwNjMxZWY3NDc4ZTgyNmRhNTE5MCJ9fX0=",
                            "§aVisión nocturna",
                            null
                    ),
                    14
            );
            gui.setItem(
                    ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODVlNWJmMjU1ZDVkN2U1MjE0NzQzMTgwNTBhZDMwNGFiOTViMDFhNGFmMGJhZTE1ZTVjZDljMTk5M2FiY2M5OCJ9fX0=",
                            "§aOcultar/Mostrar scoreboard",
                            null
                    ),
                    15
            );
            gui.setItem(
                    ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM2MTExNGMxZWNiNThhNjVkOTQ5N2M4ZmQ4YmJhNjlmNTk1ZjY3ODc4NTU5YjE2MmY1YmM1ODVmNGZlY2JmMyJ9fX0=",
                            "§aActivar/Desactivar scoreboard automático",
                            null
                    ),
                    16
            );

        } // FIRST ROW

        {

            // PAÍS
            {
                Country country = null;
                for (Country c : plugin.getCountryManager().getAllCountries()) {
                    if (c.getChatPrefix().equals(manager.getCountryPrefix())) {
                        country = c;
                        break;
                    }
                }

                if (country == null) {
                    p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                    return;
                }

                gui.setItem(
                        ItemBuilder.head(
                                country.getHeadValue(),
                                "§a" + country.getDisplayName(),
                                Collections.singletonList("Prefijo: " + country.getChatPrefix())
                        ),
                        19
                );

                gui.setLCAction(
                        event -> this.openConfigCountryMenu(p),
                        19
                );
            }

            // CHAT ACTUAL
            {

                Chat currentChat = manager.getCurrentChat();

                gui.setItem(
                        currentChat.getHead(),
                        20
                );

                gui.setLCAction(
                        event -> {
                            try {
                                this.openConfigChatMenu(p);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                            }
                        },
                        20
                );

            }

            // CHAT DEFAULT
            {
                Chat defaultChat = manager.getDefaultChat();

                gui.setItem(
                        defaultChat.getHead(),
                        21
                );

                gui.setLCAction(
                        event -> {
                            try {
                                this.openConfigDefaultChatMenu(p);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                            }
                        },
                        21
                );
            }

            // OCULTAR CHAT
            {
                gui.setItem(
                        ItemBuilder.of(
                                        Material.INK_SACK,
                                        1,
                                        (manager.isHidden() ? 5 : 14)
                                )
                                .name((manager.isHidden()) ? "§cEl chat está oculto." : "§aEl chat es visible.")
                                .lore(Collections.singletonList("§6[•]§7 Haz click para " + (manager.isHidden() ? "mostrar" : "ocultar") + " el chat."))
                                .build(),
                        22
                );

                gui.setLCAction(
                        event -> {
                            try {
                                manager.toggleHidden();

                                event.updateSlot(
                                        ItemBuilder.of(
                                                        Material.INK_SACK,
                                                        1,
                                                        (manager.isHidden() ? 5 : 14)
                                                )
                                                .name((manager.isHidden()) ? "§cEl chat está oculto." : "§aEl chat es visible.")
                                                .lore(Collections.singletonList("[•] Haz click para " + (manager.isHidden() ? "mostrar" : "ocultar") + " el chat."))
                                                .build()
                                );
                            } catch (SQLException e) {
                                e.printStackTrace();
                                event.closeGUI();
                                p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                            }
                        },
                        22
                );
            }

            // VISIÓN NOCTURNA
            {
                gui.setItem(
                        ItemBuilder.of(
                                Material.INK_SACK,
                                1,
                                (p.hasPotionEffect(PotionEffectType.NIGHT_VISION) ? 5 : 14)
                        )
                                .name((p.hasPotionEffect(PotionEffectType.NIGHT_VISION) ? "§aLa visión nocturna está activada." : "La visión nocturna está descativada."))
                                .lore(Collections.singletonList("[•] Haz click para " + (p.hasPotionEffect(PotionEffectType.NIGHT_VISION) ? "desactivar" : "activar") + " la visión nocturna."))
                                .build(),
                        23
                );

                gui.setLCAction(
                        event -> {
                            if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                                p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            } else {
                                p.addPotionEffect(new PotionEffect(
                                        PotionEffectType.NIGHT_VISION,
                                        999999,
                                        1
                                ));
                            }

                            event.updateSlot(
                                    ItemBuilder.of(
                                                    Material.INK_SACK,
                                                    1,
                                                    (p.hasPotionEffect(PotionEffectType.NIGHT_VISION) ? 5 : 14)
                                            )
                                            .name((p.hasPotionEffect(PotionEffectType.NIGHT_VISION) ? "§aLa visión nocturna está activada." : "La visión nocturna está descativada."))
                                            .lore(Collections.singletonList("[•] Haz click para " + (p.hasPotionEffect(PotionEffectType.NIGHT_VISION) ? "desactivar" : "activar") + " la visión nocturna."))
                                            .build()
                            );
                        },
                        23
                );
            }

            ScoreboardManager scoreboardManager = s.getScoreboardManager();

            // OCULTAR SCOREBOARD
            {

                gui.setItem(
                        ItemBuilder.of(
                                        Material.INK_SACK,
                                        1,
                                        (!scoreboardManager.isHidden() ? 5 : 14)
                                )
                                .name((!scoreboardManager.isHidden() ? "§aEl scoreboard está visible." : "§aEl scoreboard está oculto."))
                                .lore(Collections.singletonList("[•] Haz click para " + (!scoreboardManager.isHidden() ? "ocultar" : "mostrar") + " el scoreboard."))
                                .build(),
                        23
                );

                gui.setLCAction(
                        event -> {
                            try {
                                scoreboardManager.setHidden(!scoreboardManager.isHidden());

                                event.updateSlot(
                                        ItemBuilder.of(
                                                        Material.INK_SACK,
                                                        1,
                                                        (!scoreboardManager.isHidden() ? 5 : 14)
                                                )
                                                .name((!scoreboardManager.isHidden() ? "§aEl scoreboard está visible." : "§aEl scoreboard está oculto."))
                                                .lore(Collections.singletonList("[•] Haz click para " + (!scoreboardManager.isHidden() ? "ocultar" : "mostrar") + " el scoreboard."))
                                                .build()
                                );
                            } catch (SQLException e) {
                                e.printStackTrace();
                                event.closeGUI();
                                p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                            }

                        },
                        23
                );
            }

            // SCOREBOARD AUTOMÁTICO
            {

                gui.setItem(
                        ItemBuilder.of(
                                        Material.INK_SACK,
                                        1,
                                        (!scoreboardManager.isAuto() ? 5 : 14)
                                )
                                .name((!scoreboardManager.isAuto() ? "§aEl scoreboard automático está activado." : "§aEl scoreboard automático está desactivado."))
                                .lore(Collections.singletonList("[•] Haz click para " + (!scoreboardManager.isAuto() ? "desactivar" : "activar") + " el scoreboard automático."))
                                .build(),
                        23
                );

                gui.setLCAction(
                        event -> {
                            try {
                                scoreboardManager.setAuto(!scoreboardManager.isAuto());

                                event.updateSlot(
                                        ItemBuilder.of(
                                                        Material.INK_SACK,
                                                        1,
                                                        (!scoreboardManager.isAuto() ? 5 : 14)
                                                )
                                                .name((!scoreboardManager.isAuto() ? "§aEl scoreboard automático está activado." : "§aEl scoreboard automático está desactivado."))
                                                .lore(Collections.singletonList("[•] Haz click para " + (!scoreboardManager.isAuto() ? "desactivar" : "activar") + " el scoreboard automático."))
                                                .build()
                                );
                            } catch (SQLException e) {
                                e.printStackTrace();
                                event.closeGUI();
                                p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                            }

                        },
                        23
                );
            }

        } // SECOND ROW

        plugin.getInventoryHandler().open(p, gui);

    }

    public void openConfigCountryMenu(@NotNull Player p) {

        CustomSlotsPaginatedGUI gui = new CustomSlotsPaginatedGUI(
                "Elige un país",
                3,
                new Integer[] {10, 11, 12, 13, 14, 15, 16},
                9, 17
        );

        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());

        ChatManager manager = s.getChatManager();

        gui.addPaginated(
                ItemBuilder.head(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkYWExZTNlZDk0ZmYzZTMzZTFkNGM2ZTQzZjAyNGM0N2Q3OGE1N2JhNGQzOGU3NWU3YzkyNjQxMDYifX19",
                        "§aInternacional",
                        Collections.singletonList(
                                "Prefijo: §7[INTERNACIONAL]"
                        )
                ),
                event -> {
                    try {
                        manager.setCountryPrefix("§7[INTERNACIONAL]");
                        manager.setCountryTabPrefix("§7[INT]");
                        this.openConfigMenu(p);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        p.sendMessage(plugin.getPrefix() + "Ha ocurrido un error en la base de datos.");
                        event.closeGUI();
                    }
                },
                null, null, null
        );

        for (Country country : plugin.getCountryManager().getAllCountries()) {

            gui.addPaginated(
                    ItemBuilder.head(
                            country.getHeadValue(),
                            "§a" + country.getDisplayName(),
                            Collections.singletonList(
                                    "Prefijo: " + country.getChatPrefix()
                            )
                    ),
                    event -> {
                        try {
                            manager.setCountryPrefix(country.getChatPrefix());
                            manager.setCountryTabPrefix(country.getTabPrefix());
                            this.openConfigMenu(p);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            p.sendMessage(plugin.getPrefix() + "Ha ocurrido un error en la base de datos.");
                            event.closeGUI();
                        }
                    },
                    null, null, null
            );

        }

        gui.openTo(p, plugin);

    }

    public void openConfigChatMenu(@NotNull Player p) throws SQLException {

        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());

        ChatManager manager = s.getChatManager();

        CustomSlotsPaginatedGUI gui = new CustomSlotsPaginatedGUI(
                "Elige un chat",
                6,
                new Integer[]{
                        18, 19, 20, 21, 22, 23, 24, 25, 26,
                        27, 28, 29, 30, 31, 32, 33, 34, 35,
                        36, 37, 38, 39, 40, 41, 42, 43, 44,
                        45, 46, 47, 48, 49, 50, 51, 52, 53
                },
                9, 17
        );

        Chat globalChat = plugin.getChatHandler().getChat("global");

        gui.addPaginated(
                globalChat.getHead(),
                event -> {
                    try {
                        if (manager.getCurrentChatName().equals("global")) {
                            this.openConfigMenu(p);
                            return;
                        }

                        Chat oldChat = manager.getCurrentChat();
                        oldChat.removePlayer(p.getUniqueId());

                        manager.setCurrentChat(globalChat);
                        globalChat.addPlayer(p.getUniqueId());
                        p.sendMessage(plugin.getChatHandler().getPrefix() + "Te has unido al chat §aGlobal§f.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                        event.closeGUI();
                    }
                },
                null, null, null
        );

        for (Country country : plugin.getCountryManager().getAllCountries()) {

            Chat countryChat = plugin.getChatHandler().getChat(country.getName());

            gui.addPaginated(
                    countryChat.getHead(),
                    event -> {
                        try {
                            if (manager.getCurrentChatName().equals(country.getName())) {
                                this.openConfigMenu(p);
                                return;
                            }

                            Chat oldChat = manager.getCurrentChat();
                            oldChat.removePlayer(p.getUniqueId());

                            manager.setCurrentChat(countryChat);
                            countryChat.addPlayer(p.getUniqueId());
                            p.sendMessage(plugin.getChatHandler().getPrefix() + "Te has unido al chat de §a" + country.getDisplayName() + "§f.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                            event.closeGUI();
                        }
                    },
                    null, null, null
            );
        }

        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "id", "name", "tag"
                ),
                new SQLORConditionSet(
                        new SQLOperatorCondition(
                                "owner", "=", p.getUniqueId()
                        ),
                        new SQLJSONArrayCondition(
                                "members", p.getUniqueId()
                        )
                )
        ).retrieve();

        while (set.next()) {

            String tagString = set.getString("tag");
            ProjectTag tag = (tagString != null ? ProjectTag.valueOf(tagString) : null);

            String name = set.getString("name");

            String id = set.getString("id");

            List<String> lore = new ArrayList<>();
            if (plugin.getChatHandler().isLoaded("project_" + id)) {
                Chat chat = plugin.getChatHandler().getChat("project_" + id);
                lore.add("Jugadores: §7" + chat.getPlayers().size());
            } else {
                lore.add("Jugadores: §70");
            }

            gui.addPaginated(
                    (tag != null ?
                            ItemBuilder.head(
                                    tag.getHeadValue(),
                                    "§aProyecto " + (name != null ? name : set.getString("id".toUpperCase())),
                                    lore
                            ) :
                            ItemBuilder.of(Material.MAP)
                                    .name("§aProyecto " + (name != null ? name : set.getString("id".toUpperCase())))
                                    .lore(lore)
                                    .build()
                    ),
                    event -> {
                        try {
                            if (manager.getCurrentChatName().equals("project_" + id)) {
                                this.openConfigMenu(p);
                                return;
                            }

                            Project project = plugin.getProjectRegistry().get(id);

                            if (!plugin.getChatHandler().isLoaded("project_" + id)) {
                                plugin.getChatHandler().registerChat(
                                        new ProjectChat(project, plugin.getChatHandler())
                                );
                            }

                            Chat projectChat = plugin.getChatHandler().getChat("project_" + id);

                            Chat oldChat = manager.getCurrentChat();
                            oldChat.removePlayer(p.getUniqueId());

                            manager.setCurrentChat(projectChat);
                            projectChat.addPlayer(p.getUniqueId());
                            p.sendMessage(plugin.getChatHandler().getPrefix() + "Te has unido al chat del proyecto §a" + project.getDisplayName() + "§f.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                            event.closeGUI();
                        }
                    },
                    null, null, null
            );

        }

        gui.openTo(p, plugin);

    }

    public void openConfigDefaultChatMenu(@NotNull Player p) throws SQLException {

        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());

        ChatManager manager = s.getChatManager();

        CustomSlotsPaginatedGUI gui = new CustomSlotsPaginatedGUI(
                "Elige un chat predeterminado",
                6,
                new Integer[]{
                        18, 19, 20, 21, 22, 23, 24, 25, 26,
                        27, 28, 29, 30, 31, 32, 33, 34, 35,
                        36, 37, 38, 39, 40, 41, 42, 43, 44,
                        45, 46, 47, 48, 49, 50, 51, 52, 53
                },
                9, 17
        );

        Chat globalChat = plugin.getChatHandler().getChat("global");

        gui.addPaginated(
                globalChat.getHead(),
                event -> {
                    try {
                        if (manager.getDefaultChatName().equals("global")) {
                            p.sendMessage(plugin.getChatHandler().getPrefix() + "Tu chat predeterminado ya es el chat §aGlobal§f.");
                            this.openConfigMenu(p);
                            return;
                        }

                        manager.setDefaultChat(globalChat);
                        p.sendMessage(plugin.getChatHandler().getPrefix() + "Chat predeterminado cambiado al chat §aGlobal§f.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                        event.closeGUI();
                    }
                },
                null, null, null
        );

        for (Country country : plugin.getCountryManager().getAllCountries()) {

            Chat countryChat = plugin.getChatHandler().getChat(country.getName());

            gui.addPaginated(
                    countryChat.getHead(),
                    event -> {
                        try {
                            if (manager.getDefaultChatName().equals(country.getName())) {
                                p.sendMessage(plugin.getChatHandler().getPrefix() + "Tu chat predeterminado ya es el chat de §a" + country.getDisplayName() + "§f.");
                                this.openConfigMenu(p);
                                return;
                            }

                            manager.setDefaultChat(countryChat);
                            p.sendMessage(plugin.getChatHandler().getPrefix() + "Chat predeterminado cambiado al chat de §a" + country.getDisplayName() + "§f.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                            event.closeGUI();
                        }
                    },
                    null, null, null
            );
        }

        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "id", "name", "tag"
                ),
                new SQLORConditionSet(
                        new SQLOperatorCondition(
                                "owner", "=", p.getUniqueId()
                        ),
                        new SQLJSONArrayCondition(
                                "members", p.getUniqueId()
                        )
                )
        ).retrieve();

        while (set.next()) {

            String tagString = set.getString("tag");
            ProjectTag tag = (tagString != null ? ProjectTag.valueOf(tagString) : null);

            String name = set.getString("name");

            String id = set.getString("id");

            List<String> lore = new ArrayList<>();
            if (plugin.getChatHandler().isLoaded("project_" + id)) {
                Chat chat = plugin.getChatHandler().getChat("project_" + id);
                lore.add("Jugadores: §7" + chat.getPlayers().size());
            } else {
                lore.add("Jugadores: §70");
            }

            gui.addPaginated(
                    (tag != null ?
                            ItemBuilder.head(
                                    tag.getHeadValue(),
                                    "§aProyecto " + (name != null ? name : set.getString("id".toUpperCase())),
                                    lore
                            ) :
                            ItemBuilder.of(Material.MAP)
                                    .name("§aProyecto " + (name != null ? name : set.getString("id".toUpperCase())))
                                    .lore(lore)
                                    .build()
                    ),
                    event -> {
                        try {
                            Project project = plugin.getProjectRegistry().get(id);

                            if (manager.getDefaultChatName().equals("project_" + id)) {
                                p.sendMessage(plugin.getChatHandler().getPrefix() + "Tu chat predeterminado ya es el chat del proyecto §a" + project.getDisplayName() + "§f.");
                                this.openConfigMenu(p);
                                return;
                            }


                            if (!plugin.getChatHandler().isLoaded("project_" + id)) {
                                plugin.getChatHandler().registerChat(
                                        new ProjectChat(project, plugin.getChatHandler())
                                );
                            }

                            Chat projectChat = plugin.getChatHandler().getChat("project_" + id);

                            manager.setDefaultChat(projectChat);
                            p.sendMessage(plugin.getChatHandler().getPrefix() + "Chat predeterminado cambiado al chat del proyecto §a" + project.getDisplayName() + "§f.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
                            event.closeGUI();
                        }
                    },
                    null, null, null
            );

        }

        gui.openTo(p, plugin);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        try {
            this.openConfigMenu(p);
        } catch (SQLException e) {
            e.printStackTrace();
            p.sendMessage(plugin.getChatHandler().getPrefix() + "Ha ocurrido un error en la base de datos.");
        }

        return true;
    }
}
