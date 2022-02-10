package pizzaaxx.bteconosur.novedades;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;

public class discordCommand implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getMessage().getContentRaw().startsWith("/")) {
                String fullCommand = e.getMessage().getContentRaw();
                String[] args = fullCommand.replaceFirst("/", "").split(" ");
                if (args.length > 0) {
                    if (args[0].equals("novedades") || args[0].equals("news")) {
                        Map<String, Object> data = YamlManager.getYamlData(pluginFolder, "novedades");

                        if (data != null && data.size() > 0) {
                            EmbedBuilder news = new EmbedBuilder();
                            news.setColor(new Color(0, 255, 42));
                            news.setTitle("Novedades del servidor");

                            int i = 1;
                            for (Map<String, Object> entry : (List<Map<String, Object>>) data.get("novedades")) {
                                news.addField("[" + i + "/" + data.size() + "]:", ChatColor.stripColor((String) entry.get("message")), false);
                                i++;
                            }

                            e.getTextChannel().sendMessageEmbeds(news.build()).queue();
                        } else {
                            EmbedBuilder noNews = new EmbedBuilder();
                            noNews.setColor(new Color(255, 0, 0));
                            noNews.setAuthor("No hay novedades para mostrar.");

                            e.getTextChannel().sendMessageEmbeds(noNews.build()).queue();
                        }
                    }
                }
            }
        }
    }
}
