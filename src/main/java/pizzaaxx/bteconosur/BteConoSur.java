package pizzaaxx.bteconosur;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
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
import pizzaaxx.bteconosur.teleport.OnTeleport;
import pizzaaxx.bteconosur.teleport.PWarp;
import pizzaaxx.bteconosur.testing.Testing;
import pizzaaxx.bteconosur.worldedit.Incremento;
import pizzaaxx.bteconosur.worldedit.Polywall;
import pizzaaxx.bteconosur.worldedit.ShortCuts;
import pizzaaxx.bteconosur.worldedit.trees.Events;
import pizzaaxx.bteconosur.yaml.YamlManager;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;

import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.discord.bot.conoSurBot;
import static pizzaaxx.bteconosur.projects.command.background;
import static pizzaaxx.bteconosur.ranks.promote_demote.lp;

public final class BteConoSur extends JavaPlugin {

    public static World mainWorld = null;
    public static File pluginFolder = null;
    public static String key;

    @Override
    public void onEnable() {

        getLogger().info("Enabling  BTE Cono Sur!");

        registerListeners(
                new join(),
                new projectActionBar(),
                new OnTeleport(),
                new event(),
                new pRandom(),
                new event(),
                new ShortCuts(),
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
        getCommand("test").setExecutor(new Testing());
        getCommand("demote").setExecutor(new promote_demote());
        getCommand("project").setTabCompleter(new tabCompletions());
        getCommand("presets").setExecutor(new pizzaaxx.bteconosur.presets.command());
        getCommand("googlemaps").setExecutor(new googlemaps());
        getCommand("increment").setExecutor(new Incremento());
        getCommand("pwarp").setExecutor(new PWarp());
        getCommand("/polywalls").setExecutor(new Polywall());
        getCommand("treegroup").setExecutor(new Events());
        getCommand("/treecover").setExecutor(new Events());
        getCommand("donator").setExecutor(new donator());
        getCommand("streamer").setExecutor(new streamer());
        getCommand("streaming").setExecutor(new streaming());
        getCommand("get").setExecutor(new get());
        getCommand("scoreboard").setExecutor(new scoreboard());
        getCommand("tpdir").setExecutor(new tpdir());
        getCommand("event").setExecutor(new pizzaaxx.bteconosur.events.command());

        pluginFolder = Bukkit.getPluginManager().getPlugin("bteConoSur").getDataFolder();
        mainWorld = Bukkit.getWorld("BTECS");

        createDirectories(
                "",
                "projects",
                "playerData",
                "link",
                "pending_projects",
                "projectTags",
                "discord",
                "chat",
                "points",
                "trees/schematics"
        );

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

        registerDiscordListener(builder,
                new linkDiscord(),
                new project(),
                new requestResponse(),
                new events(),
                new mods(),
                new schematic(),
                new player(),
                new online_where(),
                new pizzaaxx.bteconosur.discord.commands.scoreboard(),
                new help(),
                new helpButtons()
        );

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

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, scoreboard::checkAutoScoreboards, 300, 300);
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

    private void registerDiscordListener(JDABuilder builder, EventListener... listeners) {
        for (EventListener listener : listeners) {
            builder.addEventListeners(listener);
        }
    }

    private void createDirectories(String... names) {
        for (String name : names) {
            File file = new File(getDataFolder(), name);
            file.mkdirs();
        }
    }

}
