package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;

public class ModsCommand implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getMessage().getContentRaw().startsWith("/")) {
                String fullCommand = e.getMessage().getContentRaw();
                String[] args = fullCommand.replaceFirst("/", "").split(" ");
                if (args.length > 0) {
                    if (args[0].equals("mods")) {
                        File file = new File(pluginFolder, "modsBTECS.zip");

                        e.getTextChannel().sendMessage("Estos son los mods que usamos en el servidor, si necesitas ayuda instalándolos, ve.").addFile(file).reference(e.getMessage()).mentionRepliedUser(false).queue();
                    }
                }
            }
        }
    }
}
