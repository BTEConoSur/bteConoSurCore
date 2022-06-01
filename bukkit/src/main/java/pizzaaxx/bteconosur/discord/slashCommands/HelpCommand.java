package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.configuration.ConfigurationSection;
import pizzaaxx.bteconosur.yaml.Configuration;

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

                    List

                }
            } else {
                String commandPath = option.getAsString();

                // /help minecraft project add

                String fixedPath = root + "." + String.join(".subcommand.", commandPath.split(" "));

                if (help.contains(fixedPath)) {

                    ConfigurationSection commandSection = help.getConfigurationSection(fixedPath);

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.GREEN);
                    builder.setTitle("/" + commandPath);
                    builder.addField(":open_file_folder: Descripción:", commandSection.getString("usage"), false);
                    builder.addField(":label: Tiene(n) permisos:", commandSection.getString("permission"), false);
                    if (commandSection.contains("aliases")) {
                        List<String> lines = new ArrayList<>();
                        for (String alias : commandSection.getStringList("aliases")) {
                            lines.add("• `/" + alias + "`");
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

}
