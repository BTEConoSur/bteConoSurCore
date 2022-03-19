package pizzaaxx.bteconosur.country;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Country {

    private final String name;
    private final String img;
    private final String prefix;
    private final String abbreviation;
    private final String requestChannelId;
    private final String guildId;

    private final Set<UUID> players;

    public Country(String name,
                   String img,
                   String prefix,
                   String requestChannelId,
                   String abbreviation,
                   String guildId) {
        this.name = name;
        this.img = img;
        this.prefix = prefix;
        this.requestChannelId = requestChannelId;
        this.abbreviation = abbreviation;
        this.guildId = guildId;

        players = new HashSet<>();
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRequestChannelId() {
        return requestChannelId;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getGuildId() {
        return guildId;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

}
