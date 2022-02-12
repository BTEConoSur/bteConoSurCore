package pizzaaxx.bteconosur.points;

import org.bukkit.OfflinePlayer;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.player.data.PlayerData;

import static pizzaaxx.bteconosur.misc.Misc.getCountryPrefix;
import static pizzaaxx.bteconosur.misc.Misc.getLogsChannel;

public class PlayerPoints {

    public static String pointsPrefix = "§f[§9PUNTOS§f] §7>>§r ";

    private OfflinePlayer player;
    private String country = null;
    private Integer amount = null;


    // CONSTRUCTOR
    public PlayerPoints(OfflinePlayer player, String country){
        this.player = player;
        this.country = country;
        if (new PlayerData(player).getData(getCountryPrefix(country) + "_points") != null) {
            this.amount = (Integer) new PlayerData(player).getData(getCountryPrefix(country) + "_points");
        } else {
            this.amount = 0;
        }
    }

    // --- GETTER ---

    public Integer getAmount() {
        return amount;
    }

    public String getCountry() {
        return country;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    // --- SETTERS ---

    public void setAmount(Integer amount) {
        this.amount = amount;
        this.save();
    }

    // ADDER

    public void addAmount(Integer amount) {
        this.amount = this.amount + amount;
        this.save();
    }

    // REMOVER

    public void removeAmount(Integer amount) {
        this.amount = this.amount - amount;
        this.save();
    }

    // SAVE

    private void save() {
        if (this.amount != null) {
            ServerPlayer s = new ServerPlayer(this.player);
            PlayerData playerData = new PlayerData(this.player);
            if ((Integer) playerData.getData(getCountryPrefix(this.country) + "_points") > this.amount) {
                getLogsChannel(this.country).sendMessage(":chart_with_upwards_trend: Se han añadido `" + ((Integer) playerData.getData(getCountryPrefix(this.country) + "_points") - this.amount) + "` puntos a **" + s.getName() + "**. Total: `" + this.amount + "`.").queue();
            } else {
                getLogsChannel(this.country).sendMessage(":chart_with_upwards_trend: Se han quitado `" + (this.amount - (Integer) playerData.getData(getCountryPrefix(this.country) + "_points")) + "` puntos de **" + s.getName() + "**. Total: `" + this.amount + "`.").queue();
            }
            playerData.setData(getCountryPrefix(this.country) + "_points", this.amount);
            playerData.save();
        }
    }
}
