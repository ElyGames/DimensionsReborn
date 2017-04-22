package net.samagames.dimensionsv2.game.listeners;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.GameStep;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

/**
 * Created by Tigger_San on 22/04/2017.
 */
public class PlayerListener implements Listener
{

    private boolean isNonGameStep(){
        DimensionsGame game = Dimensions.getInstance().getGame();
        return(game.getGameStep()==GameStep.WAIT || game.getGameStep()==GameStep.PRE_TELEPORT  || game.getGameStep()==GameStep.FINISH);
    }
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
            if(game.getGameStep()== GameStep.PRE_TELEPORT || game.getGameStep()== GameStep.WAIT || game.getGameStep()== GameStep.IN_GAME ){
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onFoodUpdateEvent(FoodLevelChangeEvent e){
            e.setCancelled(isNonGameStep());
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(isNonGameStep()){
            e.setCancelled(true);
        }
        else{
            DimensionsGame game = Dimensions.getInstance().getGame();
            if(!game.getBlockPlaceAndBreakWhitelist().contains(e.getBlock().getType())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(isNonGameStep()){
            e.setCancelled(true);
        }
        else{
            DimensionsGame game = Dimensions.getInstance().getGame();
            if(!game.getBlockPlaceAndBreakWhitelist().contains(e.getBlock().getType())){
                e.setCancelled(true);
            }
            else{
                int x = e.getBlock().getX();
                int y = e.getBlock().getY();
                int z = e.getBlock().getZ();
                World w = e.getBlock().getWorld();
                boolean cantPlace = game.getBlockPlaceAndBreakWhitelist().contains(w.getBlockAt(x, y + 1, z).getType())
                        || game.getBlockPlaceAndBreakWhitelist().contains(w.getBlockAt(x, y - 1, z).getType())
                        || game.getBlockPlaceAndBreakWhitelist().contains(w.getBlockAt(x + 1, y, z).getType())
                        || game.getBlockPlaceAndBreakWhitelist().contains(w.getBlockAt(x - 1, y, z).getType())
                        || game.getBlockPlaceAndBreakWhitelist().contains(w.getBlockAt(x, y, z + 1).getType())
                        || game.getBlockPlaceAndBreakWhitelist().contains(w.getBlockAt(x, y, z - 1).getType());
                if (cantPlace)
                {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("§cVous ne pouvez pas placer de bloc ici.");
                }
            }
        }
    }

    @EventHandler
    public void pickupEvet(PlayerPickupItemEvent e)
    {
        e.setCancelled(isNonGameStep());
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e)
    {
        e.setCancelled(isNonGameStep());
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e)
    {
        e.blockList().clear();
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e)
    {
        e.setCancelled(isNonGameStep());

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        if (e.getClickedBlock() != null){
            if(e.getClickedBlock().getType() == Material.BED || e.getClickedBlock().getType() == Material.BED_BLOCK)
            {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBukket(PlayerBucketFillEvent e)
    {
        e.setCancelled(isNonGameStep());
    }

    @EventHandler
    public void onBukket(PlayerBucketEmptyEvent e)
    {
        e.setCancelled(isNonGameStep());
    }

    @EventHandler
    public void onHanging(HangingBreakByEntityEvent e)
    {
        if (e.getEntity() instanceof Player){
            e.setCancelled(isNonGameStep());
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e){
        e.setCancelled(isNonGameStep());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        e.setCancelled(isNonGameStep());
    }






}

