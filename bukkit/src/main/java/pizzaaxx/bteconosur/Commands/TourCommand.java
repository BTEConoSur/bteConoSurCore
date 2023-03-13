package pizzaaxx.bteconosur.Commands;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Inventory.ItemBuilder;
import pizzaaxx.bteconosur.Projects.Finished.FinishedProject;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLJSONArrayCondition;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderExpression;
import pizzaaxx.bteconosur.SQL.Ordering.SQLOrderSet;
import pizzaaxx.bteconosur.Utils.Pair;
import xyz.upperlevel.spigot.book.BookUtil;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static pizzaaxx.bteconosur.SQL.Ordering.SQLOrderExpression.Order.ASC;

public class TourCommand implements CommandExecutor, Listener {

    private final BTEConoSur plugin;

    public TourCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Pair<String, Integer>> tours = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;

        City city;
        int counter;

        if (args.length == 0) {

            city = plugin.getCityManager().getCityAt(p.getLocation());

            if (city == null) {
                p.sendMessage(plugin.getPrefix() + "No estás dentro de ninguna ciudad.");
                return true;
            }

            counter = 0;

        } else if (args.length == 1) {

            String name = args[0];

            if (plugin.getCityManager().exists(name)) {
                city = plugin.getCityManager().get(name);
            } else {
                p.sendMessage(plugin.getPrefix() + "La ciudad introducida no existe.");
                return true;
            }

            counter = 0;

        } else {

            String name = args[0];

            if (plugin.getCityManager().exists(name)) {
                city = plugin.getCityManager().get(name);
            } else {
                p.sendMessage(plugin.getPrefix() + "La ciudad introducida no existe.");
                return true;
            }

            counter = Integer.parseInt(args[1]);
        }

        this.tour(p, city, counter);

