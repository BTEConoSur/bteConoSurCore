package pizzaaxx.bteconosur.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.gui.ItemBuilder;
import pizzaaxx.bteconosur.gui.inventory.InventoryClickAction;
import pizzaaxx.bteconosur.gui.inventory.InventoryGUI;
import pizzaaxx.bteconosur.gui.inventory.PaginatedGUI;
import pizzaaxx.bteconosur.gui.inventory.StaticGUI;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.chat.ChatManager;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.sql.SQLException;
import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;
import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX_C;
import static pizzaaxx.bteconosur.chat.ChatHandler.CHATS;
import static pizzaaxx.bteconosur.utils.ChatUtils.GRAY;

public class ChatCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public ChatCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        OnlineServerPlayer s;
        try {
            s = plugin.getPlayerRegistry().get(player.getUniqueId()).asOnlinePlayer();
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            player.sendMessage(PREFIX + "Ha ocurrido un error.");
            return true;
        }

        ChatManager chatManager = s.getChatManager();
        Chat currentChat = chatManager.getCurrentChat();
        String currentChatID = currentChat.getProviderId() + "_" + currentChat.getChatId();
        PaginatedGUI gui = PaginatedGUI.fullscreen(
                Component.text("Chat " + currentChat.getDisplayName()),
                false
        );

        CHATS.forEach(
                (uuid, chatID) -> {
                    if (chatID.equals(currentChatID)) {
                        OfflineServerPlayer offlineServerPlayer = plugin.getPlayerRegistry().get(uuid);
                        gui.addItem(
                                ItemBuilder.head(
                                        uuid,
                                        "§a§l" + StringUtils.transformToSmallCapital(offlineServerPlayer.getName()),
                                        offlineServerPlayer.getLore()
                                ),
                                InventoryClickAction.EMPTY
                        );
                    }
                }
        );

        gui.addStaticItem(
                48,
                ItemBuilder.head(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZmQ2ZWMxYmFlMGI1NGZkYTg1MjJlYWM1NDk5M2JhNDAwZTMxNDgwOGNlZjA4ODdkMjRiOTJjMTVjOTE5ZiJ9fX0=",
                        "§a§lCambiar chat",
                        List.of(
                                Component.text("§a[•] ")
                                        .append(Component.text(StringUtils.transformToSmallCapital("Haz click para cambiar de chat."), TextColor.color(GRAY)))
                        )
                ),
                InventoryClickAction.of(
                        event -> {
                            StaticGUI chatGUI = new StaticGUI(3, Component.text("Cambiar chat"));

                            chatGUI.addItem(
                                    11,
                                    ItemBuilder.head(
                                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjU0ODUwMzFiMzdmMGQ4YTRmM2I3ODE2ZWI3MTdmMDNkZTg5YTg3ZjZhNDA2MDJhZWY1MjIyMWNkZmFmNzQ4OCJ9fX0=",
                                            "§a§lChat global",
                                            List.of(
                                                    Component.text("§a[•] ")
                                                            .append(Component.text(StringUtils.transformToSmallCapital("Haz click para cambiar al chat global."), TextColor.color(GRAY))
                                                            )
                                            )
                                    ),
                                    InventoryClickAction.of(
                                            event1 -> {
                                                plugin.getChatHandler().removeFromChat(player.getUniqueId(), currentChat);
                                                plugin.getChatHandler().addToChat(player.getUniqueId(), plugin);
                                                chatManager.setCurrentChat(plugin);
                                                event1.getInventory().close();
                                                player.sendMessage(PREFIX + "Has cambiado al chat §aglobal§f.");
                                            }
                                    )
                            );

                            chatGUI.addItem(
                                    13,
                                    ItemBuilder.head(
                                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhmZDcxMjZjZDY3MGM3OTcxYTI4NTczNGVkZmRkODAyNTcyYTcyYTNmMDVlYTQxY2NkYTQ5NDNiYTM3MzQ3MSJ9fX0=",
                                            "§a§lChats de países",
                                            List.of(
                                                    Component.text("§a[•] ")
                                                            .append(Component.text(StringUtils.transformToSmallCapital("Haz click para cambiar a un chat de país."), TextColor.color(GRAY))
                                                            )
                                            )
                                    ),
                                    InventoryClickAction.of(
                                            event1 -> {
                                                PaginatedGUI countryGUI = PaginatedGUI.fullscreen(
                                                        Component.text("Chats de países"),
                                                        false
                                                );

                                                for (Country country : plugin.getCountriesRegistry().getCountries()) {
                                                    countryGUI.addItem(
                                                            ItemBuilder.head(
                                                                    country.getHeadValue(),
                                                                    "§a§l" + StringUtils.transformToSmallCapital(country.getName()),
                                                                    List.of(
                                                                            Component.text("§a[•] ")
                                                                                    .append(Component.text(StringUtils.transformToSmallCapital("Haz click para cambiar al chat de §a" + country.getName() + "§7."), TextColor.color(GRAY))
                                                                                    )
                                                                    )
                                                            ),
                                                            InventoryClickAction.of(
                                                                    event2 -> {
                                                                        plugin.getChatHandler().removeFromChat(player.getUniqueId(), currentChat);
                                                                        plugin.getChatHandler().addToChat(player.getUniqueId(), country);
                                                                        chatManager.setCurrentChat(country);
                                                                        event2.getInventory().close();
                                                                        player.sendMessage(PREFIX + "Has cambiado al chat de §a" + country.getName() + "§f.");
                                                                    }
                                                            )
                                                    );
                                                }
                                            }
                                    )
                            );

                            chatGUI.addItem(
                                    15,
                                    ItemBuilder.head(
                                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y5NGVmYWI5OTIxN2IwZTFlMDY0NTYzOWE2ZmFmMTE3ZWI5NmQ4NDRlMDIyNGVlZDU0ZWJmODY5MGRjNzJlZCJ9fX0=",
                                            "§a§lChats de proyectos",
                                            List.of(
                                                    Component.text("§a[•] ")
                                                            .append(Component.text(StringUtils.transformToSmallCapital("Haz click para cambiar a un chat de proyecto."), TextColor.color(GRAY))
                                                            )
                                            )
                                    ),
                                    InventoryClickAction.of(
                                            event1 -> {
                                                PaginatedGUI projectGUI = PaginatedGUI.fullscreen(
                                                        Component.text("Chats de proyectos"),
                                                        false
                                                );

                                                // all projects the player is owner or member of
                                                for (String projectID : s.getProjectsManager().getProjects()) {
                                                    Project project = plugin.getProjectsRegistry().get(projectID);
                                                    projectGUI.addItem(
                                                            ItemBuilder.head(
                                                                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y5NGVmYWI5OTIxN2IwZTFlMDY0NTYzOWE2ZmFmMTE3ZWI5NmQ4NDRlMDIyNGVlZDU0ZWJmODY5MGRjNzJlZCJ9fX0=",
                                                                    Component.text(StringUtils.transformToSmallCapital(project.getDisplayName()), TextColor.color(project.getType().getColor().getRGB())),
                                                                    List.of(
                                                                            Component.text("§a[•] ")
                                                                                    .append(Component.text(StringUtils.transformToSmallCapital("Haz click para cambiar al chat de §a" + projectID + "§7."), TextColor.color(GRAY))
                                                                                    )
                                                                    )
                                                            ),
                                                            InventoryClickAction.of(
                                                                    event2 -> {
                                                                        plugin.getChatHandler().removeFromChat(player.getUniqueId(), currentChat);
                                                                        plugin.getChatHandler().addToChat(player.getUniqueId(), project);
                                                                        chatManager.setCurrentChat(project);
                                                                        event2.getInventory().close();
                                                                        player.sendMessage(PREFIX + "Has cambiado al chat de §a" + projectID + "§f.");
                                                                    }
                                                            )
                                                    );
                                                }
                                            }
                                    )
                            );
                        }
                )
        );

        gui.addStaticItem(
                48,
                ItemBuilder.head(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19",
                        "§a§lInvitar jugador",
                        List.of(
                                Component.text("§a[•] ")
                                        .append(Component.text(StringUtils.transformToSmallCapital("Haz click para invitar jugadores a este chat."), TextColor.color(GRAY)))
                        )
                ),
                InventoryClickAction.of(
                        event -> {
                            PaginatedGUI inviteGUI = PaginatedGUI.fullscreen(
                                    Component.text("Invitar jugadores"),
                                    false
                            );

                            // add all players that are not in the chat
                            CHATS.forEach(
                                    (uuid, chatID) -> {
                                        if (!chatID.equals(currentChatID)) {
                                            OfflineServerPlayer offlineServerPlayer = plugin.getPlayerRegistry().get(uuid);
                                            inviteGUI.addItem(
                                                    ItemBuilder.head(
                                                            uuid,
                                                            "§a§l" + StringUtils.transformToSmallCapital(offlineServerPlayer.getName()),
                                                            List.of(
                                                                    Component.text("§a[•] ")
                                                                            .append(Component.text(StringUtils.transformToSmallCapital("Haz click para invitar a §a" + offlineServerPlayer.getName() + "§7 al chat."), TextColor.color(GRAY)))
                                                            )
                                                    ),
                                                    InventoryClickAction.of(
                                                            event1 -> {
                                                                Player p = Bukkit.getPlayer(uuid);
                                                                if (p == null) {
                                                                    return;
                                                                }
                                                                p.sendMessage(
                                                                        PREFIX_C.append(
                                                                                Component.text("§a" + player.getName() + "§f te ha invitado a unirte al chat §a" + currentChat.getDisplayName() + "§f. Haz ")
                                                                        ).append(
                                                                                Component.text("click aquí", GREEN)
                                                                                        .clickEvent(
                                                                                                ClickEvent.callback(
                                                                                                        audience -> {
                                                                                                            try {
                                                                                                                OnlineServerPlayer target = plugin.getPlayerRegistry().get(uuid).asOnlinePlayer();
                                                                                                                Chat currentTargetChat = target.getChatManager().getCurrentChat();
                                                                                                                plugin.getChatHandler().removeFromChat(uuid, currentTargetChat);
                                                                                                                plugin.getChatHandler().addToChat(uuid, currentChat);
                                                                                                                target.getChatManager().setCurrentChat(currentChat);
                                                                                                                p.sendMessage(PREFIX + "Te has unido al chat §a" + currentChat.getDisplayName() + "§f.");
                                                                                                            } catch (SQLException | JsonProcessingException e) {
                                                                                                                p.sendMessage(PREFIX + "Ha ocurrido un error.");
                                                                                                            }
                                                                                                        }
                                                                                                )
                                                                                        )
                                                                        ).append(
                                                                                Component.text("§f para unirte.")
                                                                        )
                                                                );
                                                                event1.getInventory().close();
                                                                player.sendMessage(PREFIX + "Has invitado a §a" + offlineServerPlayer.getName() + "§f al chat.");
                                                            }
                                                    )
                                            );
                                        }
                                    }
                            );
                        }
                )
        );

        return true;
    }
}
