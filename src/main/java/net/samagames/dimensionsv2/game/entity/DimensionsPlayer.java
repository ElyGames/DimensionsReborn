package net.samagames.dimensionsv2.game.entity;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamePlayer;
import net.samagames.api.shops.IPlayerShop;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tigger_San on 21/04/2017.
 */
public class DimensionsPlayer extends GamePlayer{

    private Map<PowerUp,Integer> powerUps;
    private ObjectiveSign objectiveSign;
    private int kills;

    public DimensionsPlayer(Player player) {
        super(player);

        objectiveSign = new ObjectiveSign("dimensions","§2§bDimensions");
        objectiveSign.addReceiver(player);
        this.kills = 0;


        powerUps = new HashMap<>();
        IPlayerShop shop = SamaGamesAPI.get().getShopsManager().getPlayer(this.uuid);


        for(PowerUp pu : PowerUp.values()){
            powerUps.put(pu,pu.getPowerUpLevelForPlayer(shop));
        }

    }

    public int getValueForPowerUP(PowerUp pu){
        return powerUps.get(pu);
    }

    public int getKills() {
        return kills;
    }


    public void addKill(){
        kills++;
    }

    public void updateScoreboard(){
        //TODO
    }
}
