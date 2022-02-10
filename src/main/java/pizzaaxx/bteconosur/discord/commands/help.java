package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;


import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;

public class help implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getMessage().getContentRaw().startsWith("/")) {
                String fullCommand = e.getMessage().getContentRaw();
                String[] args = fullCommand.replaceFirst("/", "").split(" ");
                if (args.length > 0) {
                    if (args[0].equals("help")) {
                        if (args.length == 1) {
                            EmbedBuilder choose = new EmbedBuilder();
                            choose.setColor(new Color(0,255,42));
                            choose.setTitle("Elige qué comandos quieres ver.");
                            ActionRow actionRow = ActionRow.of(
                                    Button.of(ButtonStyle.SECONDARY, "minecraft", "Minecraft", Emoji.fromMarkdown("<:abcdefg:941030570048258048>")),
                                    Button.of(ButtonStyle.SECONDARY, "discord", "Discord", Emoji.fromMarkdown("<:qwertyu:853810678774759434>"))
                            );

                            e.getTextChannel().sendMessageEmbeds(choose.build()).setActionRows(actionRow).reference(e.getMessage()).mentionRepliedUser(false).queue();
                        } else {


                            YamlManager yaml = new YamlManager(pluginFolder, "help.yml");
                            String platform = null;
                            if (((Map<String, Object>) yaml.getValue("minecraft")).containsKey(args[1])) {
                                platform = "minecraft";
                            } else if (((Map<String, Object>) yaml.getValue("discord")).containsKey(args[1])) {
                                platform = "discord";
                            }
                            String path = platform + "." + String.join(".subcommands.", Arrays.asList(args).subList(1, args.length));
                            Map<String, Object> command = (Map<String, Object>) yaml.getValue(path);

                            if (platform != null && command != null) {
                                EmbedBuilder help = new EmbedBuilder();
                                help.setColor(new Color(0, 255, 42));
                                help.setTitle((String) command.get("usage"));

                                help.addField(":open_file_folder: Descripción:", (String) command.get("description"), false);
                                help.addField(":label: Tiene(n) permisos:", (String) command.get("permission"), false);

                                if (command.containsKey("aliases")) {
                                    List<String> aliases = new ArrayList<>();
                                    for (String alias : (List<String>) command.get("aliases")) {
                                        aliases.add("• `" + alias + "`");
                                    }
                                    help.addField(":paperclip: Aliases:", String.join("\n", aliases), false);
                                }

                                if (command.containsKey("note")) {
                                    help.addField(":notepad_spiral: Nota:", (String) command.get("note"), false);
                                }

                                if (command.containsKey("examples")) {
                                    List<String> examples = new ArrayList<>();
                                    for (String alias : (List<String>) command.get("examples")) {
                                        examples.add("• `" + alias + "`");
                                    }
                                    help.addField(":placard: Ejemplos:", String.join("\n", examples), false);
                                }

                                if (command.containsKey("parameters")) {
                                    Map<String, String> parameters = (Map<String, String>) command.get("parameters");
                                    List<String> params = new ArrayList<>();
                                    for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                                        params.add("• **" + parameter.getKey() + ":** " + parameter.getValue());
                                    }
                                    help.addField(":ledger: Parámetros:", String.join("\n", params), false);
                                }

                                if (command.containsKey("subcommands")) {
                                    Map<String, Object> subs = (Map<String, Object>) command.get("subcommands");
                                    List<String> subcommands = new ArrayList<>();
                                    for (Map.Entry<String, Object> subcommand : subs.entrySet()) {
                                        subcommands.add("• `" + subcommand.getKey() + "`");
                                    }
                                    help.addField(":notebook_with_decorative_cover: Subcomandos:", String.join("\n", subcommands), false);
                                }

                                if (command.containsKey("image")) {
                                    help.setImage((String) command.get("image"));
                                }

                                e.getTextChannel().sendMessageEmbeds(help.build()).reference(e.getMessage()).mentionRepliedUser(false).queue(msg -> msg.delete().queueAfter(10, TimeUnit.MINUTES));
                                e.getMessage().delete().queueAfter(10, TimeUnit.MINUTES);

                            } else {
                                EmbedBuilder error = new EmbedBuilder();
                                error.setColor(new Color(255, 0,0));
                                error.setTitle("El comando/subcomando introducido no existe.");
                                e.getTextChannel().sendMessageEmbeds(error.build()).reference(e.getMessage()).mentionRepliedUser(false).queue(msg -> msg.delete().queueAfter(1, TimeUnit.MINUTES));
                                e.getMessage().delete().queueAfter(1, TimeUnit.MINUTES);
                            }
                        }
                    }
                }
            }
        }
    }
}
