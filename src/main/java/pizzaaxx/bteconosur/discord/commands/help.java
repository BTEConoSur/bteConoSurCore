package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.discord.bot.conoSurBot;

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
                                platform = "Discord";
                            } else if (((Map<String, Object>) yaml.getValue("discord")).containsKey(args[1])) {
                                platform = "Minecraft";
                            }
                            String path = platform + "." + String.join(".subcommands.", Arrays.asList(args).subList(0, args.length));
                            Map<String, Object> command = (Map<String, Object>) yaml.getValue(path);

                            if (platform != null && command != null) {
                                EmbedBuilder help = new EmbedBuilder();
                                help.setColor(new Color(0, 255, 42));
                                help.setTitle((String) command.get("usage"));



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
