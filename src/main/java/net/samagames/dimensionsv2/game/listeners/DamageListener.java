package net.samagames.dimensionsv2.game.listeners;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tigger_San on 23/04/2017.
 */
public class DamageListener implements Listener
{

    @EventHandler
    public void onDamage(EntityDamageEvent e)
    {
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(game.isNonGameStep()){
            e.setCancelled(true);
            return;
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.WITHER){
            e.setCancelled(true);
        }
        else if (e.getCause() == EntityDamageEvent.DamageCause.MAGIC && game.isNonPVPActive()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e){
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(e.getDamager() instanceof Player && !(e.getEntity() instanceof Player)){
            e.setCancelled(true);
            return;
        }
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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player p = event.getEntity();
        DimensionsGame game = Dimensions.getInstance().getGame();
        final List<ItemStack> remove = event.getDrops().stream().filter(stack -> stack.getType() == Material.COMPASS || stack.getType() == Material.EYE_OF_ENDER).collect(Collectors.toList());
        for (ItemStack rem : remove){
            event.getDrops().remove(rem);
        }
        event.setDeathMessage("");
        game.die(p);
        game.stumpPlayer(p,false);
    }


}
