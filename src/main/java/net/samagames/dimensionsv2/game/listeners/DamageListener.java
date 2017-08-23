package net.samagames.dimensionsv2.game.listeners;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.utils.ItemUtils;
import org.bukkit.EntityEffect;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import java.util.stream.Collectors;

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
public class DamageListener implements Listener
{

    @EventHandler
    public void onDamage(EntityDamageEvent e)
    {

        DimensionsGame game = Dimensions.getInstance().getGame();
        if(game.isNonGameStep() ||
                e.getCause() == EntityDamageEvent.DamageCause.MAGIC && game.isNonPVPActive() ||
                e.getCause() == EntityDamageEvent.DamageCause.POISON && game.isNonPVPActive()){
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof ItemFrame){
            e.setCancelled(true);
            return;
        }
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(e.getDamager() instanceof Player && !(e.getEntity() instanceof Player)){
            if(game.isNonGameStep() ){
                e.setCancelled(true);
                return;
            }
            else if(e.getEntity() instanceof ArmorStand){
                //Beautiful animation on armor stands death :o
                e.setCancelled(true);
                e.getEntity().playEffect(EntityEffect.DEATH);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        e.getEntity().remove();
                    }
                }.runTaskLater(Dimensions.getInstance(),20L);
            }
        }
        else if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
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
        final List<ItemStack> remove = event.getDrops().stream().filter(stack -> stack.equals(ItemUtils.getTargetItem(p))|| stack.equals(ItemUtils.getSwapItem())).collect(Collectors.toList());
        for (ItemStack rem : remove){
            event.getDrops().remove(rem);
        }
        event.setDeathMessage("");
        game.die(p);
        game.stumpPlayer(p,false);
    }

    @EventHandler
    public void onStorm(WeatherChangeEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onFire(BlockIgniteEvent e){
        e.setCancelled(true);
    }
}
