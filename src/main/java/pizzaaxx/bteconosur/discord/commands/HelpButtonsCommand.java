package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import pizzaaxx.bteconosur.yaml.Configuration;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HelpButtonsCommand extends ListenerAdapter {

    @Override
    public void onButtonClick(ButtonClickEvent e) {
        Message message = e.getMessage();
        if (message.getEmbeds().size() > 0 && message.getEmbeds().get(0).getTitle() != null && Objects.equals(message.getEmbeds().get(0).getTitle(), "Elige qué comandos quieres ver.")) {
            MessageReference messageReference = e.getMessage().getMessageReference();
            if (messageReference != null) {
                Message rMessage = e.getMessage().getMessageReference().resolve().complete();
                if (rMessage != null && Objects.equals(rMessage.getAuthor(), e.getUser())) {
                    EmbedBuilder help = new EmbedBuilder();
                    help.setColor(new Color(0,255,42));
                    help.setTitle("Ayuda de comandos de " + StringUtils.capitalize(e.getButton().getId()));
                    help.setDescription("Usa `/help [comando]` para obtener más información de cada comando.");
                    Configuration yaml = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "help");

                    Map<Character, java.util.List<String>> map = new HashMap<>();

                    for (Map.Entry<String, Object> entry : yaml.getConfigurationSection(e.getButton().getId()).getValues(false).entrySet()) {
                        if (map.containsKey(entry.getKey().charAt(0))) {
                            map.get(entry.getKey().charAt(0)).add("• `/" + entry.getKey() + "`: " + yaml.getString(e.getButton().getId() + "." + entry.getKey() + ".description"));
                        } else {
                            List<String> lines = new ArrayList<>();
                            lines.add("• `/" + entry.getKey() + "`: " + yaml.getString(e.getButton().getId() + "." + entry.getKey() + ".description"));
                            map.put(entry.getKey().charAt(0), lines);
                        }
                    }

                    SortedSet<Character> sortedMap = new TreeSet<>(map.keySet());
                    for (Character letter : sortedMap) {
                        if (letter.toString().matches("[0-9]{1}")) {
                            help.addField(":1234:", String.join("\n", map.get(letter)), false);
                        } else {
                            help.addField(":regional_indicator_" + letter + ":", String.join("\n", map.get(letter)), false);
                        }
                    }
                    e.getTextChannel().sendMessageEmbeds(help.build()).reference(rMessage).mentionRepliedUser(false).queue(msg -> msg.delete().queueAfter(10, TimeUnit.MINUTES));
                    e.getMessage().delete().queue();
                    rMessage.delete().queueAfter(10, TimeUnit.MINUTES);
                }
            }
        }
    }
}
