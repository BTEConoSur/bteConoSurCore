package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.util.*;
import java.util.List;

import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;

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
                            choose.setTitle("Elige que comandos quieres ver.");
                            ActionRow actionRow = ActionRow.of(
                                    Button.of(ButtonStyle.SECONDARY, "minecraft", "Minecraft", Emoji.fromMarkdown("<:abcdefg:941030570048258048>")),
                                    Button.of(ButtonStyle.SECONDARY, "discord", "Discord", Emoji.fromMarkdown("<:qwertyu:853810678774759434>"))
                            );
                            e.getTextChannel().sendMessageEmbeds(choose.build()).setActionRows(actionRow).reference(e.getMessage()).mentionRepliedUser(false).queue();
                        } else {

                        }
                    }
                }
            }
        }
    }
}
