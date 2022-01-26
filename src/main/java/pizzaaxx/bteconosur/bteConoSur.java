package pizzaaxx.bteconosur;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pizzaaxx.bteconosur.chats.events;
import pizzaaxx.bteconosur.commands.googlemaps;
import pizzaaxx.bteconosur.commands.nickname;
import pizzaaxx.bteconosur.commands.nightvision;
import pizzaaxx.bteconosur.discord.bot;
import pizzaaxx.bteconosur.discord.commands.mods;
import pizzaaxx.bteconosur.discord.commands.project;
import pizzaaxx.bteconosur.join.join;
import pizzaaxx.bteconosur.link.linkDiscord;
import pizzaaxx.bteconosur.link.linkMinecraft;
import pizzaaxx.bteconosur.presets.event;
import pizzaaxx.bteconosur.projects.*;
import pizzaaxx.bteconosur.ranks.prefix;
import pizzaaxx.bteconosur.ranks.promote_demote;
import pizzaaxx.bteconosur.teleport.onTeleport;
import pizzaaxx.bteconosur.teleport.pWarp;
import pizzaaxx.bteconosur.testing.testing;
import pizzaaxx.bteconosur.worldedit.incremento;
import pizzaaxx.bteconosur.worldedit.polywall;
import pizzaaxx.bteconosur.worldedit.shortcuts;
import pizzaaxx.bteconosur.yaml.YamlManager;

import javax.security.auth.login.LoginException;
import java.io.File;

import static pizzaaxx.bteconosur.ranks.promote_demote.lp;

public final class bteConoSur extends JavaPlugin {

    public static World mainWorld = null;
    public static File pluginFolder = null;

    @Override
    public void onEnable() {
        getLogger().info("Enabling  BTE Cono Sur!");

        org.bukkit.Bukkit.getPluginManager().registerEvents(new join(), this);
        Bukkit.getPluginManager().registerEvents(new actionBar(), this);
        Bukkit.getPluginManager().registerEvents(new onTeleport(), this);
        Bukkit.getPluginManager().registerEvents(new events(), this);
        Bukkit.getPluginManager().registerEvents(new pRandom(), this);
        Bukkit.getPluginManager().registerEvents(new event(), this);
        Bukkit.getPluginManager().registerEvents(new shortcuts(), this);

        getCommand("btecs_reload").setExecutor(new Config());
        getCommand("project").setExecutor(new command());
        getCommand("link").setExecutor(new linkMinecraft());
        getCommand("unlink").setExecutor(new linkMinecraft());
        getCommand("nightvision").setExecutor(new nightvision());
        getCommand("promote").setExecutor(new promote_demote());
        getCommand("prefix").setExecutor(new prefix());
        getCommand("chat").setExecutor(new pizzaaxx.bteconosur.chats.command());
        getCommand("nickname").setExecutor(new nickname());
        getCommand("test").setExecutor(new testing());
        getCommand("demote").setExecutor(new promote_demote());
        getCommand("project").setTabCompleter(new tabCompletions());
        getCommand("presets").setExecutor(new pizzaaxx.bteconosur.presets.command());
        getCommand("googlemaps").setExecutor(new googlemaps());
        getCommand("increment").setExecutor(new incremento());
        getCommand("pwarp").setExecutor(new pWarp());
        getCommand("/polywalls").setExecutor(new polywall());

        pluginFolder = Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder();
        mainWorld = Bukkit.getWorld("BTECS");

        File configFolder =  new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "");
        configFolder.mkdirs();
        File projectsFolder =  new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "projects");
        projectsFolder.mkdirs();
        File playerDataFolder =  new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "playerData");
        playerDataFolder.mkdirs();
        File linksFolder =  new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "link");
        linksFolder.mkdirs();
        File pendingFolder =  new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "pending_projects");
        pendingFolder.mkdirs();
        File tagsFolder =  new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "projectTags");
        tagsFolder.mkdirs();
        File discordFolder =  new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "discord");
        discordFolder.mkdirs();
        File chatFolder =  new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "chat");
        chatFolder.mkdirs();

        // DISCORD BOT
        JDABuilder builder = JDABuilder.createDefault((String) new YamlManager(pluginFolder, "discord/token.yml").getValue("token"));
        builder.setActivity(Activity.playing("IP: bteconosur.com"));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.addEventListeners(new linkDiscord());
        builder.addEventListeners(new project());
        builder.addEventListeners(new requestResponse());
        builder.addEventListeners(new events());
        builder.addEventListeners(new mods());
        try {
            bot.conoSurBot = builder.build().awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        // CONFIG

        Config.reload();

        // LUCKPERMS

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lp = provider.getProvider();
        }

    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling  BTE Cono Sur!");
    }
}
