package pizzaaxx.bteconosur.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class HelpMethods {

    public static @NotNull MessageEmbed errorEmbed(String text){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(text);
        return embed.build();
    }

}
