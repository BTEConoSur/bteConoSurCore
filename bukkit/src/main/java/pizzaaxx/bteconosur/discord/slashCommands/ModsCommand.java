package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ModsCommand extends ListenerAdapter {

    private final File file;

    public ModsCommand(File mods) {
        this.file = mods;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.replyFile(file).setContent("Estos son los *mods* que usamos en el servidor. Si necesitas ayuda instalándolos, ve <#942838899863081010>.").queue(
                msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
        );
    }
}