        return true;

    }

    public void tour(Player p, City city, int counter) {

        try {

            int finishedAmount = city.getFinishedProjectsAmount();

            if (finishedAmount == 0) {
                p.sendMessage(plugin.getPrefix() + "§a" + city.getDisplayName() + "§f no tiene proyectos terminados.");
                return;
            }

            int finalCounter = (counter >= finishedAmount ? 0 : (counter < 0 ? finishedAmount - 1 : counter));

            ResultSet set = plugin.getSqlManager().select(
                    "finished_projects",
                    new SQLColumnSet(
                            "id"
                    ),
                    new SQLANDConditionSet(
                            new SQLJSONArrayCondition("cities", city.getName())
                    ),
                    new SQLOrderSet(
                            new SQLOrderExpression(
                                    "finished_date", ASC
                            )
                    )
            ).addText(" LIMIT 1 OFFSET " + finalCounter).retrieve();

            set.next();

            String id = set.getString("id");

            FinishedProject project = plugin.getFinishedProjectsRegistry().get(id);

            for (int i = 0; i < 9; i++) {
                p.getInventory().clear(i);
            }

            p.getInventory().setItem(
                    4,
                    ItemBuilder.head(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ==",
                            "§aVer tour completo",
                            null
                    )
            );
            p.getInventory().setItem(
                    3,
                    ItemBuilder.head(
                            ItemBuilder.BACK_HEAD,
                            "§aProyecto anterior",
                            null
                    )
            );
            p.getInventory().setItem(
                    5,
                    ItemBuilder.head(
                            ItemBuilder.NEXT_HEAD,
                            "§aProyecto siguiente",
                            null
                    )
            );

            p.teleport(project.getTeleportLocation());
            p.sendMessage(" ");
            p.sendMessage(" ");

            p.sendMessage("§a§lProyecto " + project.getDisplayName());
            p.sendMessage("§8" + project.getType().getDisplayName() + " - " + project.getPoints() + " puntos");
            p.sendMessage(" ");

            p.sendMessage("§aLíder: §f" + plugin.getPlayerRegistry().get(project.getOwner()).getName());
            if (!project.getMembers().isEmpty()) {
                p.sendMessage("§aMiembros: §f" + String.join(", ", plugin.getPlayerRegistry().getNames(project.getMembers())));
            }
            if (project.hasPost()) {
                p.sendMessage(" ");
                p.sendMessage(
                        BookUtil.TextBuilder.of("[Ver publicación en Discord]")
                                .color(ChatColor.YELLOW)
                                .onHover(BookUtil.HoverAction.showText("Haz click para abrir"))
                                .onClick(BookUtil.ClickAction.openUrl(project.getPost().getChannel().getJumpUrl()))
                                .build()
                );
            }

            p.sendMessage(" ");
            p.sendMessage("§8Terminado el " + new SimpleDateFormat("dd/MM/yy").format(new Date(project.getFinishedDate())));

            p.sendMessage(" ");
            p.sendMessage(" ");

            tours.put(p.getUniqueId(), new Pair<>(city.getName(), finalCounter));

        } catch (Exception e) {
            e.printStackTrace();
            p.sendMessage(plugin.getPrefix() + "Ha ocurrido un error en la base de datos.");
        }
    }

    @EventHandler
    public void onClick(@NotNull PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {

            String displayName = item.getItemMeta().getDisplayName();

            Player p = event.getPlayer();

            if (displayName.equals("§aVer tour completo")) {

                if (tours.containsKey(p.getUniqueId())) {

                    Pair<String, Integer> data = tours.get(p.getUniqueId());
                    City city = plugin.getCityManager().get(data.getKey());
                    int count = data.getValue();

                    try {
                        ResultSet set = plugin.getSqlManager().select(
                                "finished_projects",
                                new SQLColumnSet("id"),
                                new SQLANDConditionSet(
                                        new SQLJSONArrayCondition(
                                                "cities", city.getName()
                                        )
                                ),
                                new SQLOrderSet(
                                        new SQLOrderExpression(
                                                "finished_date", ASC
                                        )
                                )
                        ).retrieve();

                        List<String> displayNames = new ArrayList<>();
                        while (set.next()) {
                            FinishedProject project = plugin.getFinishedProjectsRegistry().get(set.getString("id"));

                            displayNames.add(project.getDisplayName());
                        }

                        BookUtil.BookBuilder builder = BookUtil.writtenBook();

                        List<BaseComponent[]> pages = new ArrayList<>();

                        List<List<String>> displayNamesLists = Lists.partition(displayNames, 12);

                        for (List<String> displayNamesList : displayNamesLists) {

                            BookUtil.PageBuilder page = new BookUtil.PageBuilder();
                            page.add("§a         §a§l[TOUR]");
                            page.newLine();
                            page.add(" ");

                            int counter = 0;
                            for (String name : displayNamesList) {

                                page.newLine();
                                page.add("§7• §r");
                                page.add(
                                        BookUtil.TextBuilder.of(name)
                                                .color((counter == count ? ChatColor.GREEN : ChatColor.BLACK))
                                                .onHover(BookUtil.HoverAction.showText("Haz click para ir."))
                                                .onClick(BookUtil.ClickAction.runCommand("/tour " + city.getName() + " " + counter))
                                                .build()
                                );

                                counter++;

                            }

                            pages.add(page.build());
                        }

                        builder.pages(pages);

                        BookUtil.openPlayer(p, builder.build());

                    } catch (SQLException e) {
                        e.printStackTrace();
                        p.sendMessage(plugin.getPrefix() + "Ha ocurrido un error en la base de datos.");
                    }


                } else {
                    p.performCommand("tour");
                }

            }

            if (displayName.equals("§aProyecto anterior")) {

                if (tours.containsKey(p.getUniqueId())) {

                    Pair<String, Integer> data = tours.get(p.getUniqueId());
                    City city = plugin.getCityManager().get(data.getKey());
                    int count = data.getValue();

                    this.tour(p, city, count - 1);

                } else {
                    p.performCommand("tour");
                }
            }

            if (displayName.equals("§aProyecto siguiente")) {

                if (tours.containsKey(p.getUniqueId())) {

                    Pair<String, Integer> data = tours.get(p.getUniqueId());
                    City city = plugin.getCityManager().get(data.getKey());
                    int count = data.getValue();

                    this.tour(p, city, count + 1);

                } else {
                    p.performCommand("tour");
                }
            }
        }
    }

}
