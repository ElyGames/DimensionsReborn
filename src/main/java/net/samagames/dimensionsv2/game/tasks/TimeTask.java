package net.samagames.dimensionsv2.game.tasks;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.dimensionsv2.game.entity.chestitem.ChestItemManager;
import net.samagames.dimensionsv2.game.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Represent the task that count the time and activates events
 * Created by Tigger_San on 22/04/2017.
 */
public class TimeTask extends BukkitRunnable {

    @Override
    public void run() {
        DimensionsGame game = Dimensions.getInstance().getGame();
        game.increaseGameTime();
        if(game.getGameStep()==GameStep.IN_GAME){
            switch(game.getPvpIn()){
                case 10 :case 5:case 4:case 3:case 2:case 1:game.getCoherenceMachine().getMessageManager().writeCustomMessage("§6Le PVP sera activé dans " + game.getPvpIn() + " secondes !",true);break;
                case 0:game.getCoherenceMachine().getMessageManager().writeCustomMessage("§6Le PVP est activé !",true);
                    game.setGameStep(GameStep.PVP);
                    game.playSound(Sound.ENTITY_WITHER_DEATH,1F);game.sendActionBar("§6Le PvP est activé, prenez garde !");
                    if( game.getGameStep()!=GameStep.DEATHMATCH_PLANNED && game.getInGamePlayers().size()<=game.getDeathMatchSpawns().size()){
                        game.setGameStep(GameStep.DEATHMATCH_PLANNED);
                    }
            }
            game.decreasePvpIn();
        }
        if(game.getGameStep()== GameStep.DEATHMATCH_PLANNED)
        {
            switch(game.getDeathMatchIn()){
                case 60: case 30 :case 10 :case 5:case 4:case 3:case 2:case 1:game.getCoherenceMachine().getMessageManager().writeCustomMessage("§cDeathmatch dans " + game.getDeathMatchIn() + " secondes !",true);break;
                case 0:game.getCoherenceMachine().getMessageManager().writeCustomMessage("§cLe Deathmatch commence !",true);game.startDeathmatch();
            }
            game.decreaseDeathmatchIn();
        }
    }
}
