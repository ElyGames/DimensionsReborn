package fr.elygames.cube.dimensions.game.tasks;

import fr.elygames.cube.ElyAPI;
import fr.elygames.cube.dimensions.game.entity.DimensionsPlayer;
import fr.elygames.cube.dimensions.game.entity.GameStep;
import fr.elygames.cube.dimensions.Dimensions;
import fr.elygames.cube.dimensions.game.DimensionsGame;

import org.bukkit.Sound;
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
public class TimeTask extends BukkitRunnable {

    @Override
    public void run() {
        DimensionsGame game = Dimensions.getInstance().getGame();
        //game.getRegisteredGamePlayers().values().forEach(DimensionsPlayer::updateScoreboard);
        game.increaseGameTime();
        if(game.getDimensionsGameStep()== GameStep.IN_GAME){
            switch(game.getPvpIn()){
                case 10 :case 5:case 4:case 3:case 2:case 1:
                    ElyAPI.getChatManager().sendGlobalMessage("§6Le PVP sera activé dans " + game.getPvpIn() + " secondes !");break;
                case 0:ElyAPI.getChatManager().sendGlobalMessage("§6Le PVP est activé !");
                    game.setGameStep(GameStep.PVP);
                    game.playSound(Sound.ENTITY_WITHER_DEATH,1F);game.sendActionBar("§6Le PvP est activé, prenez garde !");
                    if( game.getDimensionsGameStep()!=GameStep.DEATHMATCH_PLANNED && game.getPlayers().size()<=game.getDeathMatchSpawns().size()){
                        game.setGameStep(GameStep.DEATHMATCH_PLANNED);
                    }
            }
            game.decreasePvpIn();
        }
        if(game.getDimensionsGameStep()== GameStep.DEATHMATCH_PLANNED)
        {
            switch(game.getDeathMatchIn()){
                case 60: case 30 :case 10 :case 5:case 4:case 3:case 2:case 1:ElyAPI.getChatManager().sendGlobalMessage("§cDeathmatch dans " + game.getDeathMatchIn() + " secondes !");break;
                case 0:ElyAPI.getChatManager().sendGlobalMessage("§cLe Deathmatch commence !");game.startDeathmatch();
            }
            game.decreaseDeathmatchIn();
        }
    }
}
