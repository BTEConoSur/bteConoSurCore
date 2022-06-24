package pizzaaxx.bteconosur.discord.fuzzyMatching.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import pizzaaxx.bteconosur.discord.fuzzyMatching.FuzzyMatchListener;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class BedrockListener implements FuzzyMatchListener {
    @Override
    public void onFuzzyMatch(Message message, String matchedText, String match) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.setTitle("Bedrock:");
        builder.setDescription("Por el momento, no se puede entrar con Bedrock Edition ya que el *Geyser* (el plugin que permite a jugadores de Bedrock entrar) es muy inestable en 1.17 en conjunto con *CubicChunks*. Lamentamos las molestias.");
        message.replyEmbeds(
                builder.build()
        ).mentionRepliedUser(false).queue(
                msg -> msg.delete().queueAfter(2, TimeUnit.MINUTES)
        );
    }
}
