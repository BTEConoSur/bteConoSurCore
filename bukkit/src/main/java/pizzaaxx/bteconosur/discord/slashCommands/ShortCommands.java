package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.configuration.Configuration;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ShortCommands extends ListenerAdapter {

    private final Configuration embeds = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "discord/shortCommands");

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        String name = event.getName();
        EmbedBuilder builder = new EmbedBuilder();

        ConfigurationSection section = embeds.getConfigurationSection(name);
        builder.setColor(Color.GREEN);

        if (section.contains("title")) {
            builder.setTitle(section.getString("title"));
        }

        if (section.contains("description")) {
            builder.setDescription(section.getString("description").replace("\\n", "\n"));
        }

        if (section.contains("fields")) {
            ConfigurationSection fields = section.getConfigurationSection("fields");
            for (String fieldTitle : fields.getKeys(false)) {
                builder.addField(fieldTitle.replace("%dot%", "."), fields.getString(fieldTitle).replace("\\n", "\n"), false);
            }
        }

        if (section.contains("thumbnail")) {
            builder.setThumbnail(section.getString("thumbnail"));
        }

        event.replyEmbeds(builder.build()).queue(
                msg -> msg.deleteOriginal().queueAfter(2, TimeUnit.MINUTES)
        );

    }

}
