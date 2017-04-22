package net.samagames.dimensionsv2.game.listeners;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.GameStep;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Tigger_San on 22/04/2017.
 */
public class PlayerListener implements Listener
{
    @EventHandler
    public void onMove(PlayerMoveEvent e){
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(game.getGameStep()== GameStep.PRE_TELEPORT && game.hasPlayer(e.getPlayer()) && !game.isSpectator(e.getPlayer())){
            if (e.getTo().getBlockX() != e.getFrom().getBlockX() || e.getTo().getBlockZ() != e.getFrom().getBlockZ())
                e.setTo(e.getFrom());
        }
    }
    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e){
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
            if(game.getGameStep()!= GameStep.PRE_TELEPORT && game.getGameStep()!= GameStep.WAIT && game.getGameStep()!= GameStep.IN_GAME ){
                e.setCancelled(true);
            }
        }
    }
}

