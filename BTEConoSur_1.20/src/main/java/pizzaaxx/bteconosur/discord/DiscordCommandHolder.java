package pizzaaxx.bteconosur.discord;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface DiscordCommandHolder {

    CommandData[] getCommandData();

}