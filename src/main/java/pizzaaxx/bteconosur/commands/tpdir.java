package pizzaaxx.bteconosur.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.Country;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class tpdir implements CommandExecutor {

    public static String tpdirPrefix = "§f[§9TPDIR§f] §7>>§r ";

    // TODO Finish this.

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("tpdir")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length > 0) {
                    String dir = String.join("+", args);
                    try {
                        // WEB REQUEST

                        URL url = new URL("https://nominatim.openstreetmap.org/search?q=" + dir + "&format=json");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");

                        int status = connection.getResponseCode();

                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder content = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }
                        in.close();

                        connection.disconnect();

                        String response = content.toString();

                        if (response.equals("[]")) {
                            p.sendMessage(tpdirPrefix + "No se ha podido encontrar el lugar introducido.");
                            return true;
                        }
                        
                        String firstOption = response.split("},")[0].replace("[{", "").replace("}]", "");

                        Map<String, String> map = new HashMap<>();

                        for (String string : firstOption.split(",")) {
                            if (!string.contains(":")) {
                                continue;
                            }
                            map.put(string.split(":")[0].replace("\"", ""), string.split(":")[1].replace("\"", ""));
                        }

                        Coords2D coords = new Coords2D(Double.valueOf(map.get("lat")), Double.valueOf(map.get("lon")));

                        if (new Country(coords.toBlockVector2D()).getCountry().equals("global")) {
                            p.sendMessage(tpdirPrefix + "El lugar introducido está fuera del Cono Sur.");
                            return true;
                        }

                        p.teleport(coords.toHighestLocation());
                        p.sendMessage(tpdirPrefix + "Teletransportándote a §a" + map.get("display_name").split(",")[0] + "§f.");
                    } catch (IOException e) {
                        p.sendMessage(tpdirPrefix + "Ha ocurrido un error.");
                    }
                } else {
                    p.sendMessage(tpdirPrefix + "Introduce el nombre de un lugar.");
                }
            }
        }
        return true;
    }
}
