package net.samagames.dimensionsv2.game.tasks;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Tigger_San on 22/04/2017.
 */
public class TimeTask extends BukkitRunnable {

    @Override
    public void run() {
        DimensionsGame game = Dimensions.getInstance().getGame();
        game.getInGamePlayers().values().forEach(DimensionsPlayer::updateScoreboard);
        game.increaseGameTime();
        if(game.getPvpIn()>=0){
            switch(game.getPvpIn()){
                case 10 :case 5:case 4:case 3:case 2:case 1:game.getCoherenceMachine().getMessageManager().writeCustomMessage("§6Le PVP sera activé dans " + game.getPvpIn() + " secondes !",true);break;
                case 0:game.getCoherenceMachine().getMessageManager().writeCustomMessage("§6Le PVP est activé !",true);
            }
            game.decreasePvpIn();
        }
        if(game.isDeathMatchPlanned() && game.getDeathMatchIn()>=0)
        {
            switch(game.getDeathMatchIn()){
                case 60: case 30 :case 10 :case 5:case 4:case 3:case 2:case 1:game.getCoherenceMachine().getMessageManager().writeCustomMessage("§cDeathmatch dans " + game.getDeathMatchIn() + " secondes !",true);break;
                case 0:game.getCoherenceMachine().getMessageManager().writeCustomMessage("§cLe Deathmatch commence !",true);
            }
            game.decreaseDeathmatchIn();
        }
    }
}
