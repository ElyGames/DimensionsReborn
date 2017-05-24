package net.samagames.dimensionsv2.game.listeners;
import net.minecraft.server.v1_10_R1.BlockAnvil;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.dimensionsv2.game.entity.TargetType;
import net.samagames.dimensionsv2.game.entity.dimension.Dimension;
import net.samagames.dimensionsv2.game.entity.dimension.DimensionsManager;
import net.samagames.dimensionsv2.game.utils.DistanceUtil;
import net.samagames.dimensionsv2.game.utils.ItemUtils;
import net.samagames.tools.chat.ActionBarAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

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
            else if(e.getBlock().getType() == Material.COAL_ORE){
                e.setCancelled(true);
                e.getBlock().breakNaturally(new ItemStack(Material.BARRIER));
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0.5,0,0.5),new ItemStack(Material.COAL,4));
            }
            else if(e.getBlock().getType() == Material.DIAMOND_ORE){
                e.setCancelled(true);
                e.getBlock().breakNaturally(new ItemStack(Material.BARRIER));
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0.5,0,0.5),new ItemStack(Material.DIAMOND,1));
            }
            else if(e.getBlock().getType() == Material.GOLD_ORE){
                e.setCancelled(true);
                e.getBlock().breakNaturally(new ItemStack(Material.BARRIER));
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0.5,0,0.5),new ItemStack(Material.GOLD_INGOT,1));
            }
            else if(e.getBlock().getType() == Material.LAPIS_ORE){
                e.setCancelled(true);
                e.getBlock().breakNaturally(new ItemStack(Material.BARRIER));
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0.5,0,0.5),new ItemStack(Material.INK_SACK,3,(short)4));
            }
            else if(e.getBlock().getType() == Material.IRON_ORE){
                e.setCancelled(true);
                e.getBlock().breakNaturally(new ItemStack(Material.BARRIER));
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0.5,0,0.5),new ItemStack(Material.IRON_INGOT,2,(short)4));
            }
        }
    }

    @EventHandler
    public void onInteractAt(PlayerInteractAtEntityEvent e){
        if(e.getRightClicked() instanceof ArmorStand || e.getRightClicked() instanceof ItemFrame ){
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
                //Prevent from placing several blocks next to each other
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
        if(e.getItemDrop().getItemStack().equals(ItemUtils.getSwapItem()) || e.getItemDrop().getItemStack().equals(ItemUtils.getTargetItem())) {
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
        if(e.getRightClicked() instanceof ItemFrame){
            e.setCancelled(true);
            return;
        }
        DimensionsGame game = Dimensions.getInstance().getGame();
        e.setCancelled(game.isNonGameStep());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        if(e.hasItem()){
            if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK) )
            {
                if(e.getItem().equals(ItemUtils.getSwapItem())){
                    DimensionsManager.getInstance().swap(e.getPlayer());
                    e.setCancelled(true);
                }
                else if(e.getItem().equals(ItemUtils.getTargetItem())){
                    DimensionsGame game = Dimensions.getInstance().getGame();
                    DimensionsPlayer dp = game.getPlayer(e.getPlayer().getUniqueId());
                    if(dp.getNextTargetDelay() >0){
                        e.getPlayer().sendMessage("§eMerci de patienter encore §c" + dp.getNextTargetDelay() + " §eseconde(s).");
                        e.setCancelled(true);
                        return;
                    }
                    dp.setLastTargetTime(System.currentTimeMillis());

                    if(dp.getTargetType() == TargetType.PLAYER){
                        Player target = null;
                        for(DimensionsPlayer dimPlayer : DimensionsManager.getInstance().getPlayersInDimension(dp.getDimension())){
                                Player current = dimPlayer.getPlayerIfOnline();
                                if(current!=e.getPlayer()){
                                    if(game.hasPlayer(current)) {
                                        if(target == null || e.getPlayer().getLocation().distance(current.getLocation()) <  e.getPlayer().getLocation().distance(target.getLocation())){
                                            target=current;
                                        }
                                    }
                                }
                        }
                        if(target!=null){
                            e.getPlayer().sendMessage("§bVotre boussole pointe vers le joueur le plus proche : " + target.getDisplayName());
                            dp.setTarget(target.getUniqueId());
                            dp.setTargetLoc(null);
                        }
                        else{
                            e.getPlayer().sendMessage("§cAucun joueur n'a été trouvé :(");
                        }
                    }
                    else if(dp.getTargetType() == TargetType.ANVIL){
                        e.getPlayer().sendMessage("§bVotre boussole pointe vers l'enclume la plus proche.");
                        Location target = DistanceUtil.getNearbyLocation(dp.getPlayerIfOnline().getLocation(),DimensionsManager.getInstance().getAnvils(dp.getDimension()));
                        dp.setTargetLoc(target);
                        dp.getPlayerIfOnline().setCompassTarget(target);
                    }
                    else if(dp.getTargetType() == TargetType.ENCHANTING){
                        e.getPlayer().sendMessage("§bVotre boussole pointe vers l'enclume la table d'enchantement la plus proche.");
                        Location target = DistanceUtil.getNearbyLocation(dp.getPlayerIfOnline().getLocation(),DimensionsManager.getInstance().getEnchanting(dp.getDimension()));
                        dp.setTargetLoc(target);
                        dp.getPlayerIfOnline().setCompassTarget(target);
                    }

                    e.setCancelled(true);
                    return;
                }
            }
            else if((e.getAction() == Action.LEFT_CLICK_BLOCK) || (e.getAction() == Action.LEFT_CLICK_AIR)){
                if(e.getItem().equals(ItemUtils.getSwapItem())){
                    DimensionsManager.getInstance().swap(e.getPlayer());
                    e.setCancelled(true);
                }
                else if(e.getItem().equals(ItemUtils.getTargetItem())){
                    DimensionsPlayer dp = Dimensions.getInstance().getGame().getPlayer(e.getPlayer().getUniqueId());
                    switch (dp.getTargetType()){
                        case PLAYER : dp.setTargetType(TargetType.ANVIL); dp.getPlayerIfOnline().sendMessage("§6Type de cible : §cEnclume la plus proche§6, cible actuelle réinitialisée.");break;
                        case ANVIL : dp.setTargetType(TargetType.ENCHANTING); dp.getPlayerIfOnline().sendMessage("§6Type de cible : §cTable d'enchantement la plus proche§6, cible actuelle réinitialisée.");break;
                        case ENCHANTING:  dp.setTargetType(TargetType.PLAYER); dp.getPlayerIfOnline().sendMessage("§6Type de cible : §cJoueur le plus proche§6, cible actuelle réinitialisée.");
                    }
                    ActionBarAPI.sendMessage(dp.getPlayerIfOnline()," ");
                    dp.setTarget(null);
                    dp.setTargetLoc(null);
                    e.setCancelled(true);
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
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){

        if(e.getClickedInventory().getType().equals(InventoryType.PLAYER) && e.getSlot() == 40){
            e.setCancelled(true);
            return;
        }
        DimensionsGame game = Dimensions.getInstance().getGame();
        e.setCancelled(game.isNonGameStep());
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

    @EventHandler
    public void onSwitchItem(PlayerItemHeldEvent e){
        Player p = e.getPlayer();

            if(p.getInventory().getItem(e.getNewSlot())!=null && p.getInventory().getItem(e.getNewSlot()).equals(ItemUtils.getTargetItem())){
                DimensionsGame game = Dimensions.getInstance().getGame();
                if(game.getPlayer(p.getUniqueId()).getTarget()!=null){
                    ItemUtils.displayActionBarTarget(p, Bukkit.getPlayer(game.getPlayer(p.getUniqueId()).getTarget()));
                }
                else if(game.getPlayer(p.getUniqueId()).getTargetLoc()!=null){
                    ItemUtils.displayActionBarTarget(p, game.getPlayer(p.getUniqueId()).getTargetType(),game.getPlayer(p.getUniqueId()).getTargetLoc());
                }
            }
            else if(p.getInventory().getItem(e.getPreviousSlot())!=null && (p.getInventory().getItem(e.getNewSlot())==null || p.getInventory().getItem(e.getPreviousSlot()).equals(ItemUtils.getTargetItem()))){
                ActionBarAPI.sendMessage(p," ");
            }
    }
}

