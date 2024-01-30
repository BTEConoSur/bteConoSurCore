package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface DiscordCommandHolder {

    CommandData[] getCommandData();

}
