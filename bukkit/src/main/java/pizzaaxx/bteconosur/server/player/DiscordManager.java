package pizzaaxx.bteconosur.server.player;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.yaml.Configuration;

import java.util.UUID;

import static pizzaaxx.bteconosur.discord.Bot.conoSurBot;

public class DiscordManager {

    private final ServerPlayer serverPlayer;
    private final DataManager data;
    private final ConfigurationSection discord;
    private boolean linked;
    private String name = null;
    private String discriminator = null;
    private String id = null;
    private User user = null;

    public DiscordManager(ServerPlayer s) {
        serverPlayer = s;

        data = serverPlayer.getDataManager();

        if (data.contains("discord")) {
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

    public static boolean isLinked(String id) {
        Configuration links = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "link/links");
        return links.contains(id);
    }

    public static OfflinePlayer getFromID(String id) {
        Configuration links = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "link/links");
        if (links.contains(id)) {
            return Bukkit.getOfflinePlayer(UUID.fromString(links.getString(id)));
        }
        return null;
    }

    public void connect(User user) {
        setName(user.getName());
        setDiscriminator(user.getDiscriminator());
        setId(user.getId());
        linked = true;
        save();
        Configuration links = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "link/links");
        links.set(id, serverPlayer.getPlayer().getUniqueId());
        links.save();
    }

    public void disconnect() {
        setName(null);
        setDiscriminator(null);
        setId(null);
        user = null;
        linked = false;
        save();
        Configuration links = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "link/links");
        links.set(id, null);
        links.save();
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
                user = conoSurBot.retrieveUserById(id).complete();
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

    public void checkDiscordBuilder(OldCountry country) {
        if (linked) {
            loadUser();
            Guild guild = country.getGuild();
            Member member = guild.getMember(user);
            Role role = guild.getRoleById(new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "discord/ranks").getConfigurationSection(country.getName()).getString("builder"));
            if (serverPlayer.getPointsManager().getPoints(country) >= 15) {
                if (!member.getRoles().contains(role)) {
                    country.getGuild().addRoleToMember(member, role).queue();
                }
            } else {
                if (member.getRoles().contains(role)) {
                    country.getGuild().removeRoleFromMember(member, role).queue();
                }
            }
        }
    }
}
