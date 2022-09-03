package fr.elygames.cube.dimensions.game.tasks;

import fr.elygames.cube.dimensions.game.entity.GameStep;
import fr.elygames.cube.dimensions.game.utils.ItemUtils;
import fr.elygames.cube.dimensions.Dimensions;
import fr.elygames.cube.dimensions.game.DimensionsGame;

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
        game.getPlayers().values().stream().filter(dp -> dp.getTarget()!=null).forEach(dp -> {

            dp.getPlayerIfOnline().setCompassTarget(Bukkit.getPlayer(dp.getTarget()).getLocation().getBlock().getLocation());
            if(dp.getPlayerIfOnline().getInventory().getItemInMainHand().equals(ItemUtils.getTargetItem(dp.getPlayerIfOnline()))){
                ItemUtils.displayActionBarTarget(dp.getPlayerIfOnline(),Bukkit.getPlayer(dp.getTarget()));
            }
            else{
                ItemUtils.displayActionBarArrow(dp.getPlayerIfOnline(),Bukkit.getPlayer(dp.getTarget()));
            }

        });
        //For player that have a non human target
        game.getPlayers().values().stream().filter(dp -> dp.getTargetLoc()!=null).forEach(dp -> {
            if(dp.getPlayerIfOnline().getInventory().getItemInMainHand().equals(ItemUtils.getTargetItem(dp.getPlayerIfOnline()))){
                ItemUtils.displayActionBarTarget(dp.getPlayerIfOnline(),dp.getTargetType(),dp.getTargetLoc());
            }
            else{
                ItemUtils.displayActionBarArrow(dp.getPlayerIfOnline(),dp.getTargetType(),dp.getTargetLoc());
            }
        });

        if(game.getDimensionsGameStep() == GameStep.DEATHMATCH){
            this.cancel();
        }
    }
}
