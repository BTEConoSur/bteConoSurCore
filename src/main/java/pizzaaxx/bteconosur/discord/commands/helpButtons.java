package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.util.*;
import java.util.List;

import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;

public class helpButtons extends ListenerAdapter {

    @Override
    public void onButtonClick(ButtonClickEvent e) {
        Message message = e.getMessage();
        if (message.getEmbeds().size() > 0 && message.getEmbeds().get(0).getTitle() != null && message.getEmbeds().get(0).getTitle().equals("Elige que comandos quieres ver.")) {
            if (e.getMessage().getReferencedMessage() != null && e.getMessage().getReferencedMessage().getAuthor() == e.getUser()) {
                EmbedBuilder help = new EmbedBuilder();
                help.setColor(new Color(0,255,42));
                help.setTitle("Ayuda de comandos de " + StringUtils.capitalize(e.getButton().getId()));
                help.setDescription("Usa `/help [comando]` para obtener más información de cada comando.");

                YamlManager yaml = new YamlManager(pluginFolder, "help.yml");
                Map<String, Object> data = yaml.getAllData();

                Map<Character, java.util.List<String>> map = new HashMap<>();

                for (Map.Entry<String, Object> entry : ((Map<String, Object>) data.get(e.getButton().getId())).entrySet()) {
                    if (map.containsKey(entry.getKey().charAt(0))) {
                        map.get(entry.getKey().charAt(0)).add("• `/" + entry.getKey() + "`: " + yaml.getValue(e.getButton().getId() + "." + entry.getKey() + ".usage"));
                    } else {
                        List<String> lines = new ArrayList<>();
                        lines.add("• `/" + entry.getKey() + "`: " + yaml.getValue(e.getButton().getId() + "." + entry.getKey() + ".description"));
                        map.put(entry.getKey().charAt(0), lines);
                    }
                }

                SortedSet<Character> sortedMap = new TreeSet<>(map.keySet());
                for (Character letter : sortedMap) {
                    help.addField(":regional_indicator_" + letter + ":", String.join("\n", map.get(letter)), false);
                }

                e.getTextChannel().sendMessageEmbeds(help.build()).reference(e.getMessage().getReferencedMessage()).mentionRepliedUser(false).queue();
                e.getMessage().delete().queue();
            }
        }
    }
}
