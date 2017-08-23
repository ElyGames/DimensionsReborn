package net.samagames.dimensionsv2.game.tasks;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.dimensionsv2.game.entity.dimension.Dimension;
import net.samagames.dimensionsv2.game.entity.dimension.DimensionsManager;
import net.samagames.tools.chat.ActionBarAPI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
public class RandomEffectsTask extends BukkitRunnable{

    private int nextEffect = 60;
    private PotionEffect[] effects;

    public RandomEffectsTask()
    {
        this.effects = new PotionEffect[]
                {
                        new PotionEffect(PotionEffectType.POISON, 80, 0),
                        new PotionEffect(PotionEffectType.BLINDNESS, 60, 0),
                        new PotionEffect(PotionEffectType.SLOW, 100, 0),
                        new PotionEffect(PotionEffectType.WITHER, 140, 0),
                        new PotionEffect(PotionEffectType.HUNGER, 140, 0),
                        new PotionEffect(PotionEffectType.CONFUSION, 80, 0)
                };
    }

    @Override
    public void run()
    {
        this.nextEffect--;
        if (this.nextEffect <= 0)
        {
            DimensionsGame game = Dimensions.getInstance().getGame();
            if (game.getGameStep() == GameStep.DEATHMATCH){
                this.cancel();
                return;
            }
            this.nextEffect = 30 + game.getRandom().nextInt(60);

            for (DimensionsPlayer player : DimensionsManager.getInstance().getPlayersInDimension(Dimension.PARALLEL))
            {
                int effect = game.getRandom().nextInt(this.effects.length);
                Player p = player.getPlayerIfOnline();
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        p.addPotionEffect(effects[effect]);
                        ActionBarAPI.sendMessage(p,"§c§oLe maléfice de ce monde semble vous atteindre ...");
                        p.playSound(p.getLocation(), Sound.BLOCK_PORTAL_AMBIENT,50F,1F);
                    }
                }.runTask(Dimensions.getInstance());
            }
        }
    }
}
