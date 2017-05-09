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

/**
 * Represent the task that give random potions effects of players in parallel world
 * Created by Tigger_San on 27/04/2017.
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
