package net.samagames.dimensionsv2.game.listeners;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.GameStep;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Created by Tigger_San on 23/04/2017.
 */
public class DamageListener implements Listener
{

    @EventHandler
    public void onDamage(EntityDamageEvent e)
    {
        DimensionsGame game = Dimensions.getInstance().getGame();
        if (e.getCause() == EntityDamageEvent.DamageCause.WITHER){
            e.setCancelled(true);
        }
        else if (e.getCause() == EntityDamageEvent.DamageCause.MAGIC && game.isNonGameStep()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e){
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
            if(game.isNonPVPActive()){
                e.setCancelled(true);
            }
            else{
                Player damager = (Player)e.getDamager();
                Player player = (Player) e.getEntity();
                game.playerDamageByPlayer(player,damager);
            }
        }
        else if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
        {
            if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)
            {
                if(game.isNonPVPActive()){
                    e.setCancelled(true);
                    return ;
                }
                Player shooter = (Player)((Projectile) e.getDamager()).getShooter();
                Player player = (Player) e.getEntity();
                game.playerDamageByPlayer(player,shooter);
            }
        }

    }


}
