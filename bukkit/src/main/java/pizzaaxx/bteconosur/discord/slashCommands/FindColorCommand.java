package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.plugin.Plugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;

public class FindColorCommand extends ListenerAdapter {

    private final List<String> allowedFileExtensions = Arrays.asList("png", "jpeg", "jpg");
    private final Map<Color, BufferedImage> textures = new HashMap<>();

    public FindColorCommand(Plugin plugin) {
        for (File file : new File(plugin.getDataFolder(), "textures/").listFiles()) {
            try {
                BufferedImage image = ImageIO.read(file);

                int sumR = 0, sumG = 0, sumB = 0;
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        Color pixel = new Color(image.getRGB(x, y));
                        sumR += pixel.getRed();
                        sumG += pixel.getGreen();
                        sumB += pixel.getBlue();
                    }
                }
                int num = image.getWidth() * image.getHeight();
                Color color = new Color(sumR / num, sumG / num, sumB / num);

                textures.put(color, image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("findcolor")) {

            // get color
            Color color;

            if (event.getSubcommandName().equals("code")) {

                String code = event.getOption("hex").getAsString();

                if (code.matches("[0-9a-f]{6}")) {
                    color = Color.decode("#" + code);

                } else {

                    event.replyEmbeds(errorEmbed("Introduce un código hexadecimal válido (6 caracteres).")).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                    return;

                }

            } else {

                Message.Attachment attachment = event.getOption("imagen").getAsAttachment();

                if (allowedFileExtensions.contains(attachment.getFileExtension())) {

                    InputStream stream;
                    try {
                        stream = attachment.retrieveInputStream().get();

                    } catch (InterruptedException | ExecutionException e) {
                        event.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                                msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                        );
                        return;
                    }

                    BufferedImage input;
                    try {

                        input = ImageIO.read(stream);

                    } catch (IOException e) {
                        event.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                                msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                        );
                        return;
                    }

                    int sumR = 0, sumG = 0, sumB = 0;
                    for (int x = 0; x < input.getWidth(); x++) {
                        for (int y = 0; y < input.getHeight(); y++) {
                            Color pixel = new Color(input.getRGB(x, y));
                            sumR += pixel.getRed();
                            sumG += pixel.getGreen();
                            sumB += pixel.getBlue();
                        }
                    }
                    int num = input.getWidth() * input.getHeight();
                    color = new Color(sumR / num, sumG / num, sumB / num);

                } else {
                    event.replyEmbeds(errorEmbed("Adjunta un archivo válido (`.png`, `.jpg` o `.jpeg`).")).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                    return;
                }
            }

            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(color);

            g.fillRect(0, 0, 100, 100);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", os);
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            event.replyFile(is, "image.png").queue();
        }
    }
}
