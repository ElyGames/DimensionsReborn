package net.samagames.dimensionsv2.game.tasks;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.dimensionsv2.game.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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
public class TrakerTask extends BukkitRunnable
{
    @Override
    public void run() {
        DimensionsGame game = Dimensions.getInstance().getGame();

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
