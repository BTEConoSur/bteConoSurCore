package pizzaaxx.bteconosur.discord.fuzzyMatching;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FuzzyMatchListenerHandler implements EventListener {

    private final LevenshteinDistance ld = LevenshteinDistance.getDefaultInstance();
    private final List<String> texts = new ArrayList<>();
    private final Map<String, FuzzyMatchListener> listeners = new HashMap<>();
    private final Map<String, MatchType> matchTypes = new HashMap<>();
    private final Map<String, Integer> distances = new HashMap<>();
    private final Map<String, List<String>> mustContains = new HashMap<>();

    public enum MatchType {
        WHOLE, PARTIAL
    }

    /**
     * Register a text to be approximately recognised.
     * @param text The text to be recognised. Can't be null.
     * @param listener The listener of this text.
     * @param matchType Whether it should match the whole message or just a portion of it.
     */
    public void registerListener(@NotNull String text, FuzzyMatchListener listener, MatchType matchType) {
        texts.add(text);
        listeners.put(text, listener);
        matchTypes.put(text, matchType);
        distances.put(text, 5);
        mustContains.put(text, Collections.emptyList());
    }

    /**
     * Register a text to be approximately recognised.
     * @param text The text to be recognised. Can't be null.
     * @param listener The listener of this text.
     * @param matchType Whether it should match the whole message or just a portion of it.
     * @param distance The maximum edit distance for this text to be recognised.
     */
    public void registerListener(@NotNull String text, FuzzyMatchListener listener, MatchType matchType, int distance) {
        texts.add(text);
        listeners.put(text, listener);
        matchTypes.put(text, matchType);
        distances.put(text, distance);
        mustContains.put(text, Collections.emptyList());
    }

    /**
     * Register a text to be approximately recognised.
     * @param text The text to be recognised. Can't be null.
     * @param listener The listener of this text.
     * @param matchType Whether it should match the whole message or just a portion of it.
     * @param mustContain A set of strings that the original text must contain in order to be recognised.
     */
    public void registerListener(@NotNull String text, FuzzyMatchListener listener, MatchType matchType, String... mustContain) {
        texts.add(text);
        listeners.put(text, listener);
        matchTypes.put(text, matchType);
        distances.put(text, 5);
        mustContains.put(text, Arrays.asList(mustContain));
    }

    /**
     * Register a text to be approximately recognised.
     * @param text The text to be recognised. Can't be null.
     * @param listener The listener of this text.
     * @param matchType Whether it should match the whole message or just a portion of it.
     * @param distance The maximum edit distance for this text to be recognised.
     * @param mustContain A set of strings that the original text must contain in order to be recognised.
     */
    public void registerListener(@NotNull String text, FuzzyMatchListener listener, MatchType matchType, int distance, String... mustContain) {
        texts.add(text);
        listeners.put(text, listener);
        matchTypes.put(text, matchType);
        distances.put(text, distance);
        mustContains.put(text, Arrays.asList(mustContain));
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof MessageReceivedEvent) {
            MessageReceivedEvent event = (MessageReceivedEvent) genericEvent;

            Message message = event.getMessage();

            String messageContent = message.getContentDisplay();

            outerLoop:
            for (String text : texts) {
                int maxDistance = distances.get(text);
                List<String> mustContain = mustContains.get(text);

                for (String match : mustContain) {
                    if (!messageContent.toLowerCase().contains(match.toLowerCase())) {
                        continue outerLoop;
                    }
                }

                // -------------

                MatchType type = matchTypes.get(text);

                if (type == MatchType.WHOLE) {
                    int editDistance = ld.apply(messageContent, text);
                    if (editDistance <= maxDistance) {
                        listeners.get(text).onFuzzyMatch(message, messageContent, messageContent);
                    }
                } else {
                    if (messageContent.length() < text.length()) {
                        int editDistance = ld.apply(messageContent, text);
                        if (editDistance <= maxDistance) {
                            listeners.get(text).onFuzzyMatch(message, messageContent, messageContent);
                        }
                    } else {
                        for (int i = 0; i <= messageContent.length() - text.length(); i++) {
                            String substring = messageContent.substring(i, i+text.length());
                            int editDistance = ld.apply(substring.toLowerCase(), text.toLowerCase());
                            if (editDistance <= maxDistance) {
                                listeners.get(text).onFuzzyMatch(message, messageContent, substring);
                                break outerLoop;
                            }
                        }
                    }
                }
            }
        }
    }
}