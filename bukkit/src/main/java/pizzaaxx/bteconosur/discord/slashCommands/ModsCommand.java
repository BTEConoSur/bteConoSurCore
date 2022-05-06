package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ModsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("mods")) {
            event.reply("Estos son los *mods* que usamos en el servidor. Si necesitas ayuda instalándolos, ve <#942838899863081010>.\n\nhttps://cutt.ly/modsBTECS").queue(
                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
            );
        }
    }
}
