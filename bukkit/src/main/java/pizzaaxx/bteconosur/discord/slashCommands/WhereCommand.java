package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.misc.NumberMethods;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.country.OldCountry.countryNames;
import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;

public class WhereCommand extends ListenerAdapter implements Listener {

    private final Map<UUID, BufferedImage> headImages = new HashMap<>();
    private final File latamMap;

    public WhereCommand(Plugin plugin) {

        latamMap = new File(plugin.getDataFolder(), "discord/latamMap.png");

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        try {
            BufferedImage image = ImageIO.read(new URL("https://cravatar.eu/helmavatar/" + event.getPlayer().getName() + "/32.png"));

            headImages.put(event.getPlayer().getUniqueId(), image);
        } catch (IOException ignored) {
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        headImages.remove(event.getPlayer().getUniqueId());

    }

    public void updateHeads() {

        for (Player player : Bukkit.getOnlinePlayers()) {

            try {
                BufferedImage image = ImageIO.read(new URL("https://cravatar.eu/helmavatar/" + player.getName() + "/32.png"));

                headImages.put(player.getUniqueId(), image);
            } catch (IOException ignored) {
            }

        }

    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("where")) {

            if (Bukkit.getOnlinePlayers().size() > 0) {
                CompletableFuture.runAsync(() -> {

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(0, 255, 42));

                    BufferedImage image;
                    try {
                        image = ImageIO.read(latamMap);
                    } catch (IOException e) {
                        event.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                                msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                        );
                        return;
                    }

                    Graphics2D graphics = image.createGraphics();

                    for (String c : countryNames) {
                        OldCountry country = new OldCountry(c);
                        List<String> names = new ArrayList<>();
                        for (Player p : country.getPlayers()) {
                            names.add(p.getName().replace("_", "\\_"));
                            Coords2D coords = new Coords2D(p.getLocation());

                            // -118, 1        -14, 1

                            // -118, -57      -14, -57

                            double lon = coords.getLon(); // X COORD
                            double lat = coords.getLat(); // Y COORD

                            if (lon < -116 || lon > -12 || lat > -1 || lat < -55) {
                                continue;
                            }

                            double pixelX = NumberMethods.getNumberInNewRange(-118, -14, 0, 1183, lon) - 23;
                            double pixelY = Math.abs(NumberMethods.getNumberInNewRange(-57, 1, -806, 0, lat)) - 69;

                            graphics.drawImage(headImages.get(p.getUniqueId()), (int) pixelX, (int) pixelY, null);

                        }
                        Collections.sort(names);
                        embed.addField(":flag_" + country.getAbbreviation() + ": " + StringUtils.capitalize(c.replace("peru", "perú")) + ": " + names.size(), (names.size() > 0 ? String.join(", ", names) : "N/A"), true);
                    }

                    embed.setThumbnail("https://media.discordapp.net/attachments/807694452214333482/845857288609988628/conosur.png?width=471&height=473");

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(image, "png", os);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    InputStream is = new ByteArrayInputStream(os.toByteArray());

                    embed.setImage("attachment://map.png");
                    event.replyFile(is, "map.png").addEmbeds(embed.build()).queue(
                            msg -> msg.deleteOriginal().queueAfter(5, TimeUnit.MINUTES)
                    );
                });

            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(255,0,0));
                embed.setAuthor("No hay jugadores online.");
                event.replyEmbeds(embed.build()).queue(
                        msg -> msg.deleteOriginal().queueAfter(1, TimeUnit.MINUTES)
                );
            }
        }


    }
}
