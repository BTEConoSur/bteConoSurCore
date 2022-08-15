package pizzaaxx.bteconosur.ServerPlayer.Managers;

import net.dv8tion.jda.api.entities.User;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.ServerPlayer.LinksManager;
import pizzaaxx.bteconosur.ServerPlayer.Managers.DataManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

public class DiscordManager {

    private final BteConoSur plugin;
    private final LinksManager linksManager;
    private final ServerPlayer serverPlayer;
    private final DataManager data;
    private final ConfigurationSection discord;
    private boolean linked;
    private String name = null;
    private String discriminator = null;
    private String id = null;
    private User user = null;

    public DiscordManager(@NotNull ServerPlayer s) {
        serverPlayer = s;
        this.plugin = s.getPlugin();
        this.linksManager = s.getPlayerRegistry().getLinksManager();

        data = serverPlayer.getDataManager();

        if (linksManager.isLinked(s.getId())) {
            discord = data.getConfigurationSection("discord");
            linked = true;

            name = discord.getString("name");
            discriminator = discord.getString("discriminator");
            id = discord.getString("id");
        } else {
            discord = data.createSection("discord");
            linked = false;
        }

    }

    public void connect(@NotNull User user) {
        setName(user.getName());
        setDiscriminator(user.getDiscriminator());
        setId(user.getId());
        linked = true;
        save();
        linksManager.link(serverPlayer.getId(), user.getId());
    }

    public void disconnect() {
        linksManager.unlink(serverPlayer.getId());
        setName(null);
        setDiscriminator(null);
        setId(null);
        user = null;
        linked = false;
        save();
    }

    public void setName(String name) {
        this.name = name;
        discord.set("name", name);
    }

    public void setId(String id) {
        this.id = id;
        discord.set("id", id);
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
        discord.set("discriminator", discriminator);
    }

    public boolean isLinked() {
        return linked;
    }

    public void loadUser() {
        if (linked) {
            if (user == null) {
                user = plugin.getBot().retrieveUserById(id).complete();
            }
        }
    }

    private void save() {
        data.set("discord", discord);
        data.save();
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public String getId() {
        return id;
    }
}
