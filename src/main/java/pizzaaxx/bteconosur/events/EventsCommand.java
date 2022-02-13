package pizzaaxx.bteconosur.events;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.helper.Pair;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.io.IOException;
import java.lang.management.PlatformLoggingMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.Config.gateway;
import static pizzaaxx.bteconosur.country.Country.countryAbbreviations;
import static pizzaaxx.bteconosur.misc.Misc.getMapURL;
import static pizzaaxx.bteconosur.ranks.Main.primaryGroupsList;
import static pizzaaxx.bteconosur.worldedit.Methods.getSelection;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class EventsCommand implements CommandExecutor {

    public static String eventsPrefix = "[§5EVENTO§f] §7>> §f";
    public static Set<CommandSender> startConfirm = new HashSet<>();
    public static Set<CommandSender> stopConfirm = new HashSet<>();
    public static Set<CommandSender> readyConfirm = new HashSet<>();


    @Override
    public boolean onCommand(CommandSender p, org.bukkit.command.Command command, String label, String[] args) {
        if (command.getName().equals("event")) {
            if (args.length > 0) {

            } else {
                // TODO GUI
            }
        }

        if (command.getName().equals("manageevent")) {
            if (args.length > 0) {
                if (countryAbbreviations.contains(args[0])) {
                    String country = new Country(args[0]).getCountry();
                    if (args.length > 1) {
                        YamlManager yaml = new YamlManager(pluginFolder, "events.yml");
                        if (args[1].equals("name")) {
                            if (args.length > 2) {
                                String name = String.join(" ", Arrays.asList(args).subList(2, args.length));
                                if (name.matches("[ A-Za-z0-9/]{1,32}")) {
                                    yaml.setValue(country + ".name", name);
                                    p.sendMessage(eventsPrefix + "Has establecido el nombre del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en §a" + name + "§f.");

                                } else {
                                    p.sendMessage(eventsPrefix + "Introduce un nombre válido.");
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "El nombre del evento del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f es §a" + yaml.getValue(country + ".name") + "§f.");
                            }
                        } else if (args[1].equals("date")) {
                            if (args.length > 2) {
                                String date = String.join(" ", Arrays.asList(args).subList(2, args.length));
                                if (date.matches("[ -A-Za-z0-9\\/]{1,32}")) {
                                    yaml.setValue(country + ".date", date);
                                    p.sendMessage(eventsPrefix + "Has establecido la fecha del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en §a" + date + "§f.");

                                } else {
                                    p.sendMessage(eventsPrefix + "Introduce un texto válido.");
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "La fecha del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f es §a" + yaml.getValue(country + ".date") + "§f.");
                            }
                        } else if (args[1].equals("status")) {
                            if (args.length > 2) {
                                if (args[2].equals("stop")) {
                                    if (!yaml.getValue(country + ".status").equals("stop")) {
                                        if (stopConfirm.contains(p)) {
                                            stopConfirm.remove(p);
                                            yaml.setValue(country + ".status", "stop");
                                            for (String uuid : (List<String>) yaml.getValue(country + ".participants")) {
                                                new ServerPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).sendNotification(eventsPrefix + "El evento §a**" + yaml.getValue(country + ".name") +  "** (" + country.replace("peru", "perú").toUpperCase() + ")§f acaba de terminar. ¡Gracias por participar!");
                                            }
                                            yaml.setValue(country + ".participants", new ArrayList<>());
                                            yaml.setValue(country + ".image", "none");
                                            yaml.setValue(country + ".minPoints", 0);
                                            yaml.setValue(country + ".name", "none");
                                            yaml.setValue(country + ".date", "none");
                                            yaml.setValue(country + ".tp.x", 0);
                                            yaml.setValue(country + ".tp.y", 0);
                                            yaml.setValue(country + ".tp.z", 0);

                                            RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
                                            regionManager.getRegion("evento_" + country).setMembers(new DefaultDomain());

                                            p.sendMessage(eventsPrefix + "Has terminado el evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f.");
                                        } else {
                                            stopConfirm.add(p);
                                            p.sendMessage(eventsPrefix + "§cToda la configuración del evento se perderá.§f Usa el comando de nuevo para confirmar.");
                                        }
                                    }
                                } else if (args[2].equals("ready")) {
                                    if (yaml.getValue(country + ".status").equals("stop")) {
                                        if (readyConfirm.contains(p)) {
                                            readyConfirm.remove(p);

                                            yaml.setValue(country + ".status", "ready");

                                            p.sendMessage(eventsPrefix + "Has marcado el evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f como terminado.");

                                        } else {
                                            readyConfirm.add(p);
                                            p.sendMessage(eventsPrefix + "¿Estás seguro de que quieres marcar el evento como terminado? A continuación se hay una previsualización del evento. Usa el comando de nuevo para confirmar.");
                                            p.sendMessage(">+-----------+[-< §5EVENTO >-]+-----------+<");
                                            p.sendMessage("§aNombre: " + yaml.getValue(country + ".name"));
                                            p.sendMessage("§aFecha: " + yaml.getValue(country + ".date"));
                                            p.sendMessage("§aPuntos mínimos: " + yaml.getValue(country + ".minPoints"));
                                            p.sendMessage("§aImagen: " + yaml.getValue(country + ".image"));
                                            p.sendMessage("§aCoordenadas: " + yaml.getValue(country + ".tp.x") + ", " + yaml.getValue(country + ".tp.y") + ", " + yaml.getValue(country + ".tp.z"));
                                            p.sendMessage(">+-----------+[-< ====== >-]+-----------+<");
                                        }
                                    } else {
                                        p.sendMessage(eventsPrefix + "Sólo puedes hacer esto cuando el proyecto está desactivado.");
                                    }
                                } else if (args[2].equals("start")) {
                                    if (yaml.getValue(country + ".status").equals("ready")) {
                                        if (startConfirm.contains(p)) {
                                            startConfirm.remove(p);
                                            DefaultDomain defaultDomain = new DefaultDomain();
                                            List<String> names =  new ArrayList<>();
                                            for (String uuid : (List<String>) yaml.getValue(country + ".participants")) {
                                                UUID id = UUID.fromString(uuid);
                                                OfflinePlayer player = Bukkit.getOfflinePlayer(id);
                                                new ServerPlayer(Bukkit.getOfflinePlayer(id)).sendNotification(eventsPrefix + "¡El evento §a**" + yaml.getValue(country + ".name") +  "** (" + country.replace("peru", "perú").toUpperCase() + ")§f acaba de empezar!");
                                                defaultDomain.addPlayer(id);
                                                names.add(new ServerPlayer(player).getName().replace("_", "\\_"));
                                            }
                                            RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
                                            ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) regionManager.getRegion("evento_" + country);
                                            region.setMembers(defaultDomain);

                                            p.sendMessage(eventsPrefix + "Has empezado el evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f.");

                                            EmbedBuilder embed = new EmbedBuilder();
                                            embed.setColor(new Color(0,255,43));
                                            embed.setTitle("El evento \"" + yaml.getValue(country + ".name") + "\" (" + country.replace("peru", "perú").toUpperCase() + ") acaba de empezar.");
                                            embed.addField("Fecha:", (String) yaml.getValue(country + ".date"), false);
                                            embed.addField("Coordenadas:", yaml.getValue(country + ".tp.x") + " " + yaml.getValue(country + ".tp.y") + " " + yaml.getValue(country + ".tp.z"), false);
                                            embed.addField("Puntos mínimos para participar:", (String) yaml.getValue(country + ".minPoints"), false);
                                            if (names.size() > 0) {
                                                embed.addField("Participantes actuales:", String.join(", ", names), false);
                                            }
                                            if (yaml.getValue(country + ".image") == "none") {
                                                try {
                                                    embed.setImage("attachment://map.png");
                                                    gateway.sendFile(new URL(getMapURL(new Pair<>(region.getPoints(), "7434eb"))).openStream(), "map.png").setEmbeds(embed.build()).queue();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                embed.setImage((String) yaml.getValue(country + ".image"));
                                                gateway.sendMessageEmbeds(embed.build()).queue();
                                            }
                                        } else {
                                            startConfirm.add(p);
                                            p.sendMessage(eventsPrefix + "¿Estás seguro de que quieres iniciar el evento? Usa el comando de nuevo para confirmar.");
                                        }
                                    } else {
                                        p.sendMessage(eventsPrefix + "Solo puedes iniciar un evento cuando este está marcado como listo.");
                                    }
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "Introduce una acción.");
                            }
                        } else if (args[1].equals("tp")) {
                            if (args.length > 2) {
                                if (args[2].equals("here")) {
                                    if (p instanceof Player) {
                                        Player player = (Player) p;
                                        yaml.setValue(country + ".tp.x", player.getLocation().getBlockX());
                                        yaml.setValue(country + ".tp.y", player.getLocation().getBlockY());
                                        yaml.setValue(country + ".tp.z", player.getLocation().getBlockZ());
                                        p.sendMessage(eventsPrefix + "Has establecido el teletransporte al evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en tu posición actual.");

                                    } else {
                                        p.sendMessage(eventsPrefix + "No puedes usar este comando desde la consola.");
                                    }
                                } else if (args.length > 4 && args[2].matches("^-?[0-9]{1,16}(\\.[0-9]{1,16})?$") && args[3].matches("^-?[0-9]{1,16}(\\.[0-9]{1,16})?$") && args[4].matches("^-?[0-9]{1,16}(\\.[0-9]{1,16})?$") ) {
                                    double x = Double.parseDouble(args[2]);
                                    double y = Double.parseDouble(args[3]);
                                    double z = Double.parseDouble(args[4]);

                                    yaml.setValue(country + ".tp.x", x);
                                    yaml.setValue(country + ".tp.y", y);
                                    yaml.setValue(country + ".tp.z", z);
                                    p.sendMessage(eventsPrefix + "Has establecido el teletransporte al evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en §a" + x + ", " + y + ", " + z + "§f.");
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "El teletransporte del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f está en §a" + yaml.getValue(country + ".tp.x") + yaml.getValue(country + ".tp.y") + yaml.getValue(country + ".tp.z") + "§f.");
                            }

                        } else if (args[1].equals("redefine")) {
                            if (!yaml.getValue(country + ".status").equals("started")) {
                                if (p instanceof Player) {
                                    Player player = (Player) p;
                                    Region region = null;
                                    try {
                                        region = getSelection(player);
                                    } catch (IncompleteRegionException e) {
                                        p.sendMessage(eventsPrefix + "§cSelecciona un área primero.");
                                        return true;
                                    }

                                    // GET POINTS

                                    List<BlockVector2D> points = new ArrayList<>();

                                    if (region instanceof CuboidRegion) {
                                        CuboidRegion cuboidRegion = (CuboidRegion) region;
                                        Vector first = cuboidRegion.getPos1();
                                        Vector second = cuboidRegion.getPos2();

                                        points.add(new BlockVector2D(first.getX(), first.getZ()));
                                        points.add(new BlockVector2D(second.getX(), first.getZ()));
                                        points.add(new BlockVector2D(second.getX(), second.getZ()));
                                        points.add(new BlockVector2D(first.getX(), second.getZ()));
                                    } else if (region instanceof Polygonal2DRegion) {
                                        points = ((Polygonal2DRegion) region).getPoints();
                                    } else {
                                        p.sendMessage(eventsPrefix + "Debes seleccionar una region cúbica o poligonal.");
                                        return true;
                                    }

                                    if (points.size() < 3) {
                                        p.sendMessage(eventsPrefix + "Selecciona un área primero.");
                                        return true;
                                    }

                                    RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
                                    ProtectedRegion eventRegion = regionManager.getRegion("evento_" + country);
                                    ProtectedRegion newRegion = new ProtectedPolygonalRegion("evento_" + country, points, -8000, 8000);
                                    newRegion.setMembers(eventRegion.getMembers());
                                    newRegion.setFlags(eventRegion.getFlags());
                                    regionManager.addRegion(newRegion);

                                    p.sendMessage(eventsPrefix + "Has redefinido la región del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f.");

                                } else {
                                    p.sendMessage(eventsPrefix + "No se puede ejecutar este comando desde la consola.");
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "No puedes hacer esto mientras el evento está activo.");
                            }

                        } else if (args[1].equals("minpoints")) {
                            if (args.length > 2) {
                                if (yaml.getValue(country + ".status").equals("start")) {
                                    p.sendMessage(eventsPrefix + "No puedes cambiar esto mientras un evento está activo.");
                                    return true;
                                }
                                if (args[2].matches("[0-9]{1,10}") && Integer.parseInt(args[2]) >= 0) {
                                    yaml.setValue(country + ".minPoints", Integer.parseInt(args[2]));
                                    p.sendMessage(eventsPrefix + "Has establecido el mínimo de puntos para el evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en §a" + args[2] + "§f.");
                                } else {
                                    p.sendMessage(eventsPrefix + "Introduce un valor válido.");
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "El mínimo de puntos para el evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f es de §a" + yaml.getValue(country + ".minPoints") + "§f.");
                            }
                        } else if (args[1].equals("image")) {
                            if (args.length > 2) {
                                if (args[2].equals("delete")) {
                                    yaml.deleteValue(country + ".image");
                                    p.sendMessage(eventsPrefix + "Has eliminado la imagen del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f.");

                                } else {
                                    yaml.setValue(country + ".image", args[2]);
                                    p.sendMessage(eventsPrefix + "Has establecido la imagen del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en\n§b" + args[2]);

                                }
                            } else {
                                p.sendMessage(eventsPrefix + "Introduce un enlace a una imagen.");
                            }
                        }
                        yaml.write();
                    } else {
                        p.sendMessage(eventsPrefix + "Introduce una acción.");
                    }
                } else {
                    p.sendMessage(eventsPrefix + "Introduce un país válido.");
                }
            } else {
                p.sendMessage(eventsPrefix + "Introduce un país para manejar.");
            }
        }
        return true;
    }
}
