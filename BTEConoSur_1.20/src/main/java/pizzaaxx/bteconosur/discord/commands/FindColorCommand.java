package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.discord.DiscordConnector;
import pizzaaxx.bteconosur.utils.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FindColorCommand extends ListenerAdapter implements DiscordCommandHolder {

    private final List<Pair<Color, BufferedImage>> textures = new ArrayList<>();
    private final List<String> allowedExtensions = List.of("png", "jpg", "jpeg");

    public FindColorCommand(@NotNull BTEConoSurPlugin plugin) {
        File texturesFolder = new File(plugin.getDataFolder(), "textures");
        File[] files = texturesFolder.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.getName().endsWith(".png")) {
                try {
                    BufferedImage image = ImageIO.read(file);
                    textures.add(new Pair<>(findDefaultColor(image), image));
                } catch (IOException e) {
                    plugin.warn("Error reading texture file " + file.getName() + ".");
                }
            }
        }
    }

    @Contract("_ -> new")
    private @NotNull Color findDefaultColor(@NotNull BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        int r = 0, g = 0, b = 0;
        for (int pixel : pixels) {
            r += (pixel >> 16) & 0xFF;
            g += (pixel >> 8) & 0xFF;
            b += pixel & 0xFF;
        }

        int total = width * height;
        return new Color(r / total, g / total, b / total);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("findcolor")) return;

        String subcommand = event.getSubcommandName();
        assert subcommand != null;

        switch (subcommand) {
            case "hex" -> {
                OptionMapping hexMapping = event.getOption("color");
                assert hexMapping != null;
                String hex = hexMapping.getAsString();
                Color color = Color.decode(hex);
                handleColor(color, event);
            }
            case "image" -> {
                OptionMapping imageMapping = event.getOption("imagen");
                assert imageMapping != null;
                Message.Attachment attachment = imageMapping.getAsAttachment();
                // if image is not png, jpg or jpeg
                if (!allowedExtensions.contains(attachment.getFileExtension())) {
                    DiscordConnector.respondError(event, "El archivo no es una imagen válida.");
                    return;
                }
                // if image is larger than 20MB
                if (attachment.getSize() > 20 * 1024 * 1024) {
                    DiscordConnector.respondError(event, "La imagen es demasiado grande.");
                    return;
                }

                attachment.getProxy().download().thenAccept(
                        is -> {
                            BufferedImage img;
                            try {
                                img = ImageIO.read(is);
                            } catch (IOException e) {
                                DiscordConnector.respondError(event, "Ocurrió un error al leer la imagen.");
                                return;
                            }
                            Color color = findDefaultColor(img);
                            handleColor(color, event);
                        }
                );
            }
        }
    }

    private void handleColor(Color color, @NotNull SlashCommandInteractionEvent event) {
        BufferedImage result = new BufferedImage(256 * 3, 256, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = result.createGraphics();
        textures.stream()
                .sorted(
                        (pair1, pair2) -> {
                            Color textureColor1 = pair1.getKey();
                            double distance1 = Math.pow(color.getRed() - textureColor1.getRed(), 2) +
                                    Math.pow(color.getGreen() - textureColor1.getGreen(), 2) +
                                    Math.pow(color.getBlue() - textureColor1.getBlue(), 2);
                            Color textureColor2 = pair2.getKey();
                            double distance2 = Math.pow(color.getRed() - textureColor2.getRed(), 2) +
                                    Math.pow(color.getGreen() - textureColor2.getGreen(), 2) +
                                    Math.pow(color.getBlue() - textureColor2.getBlue(), 2);

                            return Double.compare(distance1, distance2);
                        }
                )
                .limit(3)
                .forEach(
                        withCounter(
                                (i, pair) -> {
                                    BufferedImage texture = pair.getValue();
                                    g.drawImage(
                                            texture.getScaledInstance(
                                                    256, 256, Image.SCALE_SMOOTH
                                            ),
                                            i * 256,
                                            0,
                                            null);
                                }
                        )
                );
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(color);
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        builder.setTitle("Texturas mas cercanas al color " + hex);
        builder.setImage("attachment://result.png");
        event.replyEmbeds(
                builder.build()
        ).addFiles(
                FileUpload.fromData(
                        toInputStream(result),
                        "result.png"
                )
        ).queue();
        g.dispose();
    }

    @Contract("_ -> new")
    private @NotNull InputStream toInputStream(BufferedImage image) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @Contract(pure = true)
    private <T> @NotNull Consumer<T> withCounter(BiConsumer<Integer, T> consumer) {
        AtomicInteger counter = new AtomicInteger(0);
        return item -> consumer.accept(counter.getAndIncrement(), item);
    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[] {
                Commands.slash(
                        "findcolor",
                        "Find the textures closest to the given color"
                ).addSubcommands(
                        new SubcommandData(
                                "hex",
                                "Find the textures closest to the given hex color"
                        ).addOption(
                                OptionType.STRING,
                                "color",
                                "The hex color to search for",
                                true
                        ),
                        new SubcommandData(
                                "image",
                                "Find the textures closest to the given image"
                        ).addOption(
                                OptionType.ATTACHMENT,
                                "imagen",
                                "The image to search for",
                                true
                        )
                ).setNameLocalization(
                        DiscordLocale.SPANISH,
                        "encontrarcolor"
                )
        };
    }
}
