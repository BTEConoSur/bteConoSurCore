package pizzaaxx.bteconosur.discord.slashCommands;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.configuration.Configuration;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;

public class HelpCommand extends ListenerAdapter {

    private final Configuration help;

    public HelpCommand(Configuration helpConfig) {
        this.help = helpConfig;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("help")) {

            String root = event.getSubcommandName();

            OptionMapping option = event.getOption("comando");
            if (option == null) {
                ConfigurationSection rootSection = help.getConfigurationSection(root);

                Map<String, List<String>> characters = new HashMap<>();

                for (String key : rootSection.getKeys(false)) {

                    List<String> lines = characters.getOrDefault(key.substring(0, 1), new ArrayList<>());

                    lines.add("• `" +  key + "`: " + rootSection.getString(key + ".description"));

                    Collections.sort(lines);

                    characters.put(key.substring(0, 1), lines);

                }

                List<String> keys = new ArrayList<>(characters.keySet());
                Collections.sort(keys);
                List<List<String>> dividedCharacters = Lists.partition(keys, 6);

                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.GREEN);
                builder.setTitle("Ayuda de comandos de " + StringUtils.capitalize(root));
                builder.addField("Sistema de ayuda:", "Usa `/help [comando]` para obtener más información de cada comando.", false);

                for (String character : dividedCharacters.get(0)) {
                    String title;
                    if (character.matches("[0-9]")) {

                        switch (character) {
                            case "1":
                                title = ":one:";
                                break;
                            case "2":
                                title = ":two:";
                                break;
                            case "3":
                                title = ":three:";
                                break;
                            case "4":
                                title = ":four:";
                                break;
                            case "5":
                                title = ":five:";
                                break;
                            case "6":
                                title = ":six:";
                                break;
                            case "7":
                                title = ":seven:";
                                break;
                            case "8":
                                title = ":eight:";
                                break;
                            case "9":
                                title = ":nine:";
                                break;
                            default:
                                title = ":zero:";
                                break;
                        }

                    } else {
                        title = ":regional_indicator_" + character + ":";
                    }
                    builder.addField(title, String.join("\n", characters.get(character)), false);
                }

                ActionRow row = ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "previousHelp_" + root, "Pág. anterior", Emoji.fromUnicode("U+2B05")).withDisabled(true),
                        Button.of(ButtonStyle.SECONDARY, "number", "1/" + dividedCharacters.size()).withDisabled(true),
                        Button.of(ButtonStyle.PRIMARY, "nextHelp_" + root, "Pág. siguiente", Emoji.fromUnicode("U+27A1")).withDisabled(dividedCharacters.size() == 1)
                        );

                event.replyEmbeds(builder.build()).addActionRows(row).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                );
            } else {
                String commandPath = option.getAsString();

                // /help minecraft project add

                String fixedPath = root + "." + String.join(".subcommands.", commandPath.split(" "));

                if (help.contains(fixedPath)) {

                    ConfigurationSection commandSection = help.getConfigurationSection(fixedPath);

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.GREEN);
                    builder.setTitle(commandSection.getString("usage"));
                    builder.addField(":open_file_folder: Descripción:", commandSection.getString("description"), false);
                    builder.addField(":label: Tiene(n) permisos:", commandSection.getString("permission"), false);
                    if (commandSection.contains("aliases")) {
                        List<String> lines = new ArrayList<>();
                        for (String alias : commandSection.getStringList("aliases")) {
                            lines.add("• `" + alias + "`");
                        }
                        builder.addField(":paperclip: Aliases:", String.join("\n", lines), false);
                    }
                    if (commandSection.contains("note")) {
                        builder.addField(":notepad_spiral: Nota:", commandSection.getString("note"), false);
                    }
                    if (commandSection.contains("parameters")) {
                        List<String> lines = new ArrayList<>();
                        ConfigurationSection parameters = commandSection.getConfigurationSection("parameters");
                        for (String parameter : parameters.getKeys(false)) {
                            lines.add("• **" + parameter + ":** " + parameters.getString(parameter));
                        }
                        builder.addField(":ledger: Parámetros:", String.join("\n", lines), false);
                    }
                    if (commandSection.contains("subcommands")) {
                        List<String> lines = new ArrayList<>();
                        lines.add("*Usa `/help " + commandPath + " [subcomando] para ver más información de cada subcomando.*");
                        ConfigurationSection subcommands = commandSection.getConfigurationSection("subcommands");
                        for (String subcommand : subcommands.getKeys(false)) {
                            lines.add("• `" + subcommands.getString(subcommand + ".usage").replace("/" + commandPath + " ", "") + "`");
                        }
                        Collections.sort(lines);
                        builder.addField(":notebook_with_decorative_cover: Subcomandos:", String.join("\n", lines), false);
                    }
                    event.replyEmbeds(builder.build()).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                    );

                } else {
                    event.replyEmbeds(errorEmbed("El comando introducido no existe.")).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                }
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        if (!event.getMessage().getInteraction().getUser().getId().equals(event.getUser().getId())) {
            event.replyEmbeds(errorEmbed("Sólo quién uso el comando puede usar los botones.")).setEphemeral(true).queue();
        }

        if (event.getComponentId().startsWith("nextHelp_")) {

            String root = event.getComponentId().replace("nextHelp_", "");

            ConfigurationSection rootSection = help.getConfigurationSection(root);

            Map<String, List<String>> characters = new HashMap<>();

            for (String key : rootSection.getKeys(false)) {

                List<String> lines = characters.getOrDefault(key.substring(0, 1), new ArrayList<>());

                lines.add("• `" +  key + "`: " + rootSection.getString(key + ".description"));

                Collections.sort(lines);

                characters.put(key.substring(0, 1), lines);

            }


            List<String> keys = new ArrayList<>(characters.keySet());
            Collections.sort(keys);
            List<List<String>> dividedCharacters = Lists.partition(keys, 6);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.GREEN);
            builder.setTitle("Ayuda de comandos de " + StringUtils.capitalize(root));
            builder.addField("Sistema de ayuda:", "Usa `/help [comando]` para obtener más información de cada comando.", false);

            int index = Integer.parseInt(event.getMessage().getButtonById("number").getLabel().split("/")[0]) + 1;

            for (String character : dividedCharacters.get(index - 1)) {
                String title;
                if (character.matches("[0-9]")) {

                    switch (character) {
                        case "1":
                            title = ":one:";
                            break;
                        case "2":
                            title = ":two:";
                            break;
                        case "3":
                            title = ":three:";
                            break;
                        case "4":
                            title = ":four:";
                            break;
                        case "5":
                            title = ":five:";
                            break;
                        case "6":
                            title = ":six:";
                            break;
                        case "7":
                            title = ":seven:";
                            break;
                        case "8":
                            title = ":eight:";
                            break;
                        case "9":
                            title = ":nine:";
                            break;
                        default:
                            title = ":zero:";
                            break;
                    }

                } else {
                    title = ":regional_indicator_" + character + ":";
                }
                builder.addField(title, String.join("\n", characters.get(character)), false);
            }



            ActionRow row = ActionRow.of(
                    Button.of(ButtonStyle.PRIMARY, "previousHelp_" + root, "Pág. anterior", Emoji.fromUnicode("U+2B05")),
                    Button.of(ButtonStyle.SECONDARY, "number",  index + "/" + dividedCharacters.size()).withDisabled(true),
                    Button.of(ButtonStyle.PRIMARY, "nextHelp_" + root, "Pág. siguiente", Emoji.fromUnicode("U+27A1")).withDisabled(dividedCharacters.size() == index)
            );



            event.editMessageEmbeds(builder.build()).setActionRows(row).queue(
                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
            );
        } else if (event.getComponentId().startsWith("previousHelp_")) {
            String root = event.getComponentId().replace("previousHelp_", "");

            ConfigurationSection rootSection = help.getConfigurationSection(root);

            Map<String, List<String>> characters = new HashMap<>();

            for (String key : rootSection.getKeys(false)) {

                List<String> lines = characters.getOrDefault(key.substring(0, 1), new ArrayList<>());

                lines.add("• `" +  key + "`: " + rootSection.getString(key + ".description"));

                Collections.sort(lines);

                characters.put(key.substring(0, 1), lines);

            }

            List<String> keys = new ArrayList<>(characters.keySet());
            Collections.sort(keys);
            List<List<String>> dividedCharacters = Lists.partition(keys, 6);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.GREEN);
            builder.setTitle("Ayuda de comandos de " + StringUtils.capitalize(root));
            builder.addField("Sistema de ayuda:", "Usa `/help [comando]` para obtener más información de cada comando.", false);

            int index = Integer.parseInt(event.getMessage().getButtonById("number").getLabel().split("/")[0]) - 1;

            for (String character : dividedCharacters.get(index - 1)) {
                String title;
                if (character.matches("[0-9]")) {

                    switch (character) {
                        case "1":
                            title = ":one:";
                            break;
                        case "2":
                            title = ":two:";
                            break;
                        case "3":
                            title = ":three:";
                            break;
                        case "4":
                            title = ":four:";
                            break;
                        case "5":
                            title = ":five:";
                            break;
                        case "6":
                            title = ":six:";
                            break;
                        case "7":
                            title = ":seven:";
                            break;
                        case "8":
                            title = ":eight:";
                            break;
                        case "9":
                            title = ":nine:";
                            break;
                        default:
                            title = ":zero:";
                            break;
                    }

                } else {
                    title = ":regional_indicator_" + character + ":";
                }
                builder.addField(title, String.join("\n", characters.get(character)), false);
            }

            ActionRow row = ActionRow.of(
                    Button.of(ButtonStyle.PRIMARY, "previousHelp_" + root, "Pág. anterior", Emoji.fromUnicode("U+2B05")).withDisabled(index == 1),
                    Button.of(ButtonStyle.SECONDARY, "number",  index + "/" + dividedCharacters.size()).withDisabled(true),
                    Button.of(ButtonStyle.PRIMARY, "nextHelp_" + root, "Pág. siguiente", Emoji.fromUnicode("U+27A1"))
            );

            event.editMessageEmbeds(builder.build()).setActionRows(row).queue(
                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
            );
        }
    }

}
