package net.samagames.dimensionsv2.game.listeners;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.chestitem.ChestItemManager;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChestOpen(PlayerInteractEvent e)
    {
        if(e.getAction() == Action.PHYSICAL){
            if(e.getClickedBlock().getType() == Material.SOIL){
                e.setCancelled(true);
                return;
            }
        }
        DimensionsGame game = Dimensions.getInstance().getGame();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.TRAPPED_CHEST))
        {
            e.getPlayer().sendMessage("§6§oIt's a trap !");
            e.setCancelled(true);
            return;
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.CHEST))
        {
            if(Dimensions.getInstance().getGame().isSpectator(e.getPlayer()) ||game.isNonGameStep() ){
                e.setCancelled(true);
                return;
            }
            ChestItemManager manager = ChestItemManager.getInstance();
            Chest chest = (Chest) e.getClickedBlock().getState();

            InventoryHolder ih = chest.getInventory().getHolder();
            if (ih instanceof DoubleChest) {
                e.setCancelled(true);
                return;
            }
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
            DimensionsGame game = Dimensions.getInstance().getGame();
            ChestItemManager manager = ChestItemManager.getInstance();
            Chest chest = (Chest)holder;
            Color[][] colors = new Color[][]{new Color[] { Color.FUCHSIA, Color.PURPLE, Color.RED },
                               new Color[] { Color.BLACK, Color.GRAY, Color.WHITE },
                               new Color[] { Color.GREEN, Color.LIME, Color.OLIVE },
                               new Color[] { Color.BLUE, Color.AQUA, Color.WHITE }};
            manager.launchAndExplode( chest.getLocation(), FireworkEffect.builder().withColor(colors[game.getRandom().nextInt(colors.length)]).with(FireworkEffect.Type.BALL).build());
            chest.getBlock().setType(Material.AIR);
        }

    }
}

