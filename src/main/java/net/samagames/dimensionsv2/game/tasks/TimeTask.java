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
        game.getRegisteredGamePlayers().values().forEach(DimensionsPlayer::updateScoreboard);
        //For player that have a human target
        game.getInGamePlayers().values().stream().filter(dp -> dp.getTarget()!=null).forEach(dp -> {

                dp.getPlayerIfOnline().setCompassTarget(Bukkit.getPlayer(dp.getTarget()).getLocation());
            if(dp.getPlayerIfOnline().getInventory().getItemInMainHand().equals(ItemUtils.getTargetItem(dp.getPlayerIfOnline()))){
                ItemUtils.displayActionBarTarget(dp.getPlayerIfOnline(),Bukkit.getPlayer(dp.getTarget()));
            }

        });
        //For player that have a non human target
        game.getInGamePlayers().values().stream().filter(dp -> dp.getTargetLoc()!=null).forEach(dp -> {
            if(dp.getPlayerIfOnline().getInventory().getItemInMainHand().equals(ItemUtils.getTargetItem(dp.getPlayerIfOnline()))){
                ItemUtils.displayActionBarTarget(dp.getPlayerIfOnline(),dp.getTargetType(),dp.getTargetLoc());
            }
        });
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
