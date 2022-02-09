package pizzaaxx.bteconosur;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pizzaaxx.bteconosur.chats.events;
import pizzaaxx.bteconosur.commands.*;
import pizzaaxx.bteconosur.discord.commands.*;
import pizzaaxx.bteconosur.join.join;
import pizzaaxx.bteconosur.link.linkDiscord;
import pizzaaxx.bteconosur.link.linkMinecraft;
import pizzaaxx.bteconosur.points.scoreboard;
import pizzaaxx.bteconosur.presets.event;
import pizzaaxx.bteconosur.projects.*;
import pizzaaxx.bteconosur.ranks.donator;
import pizzaaxx.bteconosur.ranks.prefix;
import pizzaaxx.bteconosur.ranks.promote_demote;
import pizzaaxx.bteconosur.ranks.streamer;
import pizzaaxx.bteconosur.teleport.onTeleport;
import pizzaaxx.bteconosur.teleport.pWarp;
import pizzaaxx.bteconosur.testing.testing;
import pizzaaxx.bteconosur.worldedit.incremento;
import pizzaaxx.bteconosur.worldedit.polywall;
import pizzaaxx.bteconosur.worldedit.shortcuts;
import pizzaaxx.bteconosur.yaml.YamlManager;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.discord.bot.conoSurBot;
import static pizzaaxx.bteconosur.points.scoreboard.checkAutoScoreboards;
import static pizzaaxx.bteconosur.projects.command.background;
import static pizzaaxx.bteconosur.ranks.promote_demote.lp;

public final class bteConoSur extends JavaPlugin {

    public static World mainWorld = null;
    public static File pluginFolder = null;
    public static String key;

    @Override
    public void onEnable() {

        getLogger().info("Enabling  BTE Cono Sur!");

        registerListeners(
                new join(),
                new projectActionBar(),
                new onTeleport(),
                new event(),
                new pRandom(),
                new event(),
                new shortcuts(),
                new events(),
                new scoreboard(),
                new get()
        );

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
        getCommand("treegroup").setExecutor(new pizzaaxx.bteconosur.worldedit.trees.events());
        getCommand("/treecover").setExecutor(new pizzaaxx.bteconosur.worldedit.trees.events());
        getCommand("donator").setExecutor(new donator());
        getCommand("streamer").setExecutor(new streamer());
        getCommand("streaming").setExecutor(new streaming());
        getCommand("get").setExecutor(new get());
        getCommand("scoreboard").setExecutor(new scoreboard());
        getCommand("tpdir").setExecutor(new tpdir());
        getCommand("event").setExecutor(new pizzaaxx.bteconosur.events.command());

        pluginFolder = Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder();
        mainWorld = Bukkit.getWorld("BTECS");

        new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "").mkdirs();
        new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "projects").mkdirs();
        new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "playerData").mkdirs();
        new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "link").mkdirs();
        new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "pending_projects").mkdirs();
        new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "projectTags").mkdirs();
        new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "discord").mkdirs();
        new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "chat").mkdirs();
        new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "points").mkdirs();
        new File(Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder(), "trees/schematics").mkdirs();

        // GUI
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta gMeta = glass.getItemMeta();
        gMeta.setDisplayName(" ");
        glass.setItemMeta(gMeta);
        background = glass;

        // DISCORD BOT
        JDABuilder builder = JDABuilder.createDefault((String) new YamlManager(pluginFolder, "discord/token.yml").getValue("token"));
        builder.setActivity(Activity.playing("IP: bteconosur.com"));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.addEventListeners(new linkDiscord());
        builder.addEventListeners(new project());
        builder.addEventListeners(new requestResponse());
        builder.addEventListeners(new events());
        builder.addEventListeners(new mods());
        builder.addEventListeners(new schematic());
        builder.addEventListeners(new player());
        builder.addEventListeners(new online_where());
        builder.addEventListeners(new pizzaaxx.bteconosur.discord.commands.scoreboard());
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES);
        try {
            conoSurBot = builder.build().awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        key = (String) new YamlManager(pluginFolder, "key.yml").getValue("key");

        // CONFIG

        Config.reload();

        // LUCKPERMS

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lp = provider.getProvider();
        }

        EmbedBuilder online = new EmbedBuilder();
        online.setColor(new Color(0, 255, 42));
        online.setTitle("¡El servidor ya está online!");
        online.setDescription("\uD83D\uDD17 **IP:** bteconosur.com");

        gateway.sendMessageEmbeds(online.build()).queue();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                checkAutoScoreboards();
            }
        };

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, runnable, 300, 300);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling  BTE Cono Sur!");

        EmbedBuilder online = new EmbedBuilder();
        online.setColor(new Color(255, 0, 0));
        online.setTitle("El servidor ha sido apagado.");
        online.setDescription("Te esperamos cuando vuelva a estar disponible.");

        gateway.sendMessageEmbeds(online.build()).queue();

        conoSurBot.shutdown();
    }

    public static void broadcast(String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ServerPlayer s = new ServerPlayer(p);
            if (!(s.isChatHidden())) {
                p.sendMessage(message);
            }
        }
    }

    public static void broadcast(BaseComponent message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ServerPlayer s = new ServerPlayer(p);
            if (!(s.isChatHidden())) {
                p.sendMessage(message);
            }
        }
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager()
                    .registerEvents(listener, this);
        }
    }

}
