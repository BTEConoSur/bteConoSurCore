package pizzaaxx.bteconosur.country;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.serverPlayer.PointsManager;
import pizzaaxx.bteconosur.chats.Chat;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;
import pizzaaxx.bteconosur.yaml.Configuration;
import pizzaaxx.bteconosur.yaml.YamlManager;
;
import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.*;
import static pizzaaxx.bteconosur.Config.*;
import static pizzaaxx.bteconosur.Config.logsPe;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class Country {
    private String name;

    public static List<String> countryNames = Arrays.asList("argentina", "bolivia", "chile", "paraguay", "peru", "uruguay");
    public static List<String> countryAbbreviations = Arrays.asList("ar", "bo", "cl", "py", "pe", "uy", "gl");
    public static List<String> countryRegionNames = Arrays.asList("argentina", "bolivia", "chile_idp", "chile_cont", "paraguay", "peru", "uruguay");

    // CONSTRUCTOR
    public Country(String country) {
        if (country.matches("[a-z]{2}")) {
            this.name = null;
            if (country.equals("ar")) {
                this.name = "argentina";
            }
            if (country.equals("bo")) {
                this.name = "bolivia";
            }
            if (country.equals("cl")) {
                this.name = "chile";
            }
            if (country.equals("py")) {
                this.name = "paraguay";
            }
            if (country.equals("pe")) {
                this.name = "peru";
            }
            if (country.equals("uy")) {
                this.name = "uruguay";
            }
            if (country.equals("gl")) {
                this.name = "global";
            }
        } else {
            this.name = null;
            if (country.equals("argentina") || country.equals("bolivia") || country.equals("chile") || country.equals("paraguay") || country.equals("peru") || country.equals("uruguay") || country.equals("global")) {
                this.name = country;
            }
        }
    }

    public Country(Guild guild) {
        String id = guild.getId();
        switch (id) {
            case "692607124210974760":
                this.name = "argentina";
                break;
            case "762309020517531698":
                this.name = "bolivia";
                break;
            case "807694451530924073":
                // TODO CHANGE THIS BEFORE DEPLOY
                this.name = "chile";
                break;
            case "695044514066464828":
                this.name = "peru";
                break;
            case "696154248593014815":
                this.name = "uruguay";
                break;
        }
    }

    public Country(Location loc) {
        RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
        Set<ProtectedRegion> regions = regionManager.getApplicableRegions(loc).getRegions();
        if (regions.contains(regionManager.getRegion("argentina"))) {
            this.name = "argentina";
        } else if (regions.contains(regionManager.getRegion("bolivia"))) {
            this.name = "bolivia";
        } else if (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp"))) {
            this.name = "chile";
        } else if (regions.contains(regionManager.getRegion("paraguay"))) {
            this.name = "paraguay";
        } else if (regions.contains(regionManager.getRegion("peru"))) {
            this.name = "peru";
        } else if (regions.contains(regionManager.getRegion("uruguay"))) {
            this.name = "uruguay";
        } else {
            this.name = "global";
        }
    }

    public Country(BlockVector2D vector) {
        Location location = new Location(mainWorld, vector.getX(), 100.0, vector.getZ());
        RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
        Set<ProtectedRegion> regions = regionManager.getApplicableRegions(location).getRegions();
        if (regions.contains(regionManager.getRegion("argentina"))) {
            this.name = "argentina";
        } else if (regions.contains(regionManager.getRegion("bolivia"))) {
            this.name = "bolivia";
        } else if (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp"))) {
            this.name = "chile";
        } else if (regions.contains(regionManager.getRegion("paraguay"))) {
            this.name = "paraguay";
        } else if (regions.contains(regionManager.getRegion("peru"))) {
            this.name = "peru";
        } else if (regions.contains(regionManager.getRegion("uruguay"))) {
            this.name = "uruguay";
        } else {
            this.name = "global";
        }
    }

    // GETTERS

    public String getIcon() {
        return (String) new YamlManager(pluginFolder, "discord/countryIcons.yml").getValue(name);
    }

    public String getName() {
        return this.name;
    }

    public Chat getChat() {
        return new Chat(this.name);
    }

    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<ProtectedRegion> regions = regionManager.getApplicableRegions(player.getLocation()).getRegions();
            if (this.name.equals("chile") && (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp")))) {
                players.add(player);
            } else if (regions.contains(regionManager.getRegion(this.name))) {
                players.add(player);
            }
        }
        return players;
    }

    public TextChannel getLogs() {
        if (this.name.equals("argentina")) {
            return logsAr;
        }
        if (this.name.equals("bolivia")) {
            return logsBo;
        }
        if (this.name.equals("chile")) {
            return logsCl;
        }
        if (this.name.equals("paraguay")) {
            return logsPy;
        }
        if (this.name.equals("peru")) {
            return logsPe;
        }
        if (this.name.equals("uruguay")) {
            return logsUy;
        }
        return null;
    }

    public TextChannel getRequests() {
        if (this.name.equals("argentina")) {
            return requestsAr;
        }
        if (this.name.equals("bolivia")) {
            return requestsBo;
        }
        if (this.name.equals("chile")) {
            return requestsCl;
        }
        if (this.name.equals("paraguay")) {
            return requestsPy;
        }
        if (this.name.equals("peru")) {
            return requestsPe;
        }
        if (this.name.equals("uruguay")) {
            return requestsUy;
        }
        return null;
    }

    public String getAbbreviation() {
        if (this.name.equals("argentina")) {
            return "ar";
        }
        if (this.name.equals("bolivia")) {
            return "bo";
        }
        if (this.name.equals("chile")) {
            return "cl";
        }
        if (this.name.equals("paraguay")) {
            return "py";
        }
        if (this.name.equals("peru")) {
            return "pe";
        }
        if (this.name.equals("uruguay")) {
            return "uy";
        }
        return null;
    }

    public List<PointsManager> getScoreboard() {
        List<PointsManager> scoreboard = new ArrayList<>();

        Configuration max = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "points/max");
        for (String uuid : max.getStringList(getAbbreviation() + "_max")) {
            scoreboard.add(new ServerPlayer(UUID.fromString(uuid)).getPointsManager());
        }

        return scoreboard;
    }

    public String getPrefix(Boolean abb) {
        if (abb) {
            if (this.name.equals("argentina")) {
                return "§b[§fA§eR§fG§b]§r";
            }
            if (this.name.equals("bolivia")) {
                return "§c[B§eO§2L]§r";
            }
            if (this.name.equals("chile")) {
                return "§9[C§fH§cI]§r";
            }
            if (this.name.equals("paraguay")) {
                return "§4[§fPGY§9]§r";
            }
            if (this.name.equals("peru")) {
                return "§4[PER§4]§r";
            }
            if (this.name.equals("uruguay")) {
                return "§e[§fU§9R§fY§9]§r";
            }
            if (this.name.equals("global")) {
                return "§7[INT]§r";
            }
        } else {
            if (this.name.equals("argentina")) {
                return "§b[AR§fGE§eN§fTI§bNA]§r";
            }
            if (this.name.equals("bolivia")) {
                return "§4[BO§eLIV§2IA]§r";
            }
            if (this.name.equals("chile")) {
                return "§9[C§fHIL§cE]§r";
            }
            if (this.name.equals("paraguay")) {
                return "§4[P§fAR§7AG§fUA§1Y]§r";
            }
            if (this.name.equals("peru")) {
                return "§4[P§fER§4Ú]§r";
            }
            if (this.name.equals("uruguay")) {
                return "§f[§eU§fR§9U§fG§9U§fA§9Y§f]§r";
            }
            if (this.name.equals("global")) {
                return "§7[INTERNACIONAL]§r";
            }
        }
        return null;
    }

    public Guild getGuild() {
        return guilds.get(this);
    }
}
