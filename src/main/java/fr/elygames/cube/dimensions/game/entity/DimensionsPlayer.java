package fr.elygames.cube.dimensions.game.entity;
import fr.elygames.cube.dimensions.game.entity.dimension.Dimension;

import fr.elygames.cube.dimensions.Dimensions;
import fr.elygames.cube.dimensions.game.DimensionsGame;
import fr.elygames.cube.dimensions.game.utils.TimeUtil;
import fr.elygames.cube.game.GamePlayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * This file is part of DimensionsV2.
 *
 * DimensionsV2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DimensionsV2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DimensionsV2.  If not, see <http://www.gnu.org/licenses/>.
 */
public class DimensionsPlayer extends GamePlayer {

    private Map<PowerUp,Integer> powerUps;
    //TODO : replace with ely sb
    //private ObjectiveSign objectiveSign;
    private int kills;
    private UUID lastDamager;
    private Dimension dimension;
    private long lastSwap;
    private long lastTarget;
    private UUID target;
    private TargetType targetType;
    private Location targetLoc;

    public DimensionsPlayer(Player player) {
        super(player);
        targetType = TargetType.PLAYER;
        lastSwap = -1;
        lastTarget= -1;
        target =null;
        dimension = Dimension.OVERWORLD;
        //objectiveSign = new ObjectiveSign("dimensions","§f☯ §5§lDimensions §f☯");
        //objectiveSign.addReceiver(this.getOfflinePlayer());
        this.kills = 0;

        powerUps = new HashMap<>();
        //IPlayerShop shop = SamaGamesAPI.get().getShopsManager().getPlayer(this.uuid);

        //Get shop values for player
        /*for(PowerUp pu : PowerUp.values()){
            powerUps.put(pu,pu.getPowerUpLevelForPlayer(shop));
        }*/
    }


    public Location getTargetLoc() {
        return targetLoc;
    }

    public void setTargetLoc(Location targetLoc) {
        this.targetLoc = targetLoc;
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
        /*DimensionsGame game = Dimensions.getInstance().getGame();
        int timer = game.getGameTime();
        String time = TimeUtil.timeToString(timer);

        objectiveSign.setLine(-8," ");

        if(game.getGameStep()== GameStep.DEATHMATCH_PLANNED ||game.getGameStep()== GameStep.DEATHMATCH  ){
            if(game.getDeathMatchIn()>0){
                String dmIn = TimeUtil.timeToString(game.getDeathMatchIn());
                objectiveSign.setLine(-7,"§6D.Match§7 : §f" + dmIn);
            }
            else{
                objectiveSign.setLine(-7,"§c§lDeathmatch");
            }
        }
        else if(game.getGameStep()== GameStep.PVP ||game.getGameStep()== GameStep.IN_GAME  ){
            if(game.getPvpIn()>0){
                String pvpIn = TimeUtil.timeToString(game.getPvpIn());
                objectiveSign.setLine(-7,"§6PVP§7 : §f" + pvpIn);
            }
            else{
                objectiveSign.setLine(-7,"§2§lPvP activé");
            }
        }
        else if(game.getGameStep()== GameStep.WAIT){
            objectiveSign.setLine(-7,"§aEn attente ...");
        }

        objectiveSign.setLine(-6, "  " );
        objectiveSign.setLine(-5, "§7Joueurs : §f" + game.getInGamePlayers().size());
        objectiveSign.setLine(-4, "   " );
        if(kills<=1){
            objectiveSign.setLine(-3, "§7Joueur tué : §f" + kills);
        }
        else{
            objectiveSign.setLine(-3, "§7Joueurs tué : §f" + kills);
        }

        objectiveSign.setLine(-2, "    " );
        objectiveSign.setLine(-1, "§f"  + time);
        objectiveSign.updateLines();*/
    }

    public UUID getLastDamager() {
        return lastDamager;
    }


    public Dimension getDimension() {
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(game.getDimensionsGameStep() == GameStep.DEATHMATCH){
            return Dimension.OVERWORLD;
        }
        return dimension;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
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

    public TargetType getTargetType() {
        return targetType;
    }

    public int getNextTargetDelay(){
        if(lastTarget == -1){
            return 0;
        }
        else{
            long nextTarget = lastTarget + (5*1000);
            return new Long((nextTarget - System.currentTimeMillis())/1000).intValue();
        }

    }

    public void setLastTargetTime(long lastTarget) {
        this.lastTarget = lastTarget;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
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
