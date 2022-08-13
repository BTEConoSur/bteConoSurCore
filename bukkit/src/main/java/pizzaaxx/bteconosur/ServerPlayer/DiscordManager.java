package pizzaaxx.bteconosur.ServerPlayer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import pizzaaxx.bteconosur.configuration.Configuration;
import pizzaaxx.bteconosur.country.OldCountry;

import java.util.UUID;

import static pizzaaxx.bteconosur.country.OldCountry.allCountries;
import static pizzaaxx.bteconosur.discord.Bot.conoSurBot;
import static pizzaaxx.bteconosur.ServerPlayer.GroupsManager.PrimaryGroup.BUILDER;

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

        if (data.contains("discord.name")) {
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

    public void connect(User user, Plugin plugin) {
        setName(user.getName());
        setDiscriminator(user.getDiscriminator());
        setId(user.getId());
        linked = true;
        save();
        Configuration links = new Configuration(plugin, "link/links");
        links.set(id, serverPlayer.getPlayer().getUniqueId().toString());
        links.save();
        for (OldCountry country : allCountries) {
            checkDiscordRoles(country);
        }
    }

    public void disconnect(Plugin plugin) {
        Configuration links = new Configuration(plugin, "link/links");
        links.set(id, null);
        links.save();
        // ----
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

    public void checkDiscordRoles(OldCountry country) {

        if (linked) {
            loadUser();
            Guild guild = country.getGuild();

            Member member;

            try {
                member = guild.retrieveMember(user).complete();
            } catch (ErrorResponseException e) {
                return;
            }


            if (member.isOwner() || (member.getRoles().isEmpty() ? 0 : member.getRoles().get(0).getPosition()) >= guild.retrieveMemberById(conoSurBot.getSelfUser().getId()).complete().getRoles().get(0).getPosition()) {
                return;
            }

            Configuration builderIDs = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "discord/ranks");

            if (serverPlayer.getGroupsManager().getPrimaryGroupFromCountry(country) == BUILDER) {

                if (builderIDs.contains(country.getName() + ".builder")) {

                    String id = builderIDs.getString(country.getName() + ".builder");

                    guild.addRoleToMember(member, guild.getRoleById(id)).queue();

                }

            } else {

                if (builderIDs.contains(country.getName() + ".builder")) {

                    String id = builderIDs.getString(country.getName() + ".builder");

                    guild.removeRoleFromMember(member, guild.getRoleById(id)).queue();
                }


            }
        }

    }
}
