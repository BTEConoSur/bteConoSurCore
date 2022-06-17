package pizzaaxx.bteconosur.discord.fuzzyMatching;

import net.dv8tion.jda.api.entities.Message;

public interface FuzzyMatchListener {

    void onFuzzyMatch(Message message, String matchedText, String match);

}
