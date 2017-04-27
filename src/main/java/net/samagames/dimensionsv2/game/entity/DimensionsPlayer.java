package net.samagames.dimensionsv2.game.entity;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamePlayer;
import net.samagames.api.shops.IPlayerShop;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.dimension.Dimension;
import net.samagames.dimensionsv2.game.utils.TimeUtil;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tigger_San on 21/04/2017.
 */
public class DimensionsPlayer extends GamePlayer{

    private Map<PowerUp,Integer> powerUps;
    private ObjectiveSign objectiveSign;
    private int kills;
    private UUID lastDamager;
    private Dimension dimension;
    private long lastSwap;

    public DimensionsPlayer(Player player) {
        super(player);

        lastSwap = -1;

        dimension = Dimension.OVERWORLD;
        objectiveSign = new ObjectiveSign("dimensions","§a§lDimensions");
        objectiveSign.addReceiver(this.getOfflinePlayer());
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

    public ObjectiveSign getObjectiveSign() {
        return objectiveSign;
    }

    public void updateScoreboard(){
        DimensionsGame game = Dimensions.getInstance().getGame();
        int timer = game.getGameTime();
        String time = TimeUtil.timeToString(timer);

        objectiveSign.setLine(-8," ");

        if(game.getGameStep()== GameStep.DEATHMATCH_PLANNED ||game.getGameStep()== GameStep.DEATHMATCH  ){
            if(game.getDeathMatchIn()>0){
                String dmIn = TimeUtil.timeToString(game.getDeathMatchIn());
                objectiveSign.setLine(-7,"§eD.Match§7 : §f" + dmIn);
            }
            else{
                objectiveSign.setLine(-7,"§aFight final !");
            }
        }
        else if(game.getGameStep()== GameStep.PVP ||game.getGameStep()== GameStep.IN_GAME  ){
            if(game.getPvpIn()>0){
                String pvpIn = TimeUtil.timeToString(game.getPvpIn());
                objectiveSign.setLine(-7,"§ePVP§7 : §f" + pvpIn);
            }
            else{
                objectiveSign.setLine(-7,"§aLet's fight !");
            }
        }

        objectiveSign.setLine(-6, "  " );
        objectiveSign.setLine(-5, "§7Joueurs : §f" + game.getInGamePlayers().size());
        objectiveSign.setLine(-4, "   " );
        objectiveSign.setLine(-3, "§7Kill(s) : §f" + kills);
        objectiveSign.setLine(-2, "    " );
        objectiveSign.setLine(-1, "§f"  + time);
        objectiveSign.updateLines();
    }

    public UUID getLastDamager() {
        return lastDamager;
    }


    public Dimension getDimension() {
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(game.getGameStep() == GameStep.DEATHMATCH){
            return Dimension.OVERWORLD;
        }
        return dimension;
    }

    public int getNextSwapDelay(){
        if(lastSwap == -1){
            return 0;
        }
        else{
            long nextSwap = lastSwap + (powerUps.get(PowerUp.TP_TIME)*1000);
            return new Long((nextSwap - System.currentTimeMillis())/1000).intValue();
        }

    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }


    public void setLastSwap(long lastSwap) {
        this.lastSwap = lastSwap;
    }

    public void setLastDamager(UUID lastDamager) {
        this.lastDamager = lastDamager;
    }
}
