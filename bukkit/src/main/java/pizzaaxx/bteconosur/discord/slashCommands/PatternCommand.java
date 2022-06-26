package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.misc.Misc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// TODO FIX ALREADY EXISTING FILES ON DEPLOY

import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;

public class PatternCommand extends ListenerAdapter {

    private final BufferedImage allTextures;

    public PatternCommand(Plugin plugin) {
        BufferedImage allTextures1;

        try {
            allTextures1 = ImageIO.read(new File(plugin.getDataFolder(), "textures.png"));
        } catch (IOException e) {
            plugin.getLogger().warning("Ha ocurrido un error cargando la imagen de texturas.");
            allTextures1 = null;
        }

        allTextures = allTextures1;
    }

    private static class ParseException extends Exception {

        private final String error;

        public ParseException(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("pattern")) {

            OptionMapping option = event.getOption("patrón");

            String pattern;
            if (option != null) {
                pattern = option.getAsString();
            } else {
                event.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                );
                return;
            }

            try {

                Map<BufferedImage, Integer> textures = parsePattern(pattern);

                BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

                Graphics2D graphics = image.createGraphics();

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {

                        BufferedImage selectedImage = Misc.selectRandomFromValues(textures);

                        if (selectedImage != null) {
                            graphics.drawImage(selectedImage, x * 16, y * 16, null);
                        }

                    }
                }

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Visualizador de patrones (16x16)");
                builder.addField("Patrón:", "`" + pattern +  "`", false);
                builder.setImage("attachment://pattern.png");
                builder.setColor(Color.GREEN);

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    ImageIO.write(image, "png", os);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                ActionRow row1 = ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "refreshPattern~~~16~~~" + pattern, "Generar patrón de nuevo", Emoji.fromUnicode("U+1F504"))
                );

                SelectMenu.Builder menu = SelectMenu.create("newPatternResolution~~~" + pattern);
                menu.setPlaceholder("Selecciona un nuevo tamaño");
                menu.addOption("16x16", "16");
                menu.addOption("32x32", "32");
                menu.addOption("64x64", "64");
                menu.setDefaultValues(Collections.singletonList("16"));

                ActionRow row2 = ActionRow.of(
                    menu.build()
                );

                event.replyFile(is, "pattern.png").addEmbeds(builder.build()).addActionRows(
                        row1, row2
                ).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                );

            } catch (ParseException e) {
                event.replyEmbeds(errorEmbed(e.getError())).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                );
            }

        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("refreshPattern")) {

            Message message = event.getMessage();

            if (!message.getInteraction().getUser().getId().equals(event.getUser().getId())) {

                event.replyEmbeds(errorEmbed("Solo quien usó el comando puede usar las interacciones.")).setEphemeral(true).queue();
                return;

            }

            String[] parts = event.getComponentId().split("~~~");

            int resolution = Integer.parseInt(parts[1]);

            String pattern = parts[2];

            try {

                Map<BufferedImage, Integer> textures = parsePattern(pattern);

                BufferedImage image = new BufferedImage(resolution * 16, resolution * 16, BufferedImage.TYPE_INT_RGB);

                Graphics2D graphics = image.createGraphics();

                for (int x = 0; x < resolution; x++) {
                    for (int y = 0; y < resolution; y++) {

                        BufferedImage selectedImage = Misc.selectRandomFromValues(textures);

                        if (selectedImage != null) {
                            graphics.drawImage(selectedImage.getScaledInstance(selectedImage.getWidth(), selectedImage.getHeight(), Image.SCALE_DEFAULT), x * 16, y * 16, null);
                        }

                    }
                }

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Visualizador de patrones (" + resolution + "x" + resolution + ")");
                builder.addField("Patrón:", "`" + pattern +  "`", false);
                builder.setImage("attachment://pattern.png");
                builder.setColor(Color.GREEN);

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    ImageIO.write(image, "png", os);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                ActionRow row1 = ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "refreshPattern~~~" + resolution + "~~~" + pattern, "Generar patrón de nuevo", Emoji.fromUnicode("U+1F504"))
                );

                SelectMenu.Builder menu = SelectMenu.create("newPatternResolution~~~" + pattern);
                menu.setPlaceholder("Selecciona una nuevo tamaño");
                menu.addOption("16x16", "16");
                menu.addOption("32x32", "32");
                menu.addOption("64x64", "64");
                menu.setDefaultValues(Collections.singletonList(Integer.toString(resolution)));

                ActionRow row2 = ActionRow.of(
                        menu.build()
                );

                event.editMessageEmbeds(builder.build()).retainFiles(new ArrayList<>()).addFile(is, "pattern.png").setActionRows(row1, row2).queue();

            } catch (ParseException e) {
                event.editMessageEmbeds(errorEmbed(e.getError())).setActionRows().queue();
            }
        }
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {

        if (event.getComponentId().startsWith("newPatternResolution")) {

            Message message = event.getMessage();

            if (!message.getInteraction().getUser().getId().equals(event.getUser().getId())) {

                event.replyEmbeds(errorEmbed("Solo quien usó el comando puede usar las interacciones.")).setEphemeral(true).queue();
                return;

            }

            String[] parts = event.getComponentId().split("~~~");

            int resolution = Integer.parseInt(event.getSelectedOptions().get(0).getValue());

            String pattern = parts[1];

            try {

                Map<BufferedImage, Integer> textures = parsePattern(pattern);

                BufferedImage image = new BufferedImage(resolution * 16, resolution * 16, BufferedImage.TYPE_INT_RGB);

                Graphics2D graphics = image.createGraphics();

                for (int x = 0; x < resolution; x++) {
                    for (int y = 0; y < resolution; y++) {

                        BufferedImage selectedImage = Misc.selectRandomFromValues(textures);

                        if (selectedImage != null) {
                            graphics.drawImage(selectedImage.getScaledInstance(selectedImage.getWidth(), selectedImage.getHeight(), Image.SCALE_DEFAULT), x * 16, y * 16, null);
                        }

                    }
                }

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Visualizador de patrones (" + resolution + "x" + resolution + ")");
                builder.addField("Patrón:", "`" + pattern +  "`", false);
                builder.setImage("attachment://pattern.png");
                builder.setColor(Color.GREEN);

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    ImageIO.write(image, "png", os);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                ActionRow row1 = ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "refreshPattern~~~" + resolution + "~~~" + pattern, "Generar patrón de nuevo", Emoji.fromUnicode("U+1F504"))
                );

                SelectMenu.Builder menu = SelectMenu.create("newPatternResolution~~~" + pattern);
                menu.setPlaceholder("Selecciona una nuevo tamaño");
                menu.addOption("16x16", "16");
                menu.addOption("32x32", "32");
                menu.addOption("64x64", "64");
                menu.setDefaultValues(Collections.singletonList(Integer.toString(resolution)));

                ActionRow row2 = ActionRow.of(
                        menu.build()
                );

                event.editMessageEmbeds(builder.build()).retainFiles(new ArrayList<>()).addFile(is, "pattern.png").setActionRows(row1, row2).queue();

            } catch (ParseException e) {
                event.editMessageEmbeds(errorEmbed(e.getError())).setActionRows().queue();
            }

        }

    }

    // 25%159:9,75%252:7

    private Map<BufferedImage, Integer> parsePattern(@NotNull String input) throws ParseException {

        final Map<BufferedImage, Integer> pattern = new HashMap<>();

        String[] blocks = input.split(",");

        for (String block : blocks) {

            if (block.isEmpty()) {
                throw new ParseException("Una sección está vacía.");
            }

            String blockData;
            int chance;
            if (block.contains("%")) {

                String[] parts = block.split("%");

                if (parts.length > 2) {
                    throw new ParseException("Formato inválido.");
                }

                if (parts[1].isEmpty()) {
                    throw new ParseException("Hay una sección sin un bloque especificado.");
                }

                if (parts[0].isEmpty()) {
                    throw new ParseException("Falta el porcentaje antes de " + parts[1] + ".");
                }

                try {
                    chance = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    throw new ParseException("Porcentaje inválido.");
                }

                blockData = parts[1];

            } else {

                blockData = block;

                chance = 100;

            }

            int id;
            int data;

            if (blockData.contains(":")) {

                String[] parts = blockData.split(":");

                if (parts.length > 2) {
                    throw new ParseException("Formato inválido.");
                }

                if (parts[1].isEmpty()) {
                    throw new ParseException("Hay una ID sin metadata.");
                }

                if (parts[0].isEmpty()) {
                    throw new ParseException("Hay una sección sin una ID.");
                }

                try {
                    id = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    throw new ParseException("ID de bloque inválido.");
                }

                try {
                    data = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    throw new ParseException("Metadata de bloque inválido.");
                }

            } else {
                try {
                    id = Integer.parseInt(blockData);
                } catch (NumberFormatException e) {
                    throw new ParseException("ID de bloque inválido.");
                }

                data = 0;
            }

            // get buffered image

            BufferedImage croppedImage = allTextures.getSubimage(id * 16, data * 16, 16, 16);

            if (croppedImage.getRGB(0, 0) == Color.CYAN.getRGB() && croppedImage.getRGB(15, 0) == Color.RED.getRGB()) {
                throw new ParseException("Uno de los bloques introducidos no existe o no está disponible.");
            }

            pattern.put(croppedImage, chance);

        }

        return pattern;

    }

}
