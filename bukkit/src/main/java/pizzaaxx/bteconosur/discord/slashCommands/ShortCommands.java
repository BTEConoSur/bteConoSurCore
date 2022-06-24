package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ShortCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        String name = event.getName();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        switch (name) {
            case "premium":
                builder.setTitle("Premium:");
                builder.setDescription("Para entrar al servidor necesitas una cuenta pagada (premium) de Minecraft **Java**. Si no cuentas con una, ¡no te preocupes! Puedes quedarte a conversar y ver nuestro progreso y los directos en los que construimos.");
                break;
            case "bedrock":
                builder.setTitle("Bedrock:");
                builder.setDescription("Por el momento, no se puede entrar con Bedrock Edition ya que el *Geyser* (el plugin que permite a jugadores de Bedrock entrar) es muy inestable en 1.17 en conjunto con *CubicChunks*. Lamentamos las molestias.");
                break;
            case "altura":
                builder.setTitle("Altitud en Google Earth:");
                builder.setDescription("1. En Google Earth Pro, pon el cursor en el lugar del que quieres conocer la altitud.\n2. En la parte inferior derecha encontrarás la altitud del cursor.");
                break;
            case "prioridad":
                builder.setTitle("Prioridad de entrada:");
                builder.setDescription("El servidor cuenta con un sistema de prioridad de entrada, que permite a jugadores con rangos más altos entrar con mayor facilidad, reemplazando a un jugador aleatorio del rango más bajo. La prioridad va así:\n```Admin > Moderador > Donador > Streamer > Builder > Postulante > Evento > Visita```\nUn jugador podrá entrar siempre y cuando el rango más bajo sea **menor** a su rango.");
                break;
            case "ipfix":
                builder.setTitle("IP alternativa");
                builder.setDescription("Si la IP normal no funciona, puedes usar la IP numérica.");
                builder.addField("Java 1.7 - 1.16.5:", "`144.172.80.107:25632`", false);
                builder.addField("Bedrock:", "No disponible.", false);
                builder.setThumbnail("https://media.discordapp.net/attachments/807694452214333482/845857288609988628/conosur.png");
                break;
            case "ip":
                builder.setTitle("IP del servidor");
                builder.addField("Java 1.7 - 1.16.5:", "`bteconosur.com`", false);
                builder.addField("Bedrock:", "No disponible por el momento.", false);
                builder.setThumbnail("https://media.discordapp.net/attachments/807694452214333482/845857288609988628/conosur.png");
                break;
        }
        event.replyEmbeds(builder.build()).queue(
                msg -> msg.deleteOriginal().queueAfter(2, TimeUnit.MINUTES)
        );

    }

}
