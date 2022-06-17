package pizzaaxx.bteconosur.discord.fuzzyMatching.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import pizzaaxx.bteconosur.discord.fuzzyMatching.FuzzyMatchListener;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class IPListener implements FuzzyMatchListener {
    @Override
    public void onFuzzyMatch(Message message, String matchedText, String match) {

        message.replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setThumbnail("https://media.discordapp.net/attachments/807694452214333482/845857288609988628/conosur.png")
                        .addField("Java 1.7-1.16.5", "`bteconosur.com`", false)
                        .addField("Bedrock", "No disponible por el momento.", false)
                        .build()
        ).mentionRepliedUser(false).queue(
                msg -> msg.delete().queueAfter(2, TimeUnit.MINUTES)
        );

    }
}
