package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OnlineCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("online")) {
            if (Bukkit.getOnlinePlayers().size() > 0) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.GREEN);
                List<String> names = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    names.add(p.getName().replace("_", "\\_"));
                }
                Collections.sort(names);
                embed.addField("Hay " + Bukkit.getOnlinePlayers().size() + " jugador" + (Bukkit.getOnlinePlayers().size() == 1 ? "" : "es") + " online:", String.join(", ", names), false);
                event.replyEmbeds(embed.build()).queue(
                        msg -> msg.deleteOriginal().queueAfter(5, TimeUnit.MINUTES)
                );
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setAuthor("No hay jugadores online.");
                event.replyEmbeds(embed.build()).queue(
                        msg -> msg.deleteOriginal().queueAfter(1, TimeUnit.MINUTES)
                );
            }
        }
    }
}
