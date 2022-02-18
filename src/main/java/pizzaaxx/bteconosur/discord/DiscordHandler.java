package pizzaaxx.bteconosur.discord;

import pizzaaxx.bteconosur.country.Country;

public class DiscordHandler {

    public void log(Country country, String message) {
        country.getLogs().sendMessage(message).queue();
    }

}
