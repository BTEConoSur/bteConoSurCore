package pizzaaxx.bteconosur.Commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Inventory.InventoryGUI;
import pizzaaxx.bteconosur.Inventory.ItemBuilder;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TpdirCommand implements CommandExecutor, Prefixable {

    private final BTEConoSur plugin;

    public TpdirCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length < 1) {
            p.sendMessage(this.getPrefix() + "Introduce una direcci´ón.");
            return true;
        }

        String dir = String.join("+", args);

        try {
            List<Map<String, Object>> options = plugin.getJSONMapper().readValue(new URL("https://nominatim.openstreetmap.org/search?q=" + dir + "&format=json"), ArrayList.class);

            List<String> nameOptions = new ArrayList<>();
            List<Coords2D> coordOptions = new ArrayList<>();

            for (Map<String, Object> option : options) {
                Coords2D coords = new Coords2D(
                        plugin,
                        Double.parseDouble(option.get("lat").toString()),
                        Double.parseDouble(option.get("lon").toString())
                );

                if (plugin.getCountryManager().isInsideCountry(coords.toHighestLocation())) {
                    nameOptions.add(option.get("display_name").toString());
                    coordOptions.add(coords);
                }
            }

            int optionsSize = nameOptions.size();

            if (optionsSize > 1) {
                int rows = Math.floorDiv(optionsSize, 9) + 3;

                InventoryGUI gui = new InventoryGUI(
                        rows,
                        "Selecciona una opción"
                );

                for (int i = 0; i < (rows - 2) * 7; i++) {
                    int realSlot = i + (2 * Math.floorDiv(i, 7)) + 1 + 9;
                    if (i < optionsSize) {
                        gui.setItem(
                                new ItemBuilder(Material.MAP, 1, 1)
                                        .name("§a" + nameOptions.get(i))
                                        .lore("§7Haz click para teletransportarte.")
                                        .build(),
                                realSlot
                        );
                        int finalI = i;
                        gui.setAction(
                                event -> {
                                    event.getPlayer().closeInventory();
                                    event.getPlayer().teleport(coordOptions.get(finalI).toHighestLocation());
                                    event.getPlayer().sendMessage(this.getPrefix() + "Teletransportándote a §a" + nameOptions.get(finalI).split(",")[0]);
                                },
                                realSlot
                        );
                    } else {
                        gui.setEmptySlot(realSlot);
                    }
                }
                plugin.getInventoryHandler().open(p, gui);
            } else if (optionsSize == 1) {
                p.teleport(coordOptions.get(0).toHighestLocation());
                p.sendMessage(this.getPrefix() + "Teletransportándote a §a" + nameOptions.get(0).split(",")[0]);
            } else {
                p.sendMessage(this.getPrefix() + "No se han encontrado lugares dentro del Cono Sur con ese nombre.");
            }


        } catch (IOException e) {
            p.sendMessage(this.getPrefix() + "Ha ocurrido un error.");
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getPrefix() {
        return StringUtils.getGenericPrefix("2", "TPDIR");
    }
}
