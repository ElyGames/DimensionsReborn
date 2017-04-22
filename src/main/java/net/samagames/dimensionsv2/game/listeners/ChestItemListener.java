package net.samagames.dimensionsv2.game.listeners;

import net.samagames.dimensionsv2.game.entity.chestitem.ChestItemManager;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Created by Tigger_San on 22/04/2017.
 */
public class ChestItemListener implements Listener {


    /*@EventHandler
    public void onBreak(BlockBreakEvent e){
        if(e.getBlock().getType() == Material.CHEST){
            Chest chest = (Chest) e.getBlock().getState();
            ChestItemManager manager = ChestItemManager.getInstance();

            if(!manager.isOpened(chest)){
                Inventory inv = chest.getInventory();
                ChestItemManager.getInstance().fillRandomChestInventory(inv);
                manager.launchAndExplode( chest.getLocation(), FireworkEffect.builder().withColor(new Color[] { Color.FUCHSIA, Color.PURPLE, Color.RED }).with(FireworkEffect.Type.BALL).build());
                manager.opened(chest);
            }

        }
    }*/

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChestOpen(PlayerInteractEvent event)
    {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().equals(Material.CHEST))
        {
            ChestItemManager manager = ChestItemManager.getInstance();
                 Chest chest = (Chest) event.getClickedBlock().getState();
            if(!manager.isOpened(chest)) {
                Inventory inv = chest.getInventory();
                ChestItemManager.getInstance().fillRandomChestInventory(inv);
                manager.opened(chest);
            }

        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event)
    {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Chest)
        {
            ChestItemManager manager = ChestItemManager.getInstance();
            Chest chest = (Chest)holder;
            manager.launchAndExplode( chest.getLocation(), FireworkEffect.builder().withColor(new Color[] { Color.FUCHSIA, Color.PURPLE, Color.RED }).with(FireworkEffect.Type.BALL).build());
            chest.getBlock().setType(Material.AIR);
        }

    }
}

