package pizzaaxx.bteconosur.discord.fuzzyMatching.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import pizzaaxx.bteconosur.discord.fuzzyMatching.FuzzyMatchListener;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class PremiumListener implements FuzzyMatchListener {
    @Override
    public void onFuzzyMatch(Message message, String matchedText, String match) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.setTitle("Premium:");
        builder.setDescription("Para entrar al servidor necesitas una cuenta pagada (premium) de Minecraft **Java**. Si no cuentas con una, ¡no te preocupes! Puedes quedarte a conversar y ver nuestro progreso y los directos en los que construimos.");
        message.replyEmbeds(
                builder.build()
        ).mentionRepliedUser(false).queue(
                msg -> msg.delete().queueAfter(2, TimeUnit.MINUTES)
        );
    }
}
