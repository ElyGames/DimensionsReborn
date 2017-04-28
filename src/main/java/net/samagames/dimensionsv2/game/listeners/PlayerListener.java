package net.samagames.dimensionsv2.game.listeners;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.dimensionsv2.game.entity.dimension.Dimension;
import net.samagames.dimensionsv2.game.entity.dimension.DimensionsManager;
import net.samagames.dimensionsv2.game.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;

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
    public void onFoodUpdateEvent(FoodLevelChangeEvent e){
        DimensionsGame game = Dimensions.getInstance().getGame();
            e.setCancelled(game.isNonGameStep());
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(game.isNonGameStep()){
            e.setCancelled(true);
        }
        else{
            if(!game.getBlockBreakWhitelist().contains(e.getBlock().getType())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractAt(PlayerInteractAtEntityEvent e){
        if(e.getRightClicked() instanceof ArmorStand){
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(game.isNonGameStep()){
            e.setCancelled(true);
        }
        else{
            if(e.getBlock().getType().equals(Material.TNT)){
              e.getBlock().setType(Material.AIR);
              e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation().clone().add(0.5,0,0.5), EntityType.PRIMED_TNT);
            }
            else if(!game.getBlockPlaceWhitelist().contains(e.getBlock().getType())){
                e.setCancelled(true);
            }
            else{
                int x = e.getBlock().getX();
                int y = e.getBlock().getY();
                int z = e.getBlock().getZ();
                World w = e.getBlock().getWorld();
                boolean cantPlace = game.getBlockPlaceWhitelist().contains(w.getBlockAt(x, y + 1, z).getType())
                        || game.getBlockPlaceWhitelist().contains(w.getBlockAt(x, y - 1, z).getType())
                        || game.getBlockPlaceWhitelist().contains(w.getBlockAt(x + 1, y, z).getType())
                        || game.getBlockPlaceWhitelist().contains(w.getBlockAt(x - 1, y, z).getType())
                        || game.getBlockPlaceWhitelist().contains(w.getBlockAt(x, y, z + 1).getType())
                        || game.getBlockPlaceWhitelist().contains(w.getBlockAt(x, y, z - 1).getType());
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
        DimensionsGame game = Dimensions.getInstance().getGame();
        e.setCancelled(game.isNonGameStep());
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e)
    {
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(e.getItemDrop().getItemStack().equals(ItemUtils.getSwapItem())){
            e.setCancelled(true);
            return;
        }
        e.setCancelled(game.isNonGameStep());
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e)
    {
        e.blockList().clear();
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e)
    {

        DimensionsGame game = Dimensions.getInstance().getGame();
        e.setCancelled(game.isNonGameStep());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        if(e.hasItem()){
            if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK) ||
                    (e.getAction() == Action.LEFT_CLICK_AIR) || (e.getAction() == Action.LEFT_CLICK_BLOCK)  )
            {
                if(e.getItem().equals(ItemUtils.getSwapItem())){
                    DimensionsManager.getInstance().swap(e.getPlayer());
                }
            }
        }
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
        DimensionsGame game = Dimensions.getInstance().getGame();
        e.setCancelled(game.isNonGameStep());
    }

    @EventHandler
    public void onBukket(PlayerBucketEmptyEvent e)
    {
        DimensionsGame game = Dimensions.getInstance().getGame();
        e.setCancelled(game.isNonGameStep());
    }

    @EventHandler
    public void onHanging(HangingBreakByEntityEvent e)
    {
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(e.getEntity() instanceof Painting || e.getEntity() instanceof ItemFrame){
            e.setCancelled(true);
            return;
        }
        if (e.getEntity() instanceof Player){
            e.setCancelled(game.isNonGameStep());
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e){
        DimensionsGame game = Dimensions.getInstance().getGame();
        e.setCancelled(game.isNonGameStep());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(e.getSlot()==8){
            if(game.getGameStep()!= GameStep.DEATHMATCH){
                e.setCancelled(true);
                return;
            }
        }
        e.setCancelled(game.isNonGameStep());
    }


    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e){
        e.setCancelled(true);
    }


    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent e)
    {
        if (e.getEntity() instanceof Player)
        {
            DimensionsGame game = Dimensions.getInstance().getGame();
            DimensionsPlayer dp = game.getPlayer(e.getEntity().getUniqueId());
            if (dp.getDimension() == Dimension.PARALLEL){
                e.setCancelled(e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED));
            }

        }
    }



}

