package pizzaaxx.bteconosur.ServerPlayer;

import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.HelpMethods.DualMap;
import pizzaaxx.bteconosur.configuration.Configuration;

import java.util.UUID;

public class LinksManager {

    private final DualMap<UUID, String> links = new DualMap<>();
    private final Configuration config;

    public LinksManager(BteConoSur plugin) {
        this.config = new Configuration(plugin, "discord/links");

        for (String key : config.getKeys(false)) {
            links.put(UUID.fromString(config.getString(key)), key);
        }
    }

    public void link(UUID uuid, String discordId) {

        if (links.contains2(discordId) && links.get1(discordId) != uuid) {
            config.set(links.get1(discordId).toString(), null);
        }
        links.put(uuid, discordId);
        config.set(uuid.toString(), discordId);
        config.save();
    }

    public void unlink(UUID uuid) {
        links.remove1(uuid);
        config.set(uuid.toString(), null);
        config.save();
    }

    public void unlink(String discordId) {
        this.unlink(links.get1(discordId));
    }

    public boolean isLinked(UUID uuid) {
        return links.contains1(uuid);
    }

    public boolean isLinked(String discordID) {
        return links.contains2(discordID);
    }

    public UUID getFromUUID(String discordID) {
        return links.get1(discordID);
    }

    public String getFromID(UUID uuid) {
        return links.get2(uuid);
    }

}
