package pizzaaxx.bteconosur.events;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.helper.Pair;
import pizzaaxx.bteconosur.player.data.PlayerData;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

import static pizzaaxx.bteconosur.BteConoSur.*;
import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.events.EventsCommand.eventsPrefix;
import static pizzaaxx.bteconosur.misc.Misc.getMapURL;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class Event {
    private Status status;
    private String name;
    private String date;
    private Location tp;
    private Integer minPoints;
    private String image;
    private List<OfflinePlayer> participants = new ArrayList<>();
    private ProtectedPolygonalRegion region;
    private final YamlManager yaml;
    private final Country country;

    enum Status {
        READY, OFF, ON
    }

    public Event(Country country) {
        this.country = country;
        String c = country.getName();
        yaml = new YamlManager(pluginFolder, "events.yml");
        status = Status.valueOf(((String) yaml.getValue(c + ".status")).toUpperCase());
        name = (String) yaml.getValue(c + ".name");
        date = (String) yaml.getValue(c + ".date");
        image = (String) yaml.getValue(c + ".image");
        minPoints = (Integer) yaml.getValue(c + ".minPoints");
        tp = new Location(mainWorld, (double) yaml.getValue(c + ".tp.x"), (double) yaml.getValue(c + ".tp.y"), (double) yaml.getValue(c + ".tp.z"));
        for (String uuid : (List<String>) yaml.getValue(c + ".participants")) {
            participants.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
        }
        region = (ProtectedPolygonalRegion) getWorldGuard().getRegionManager(mainWorld).getRegion("evento_" + c);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStatus(String status) {
        switch (status) {
            case "ready":
                this.setStatus(Status.READY);
                break;
            case "off":
                this.setStatus(Status.OFF);
                break;
            case "on":
                this.setStatus(Status.ON);
                break;
        }
    }

    public Location getTp() {
        return tp;
    }

    public void setTp(Location tp) {
        this.tp = tp;
    }

    public List<OfflinePlayer> getParticipants() {
        return participants;
    }

    public void addParticipant(OfflinePlayer player) {
        participants.add(player);
    }

    public void removeParticipant(OfflinePlayer player) {
        participants.remove(player);
    }

    public void setParticipants(List<OfflinePlayer> participants) {
        this.participants = participants;
    }

    public void setNewRegion(List<BlockVector2D> points) {
        ProtectedPolygonalRegion newRegion = new ProtectedPolygonalRegion("evento_" + country.getName(), points, -8000, 8000);
        newRegion.setFlags(this.region.getFlags());
        DefaultDomain defaultDomain = new DefaultDomain();
        for (OfflinePlayer player : this.participants) {
            defaultDomain.addPlayer(player.getUniqueId());
        }
        newRegion.setMembers(defaultDomain);
        newRegion.setPriority(2);
        this.region = newRegion;
    }

    public void reset() {
        this.setName("notSet");
        this.setDate("notSet");
        this.setImage("notSet");
        this.setStatus(Status.OFF);
        this.setTp(new Location(mainWorld, 0, 0, 0));
        this.setMinPoints(0);
        this.setParticipants(new ArrayList<>());
    }

    public void save() {
        String c = country.getName();
        yaml.setValue(c + ".name", this.name);
        yaml.setValue(c + ".date", this.date);
        yaml.setValue(c + ".image", this.image);
        yaml.setValue(c + ".status", this.status.toString());
        yaml.setValue(c + ".minPoints", this.minPoints);
        List<String> uuids = new ArrayList<>();
        for (OfflinePlayer player : this.participants) {
            uuids.add(player.getUniqueId().toString());
        }
        yaml.setValue(c + ".participants", uuids);
        yaml.setValue(c + ".tp.x", this.tp.getX());
        yaml.setValue(c + ".tp.y", this.tp.getY());
        yaml.setValue(c + ".tp.z", this.tp.getZ());

        yaml.write();

        DefaultDomain defaultDomain = new DefaultDomain();
        if (this.status == Status.ON) {
            for (OfflinePlayer player : participants) {
                defaultDomain.addPlayer(player.getUniqueId());
            }
        }
        region.setMembers(defaultDomain);

        RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
        if (regionManager.getRegion("evento_" + country.getName()) != region) {
            regionManager.addRegion(region);
        }
    }

    public void start() {
        this.setStatus(Status.ON);
        this.save();
        List<String> names = new ArrayList<>();
        for (OfflinePlayer player : participants) {
            ServerPlayer serverPlayer = new ServerPlayer(player);
            if (serverPlayer.newGetPrimaryGroup() == ServerPlayer.PrimaryGroup.DEFAULT && !(serverPlayer.getSecondaryGroups().contains("evento"))) {
                serverPlayer.addSecondaryGroup("evento");
            }
            names.add(serverPlayer.getName());
            if (!player.isOnline()) {
                // TODO FIX ¡
                serverPlayer.sendNotification(eventsPrefix + "¡El evento §a**" + this.name +  " (" + StringUtils.capitalize(country.getName().replace("peru", "perú")) + ")**§f acaba de empezar! Usa §a`/event`§f para ir.");
            }
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(">+-----------+[-< §5EVENTO§f >-]+-----------+<");
            player.sendMessage("§d¡El evento de " + StringUtils.capitalize(country.getName().replace("peru", "perú")) + " acaba de iniciar!");
            player.sendMessage("§aNombre: §f" + this.getName());
            player.sendMessage("§aFecha: §f" + this.getDate());
            player.sendMessage("§aPuntos mínimos: §f" + this.getMinPoints());
            player.sendMessage("§aCoordenadas: §f" + this.getTp().getBlockX() + ", " + this.getTp().getBlockY() + ", " + this.getTp().getBlockZ());
            player.sendMessage("§7¡Usa §f/event§7 para ir" + (participants.contains(player) ? "!" : " y §f/event join§7 para unirte!"));
            player.sendMessage(">+-----------+[-< ====== >-]+-----------+<");
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Usa `/event` en el servidor para ir al evento y `/event join` para unirte.");
        embed.setColor(new Color(0,255,43));
        embed.setThumbnail(country.getIcon());
        embed.setTitle("¡El evento \"" + name + "\" de " + StringUtils.capitalize(country.getName().replace("peru", "perú")) + " acaba de empezar!");
        embed.addField(":calendar: Fecha:", this.date, false);
        embed.addField(":round_pushpin: Coordenadas:", tp.getBlockX() + " " + tp.getBlockY() + " " + tp.getBlockZ(), false);
        embed.addField(":chart_with_upwards_trend: Puntos mínimos para participar:", minPoints.toString(), false);
        if (names.size() > 0) {
            embed.addField(":busts_in_silhouette: Participantes actuales:", String.join(", ", names), false);
        }
        if (image.equals("notSet")) {
            try {
                embed.setImage("attachment://map.png");
                gateway.sendFile(new URL(getMapURL(new Pair<>(region.getPoints(), "7434eb"))).openStream(), "map.png").setEmbeds(embed.build()).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            embed.setImage(image);
            gateway.sendMessageEmbeds(embed.build()).queue();
        }
    }

    public void stop() {

        List<String> names = new ArrayList<>();
        for (OfflinePlayer player : participants) {
            ServerPlayer serverPlayer = new ServerPlayer(player);
            names.add(serverPlayer.getName());
            PlayerData playerData = new PlayerData(player);
            if (serverPlayer.getSecondaryGroups().contains("evento")) {
                if (playerData.getList("events") != null && playerData.getList("events").size() == 1) {
                    serverPlayer.removeSecondaryGroup("evento");
                }
            }
            playerData.removeFromList("events", this.country.getName());
            playerData.save();
            if (!player.isOnline()) {
                serverPlayer.sendNotification(eventsPrefix + "El evento §a**" + this.name +  "**§f de §a" + StringUtils.capitalize(country.getName().replace("peru", "perú")) + "§f acaba de terminar. ¡Gracias por participar!");
            }
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(">+-----------+[-< §5EVENTO§f >-]+-----------+<");
            player.sendMessage("¡El evento §a" + name + "§f de §a" + StringUtils.capitalize(country.getName().replace("peru", "perú")) + "§f acaba de terminar!");
            player.sendMessage(">+-----------+[-< ====== >-]+-----------+<");
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255,0,0));
        embed.setThumbnail(country.getIcon());
        embed.setTitle("¡El evento \"" + name + "\" de " + StringUtils.capitalize(country.getName().replace("peru", "perú")) + " acaba de terminar!");
        if (names.size() > 0) {
            embed.addField(":busts_in_silhouette: Participantes finales:", String.join(", ", names), false);
        } else {
            embed.addField(":busts_in_silhouette: Participantes finales:", "No hay.", false);
        }
        gateway.sendMessageEmbeds(embed.build()).queue();
        embed.setTitle("Evento \"" + name + "\"");
        country.getLogs().sendMessageEmbeds(embed.build()).queue();

        this.reset();
        this.save();
    }

    public void prepared() {
        this.setStatus(Status.READY);
        this.save();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(">+-----------+[-< §5EVENTO§f >-]+-----------+<");
            player.sendMessage("§d¡Hay un nuevo evento preparado en " + StringUtils.capitalize(country.getName().replace("peru", "perú")) + "!");
            player.sendMessage("§aNombre: §f" + this.getName());
            player.sendMessage("§aFecha: §f" + this.getDate());
            player.sendMessage("§aPuntos mínimos: §f" + this.getMinPoints());
            player.sendMessage("§aCoordenadas: §f" + this.getTp().getBlockX() + ", " + this.getTp().getBlockY() + ", " + this.getTp().getBlockZ());
            player.sendMessage("§7¡Usa §f/event§7 para ir y §f/event join§7 para unirte!");
            player.sendMessage(">+-----------+[-< ====== >-]+-----------+<");
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Usa `/event` en el servidor para ir al evento y `/event join` para unirte.");
        embed.setColor(new Color(0,255,43));
        embed.setThumbnail(country.getIcon());
        embed.setTitle("¡Nuevo evento \"" + name + "\" preparado en " + StringUtils.capitalize(country.getName().replace("peru", "perú")) + "!");
        embed.addField(":calendar: Fecha:", this.date, false);
        embed.addField(":round_pushpin: Coordenadas:", tp.getBlockX() + " " + tp.getBlockY() + " " + tp.getBlockZ(), false);
        embed.addField(":chart_with_upwards_trend: Puntos mínimos para participar:", minPoints.toString(), false);
        if (image.equals("notSet")) {
            try {
                embed.setImage("attachment://map.png");
                gateway.sendFile(new URL(getMapURL(new Pair<>(region.getPoints(), "7434eb"))).openStream(), "map.png").setEmbeds(embed.build()).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            embed.setImage(image);
            gateway.sendMessageEmbeds(embed.build()).queue();
        }
    }
}
