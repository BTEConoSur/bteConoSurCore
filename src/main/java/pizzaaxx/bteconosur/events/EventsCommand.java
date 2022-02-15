package pizzaaxx.bteconosur.events;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.country.Country;

import java.util.*;
import java.util.List;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.country.Country.countryAbbreviations;
import static pizzaaxx.bteconosur.worldedit.Methods.getSelection;
import static pizzaaxx.bteconosur.worldedit.Methods.polyRegion;

public class EventsCommand implements CommandExecutor {

    public static String eventsPrefix = "[§5EVENTO§f] §7>> §f";
    public static Set<CommandSender> startConfirm = new HashSet<>();
    public static Set<CommandSender> stopConfirm = new HashSet<>();
    public static Set<CommandSender> readyConfirm = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender p, org.bukkit.command.Command command, String label, final String[] args) {
        if (command.getName().equals("event")) {
            if (args.length > 0) {

            } else {
                // TODO GUI
            }
        }

        if (command.getName().equals("manageevent")) {
            if (args.length > 0) {
                if (countryAbbreviations.contains(args[0])) {
                    Country pais = new Country(args[0]);
                    final String country = pais.getCountry();
                    if (args.length > 1) {
                        Event event = new Event(pais);
                        if (args[1].equals("name")) {
                            if (args.length > 2) {
                                String name = String.join(" ", Arrays.asList(args).subList(2, args.length));
                                if (name.matches("[ A-Za-z0-9/]{1,32}")) {
                                    event.setName(name);
                                    event.save();
                                    p.sendMessage(eventsPrefix + "Has establecido el nombre del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en §a" + name + "§f.");

                                } else {
                                    p.sendMessage(eventsPrefix + "Introduce un nombre válido.");
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "El nombre del evento del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f es §a" + event.getName() + "§f.");
                            }
                        } else if (args[1].equals("date")) {
                            if (args.length > 2) {
                                String date = String.join(" ", Arrays.asList(args).subList(2, args.length));
                                if (date.matches("[ -A-Za-z0-9/]{1,32}")) {
                                    event.setDate(date);
                                    event.save();
                                    p.sendMessage(eventsPrefix + "Has establecido la fecha del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en §a" + date + "§f.");

                                } else {
                                    p.sendMessage(eventsPrefix + "Introduce un texto válido.");
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "La fecha del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f es §a" + event.getDate() + "§f.");
                            }
                        } else if (args[1].equals("status")) {
                            if (args.length > 2) {
                                if (args[2].equals("stop") || args[2].equals("off") || args[2].equals("terminar")) {
                                    if (event.getStatus() != Event.Status.OFF) {
                                        if (stopConfirm.contains(p)) {
                                            stopConfirm.remove(p);
                                            event.stop();
                                            p.sendMessage("Has terminado el evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f.");
                                        } else if (event.getStatus() == Event.Status.READY) {
                                            stopConfirm.add(p);
                                            p.sendMessage(eventsPrefix + "§cToda la configuración del evento se perderá.§f Usa el comando de nuevo para confirmar.");
                                        } else {
                                            stopConfirm.add(p);
                                            p.sendMessage(eventsPrefix + "§cNo puedes deshacer esta acción.§f Usa el comando de nuevo para confirmar.");
                                        }
                                    }
                                } else if (args[2].equals("ready") || args[2].equals("listo") || args[2].equals("preparado")) {
                                    if (event.getStatus() == Event.Status.OFF) {
                                        if (readyConfirm.contains(p)) {
                                            readyConfirm.remove(p);

                                            event.prepared();

                                            p.sendMessage("Has marcado el evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f como preparado.");

                                        } else {
                                            readyConfirm.add(p);
                                            p.sendMessage(eventsPrefix + "¿Estás seguro de que quieres marcar el evento como terminado? A continuación se hay una previsualización del evento. Usa el comando de nuevo para confirmar.");
                                            p.sendMessage(">+-----------+[-< §5EVENTO§f >-]+-----------+<");
                                            p.sendMessage("§aNombre: §f" + event.getName());
                                            p.sendMessage("§aFecha: §f" + event.getDate());
                                            p.sendMessage("§aPuntos mínimos: §f" + event.getMinPoints());
                                            p.sendMessage("§aImagen: §f" + event.getImage());
                                            p.sendMessage("§aCoordenadas: §f" + event.getTp().getBlockX() + ", " + event.getTp().getBlockY() + ", " + event.getTp().getBlockZ());
                                            p.sendMessage(">+-----------+[-< ====== >-]+-----------+<");
                                        }
                                    } else {
                                        p.sendMessage(eventsPrefix + "Sólo puedes hacer esto cuando el proyecto está apagado.");
                                    }
                                } else if (args[2].equals("start") || args[2].equals("on") || args[2].equals("empezar")) {
                                    if (event.getStatus() == Event.Status.READY) {
                                        if (startConfirm.contains(p)) {
                                            startConfirm.remove(p);

                                            event.start();

                                            p.sendMessage("Has empezado el evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f.");

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
                                        event.setTp(player.getLocation());
                                        p.sendMessage(eventsPrefix + "Has establecido el teletransporte al evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en tu posición actual.");

                                    } else {
                                        p.sendMessage(eventsPrefix + "No puedes usar este comando desde la consola.");
                                    }
                                } else if (args.length > 4 && args[2].matches("^-?[0-9]{1,16}(\\.[0-9]{1,16})?$") && args[3].matches("^-?[0-9]{1,16}(\\.[0-9]{1,16})?$") && args[4].matches("^-?[0-9]{1,16}(\\.[0-9]{1,16})?$") ) {
                                    double x = Double.parseDouble(args[2]);
                                    double y = Double.parseDouble(args[3]);
                                    double z = Double.parseDouble(args[4]);

                                    event.setTp(new Location(mainWorld, x, y, z));
                                    event.save();
                                    p.sendMessage(eventsPrefix + "Has establecido el teletransporte al evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en §a" + x + ", " + y + ", " + z + "§f.");
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "El teletransporte del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f está en §a" + event.getTp().getBlockX() + ", " +  event.getTp().getBlockY() + ", " + event.getTp().getBlockZ() + "§f.");
                            }

                        } else if (args[1].equals("redefine")) {
                            if (event.getStatus() != Event.Status.ON) {
                                if (p instanceof Player) {
                                    Player player = (Player) p;

                                    List<BlockVector2D> points = new ArrayList<>();

                                    try {
                                        points = polyRegion(getSelection(player)).getPoints();
                                    } catch (IllegalArgumentException e) {
                                        player.sendMessage(eventsPrefix + "Selecciona un área cúbica o poligonal.");
                                    } catch (IncompleteRegionException e) {
                                        player.sendMessage(eventsPrefix + "Selecciona un área primero.");
                                    }

                                    event.setNewRegion(points);
                                    event.save();

                                    p.sendMessage(eventsPrefix + "Has redefinido la región del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f.");

                                } else {
                                    p.sendMessage(eventsPrefix + "No se puede ejecutar este comando desde la consola.");
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "No puedes hacer esto mientras el evento está activo.");
                            }

                        } else if (args[1].equals("minpoints")) {
                            if (args.length > 2) {
                                if (event.getStatus() == Event.Status.ON) {
                                    p.sendMessage(eventsPrefix + "No puedes cambiar esto mientras un evento está activo.");
                                    return true;
                                }
                                if (args[2].matches("[0-9]{1,10}") && Integer.parseInt(args[2]) >= 0) {
                                    event.setMinPoints(Integer.parseInt(args[2]));
                                    event.save();
                                    p.sendMessage(eventsPrefix + "Has establecido el mínimo de puntos para el evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en §a" + args[2] + "§f.");
                                } else {
                                    p.sendMessage(eventsPrefix + "Introduce un valor válido.");
                                }
                            } else {
                                p.sendMessage(eventsPrefix + "El mínimo de puntos para el evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f es de §a" + event.getMinPoints() + "§f.");
                            }
                        } else if (args[1].equals("image")) {
                            if (args.length > 2) {
                                if (args[2].equals("delete")) {
                                    event.setImage("notSet");
                                    event.save();
                                    p.sendMessage(eventsPrefix + "Has eliminado la imagen del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f.");
                                } else {
                                    event.setImage(args[2]);
                                    event.save();
                                    p.sendMessage(eventsPrefix + "Has establecido la imagen del evento de §a" + country.replace("peru", "perú").toUpperCase() + "§f en\n§b" + args[2]);

                                }
                            } else {
                                p.sendMessage(eventsPrefix + "Introduce un enlace a una imagen.");
                            }
                        }
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
