package net.samagames.dimensionsv2.game.tasks;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.dimensionsv2.game.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Tigger_San on 27/06/2017.
 */
public class TrakerTask extends BukkitRunnable
{
    @Override
    public void run() {
        DimensionsGame game = Dimensions.getInstance().getGame();
        game.getRegisteredGamePlayers().values().forEach(DimensionsPlayer::updateScoreboard);
        //For player that have a human target
        game.getInGamePlayers().values().stream().filter(dp -> dp.getTarget()!=null).forEach(dp -> {

            dp.getPlayerIfOnline().setCompassTarget(Bukkit.getPlayer(dp.getTarget()).getLocation().getBlock().getLocation());
            if(dp.getPlayerIfOnline().getInventory().getItemInMainHand().equals(ItemUtils.getTargetItem(dp.getPlayerIfOnline()))){
                ItemUtils.displayActionBarTarget(dp.getPlayerIfOnline(),Bukkit.getPlayer(dp.getTarget()));
            }
            else{
                ItemUtils.displayActionBarArrow(dp.getPlayerIfOnline(),Bukkit.getPlayer(dp.getTarget()));
            }

        });
        //For player that have a non human target
        game.getInGamePlayers().values().stream().filter(dp -> dp.getTargetLoc()!=null).forEach(dp -> {
            if(dp.getPlayerIfOnline().getInventory().getItemInMainHand().equals(ItemUtils.getTargetItem(dp.getPlayerIfOnline()))){
                ItemUtils.displayActionBarTarget(dp.getPlayerIfOnline(),dp.getTargetType(),dp.getTargetLoc());
            }
            else{
                ItemUtils.displayActionBarArrow(dp.getPlayerIfOnline(),dp.getTargetType(),dp.getTargetLoc());
            }
        });

        if(game.getGameStep() == GameStep.DEATHMATCH){
            this.cancel();
        }
    }
}
