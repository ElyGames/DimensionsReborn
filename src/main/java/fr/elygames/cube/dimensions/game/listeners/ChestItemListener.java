package fr.elygames.cube.dimensions.game.listeners;

import fr.elygames.cube.dimensions.Dimensions;
import fr.elygames.cube.dimensions.game.DimensionsGame;
import fr.elygames.cube.dimensions.game.entity.chestitem.ChestItemManager;
import fr.elygames.cube.dimensions.game.entity.dimension.Dimension;
import fr.elygames.cube.dimensions.game.entity.dimension.DimensionsManager;


import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

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
public class ChestItemListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChestOpen(PlayerInteractEvent e)
    {
        if(e.getAction() == Action.PHYSICAL){
            if(e.getClickedBlock().getType() == Material.FARMLAND){
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
            if(Dimensions.getInstance().getGame().getSpectators().containsKey(e.getPlayer().getUniqueId()) ||game.isNonGameStep() ){
                e.setCancelled(true);
                return;
            }
            Chest chest = (Chest) e.getClickedBlock().getState();
            InventoryHolder ih = chest.getInventory().getHolder();
            if (ih instanceof DoubleChest) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e){
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(game.hasPlayer((Player)e.getPlayer())){
            if(e.getInventory().getHolder() instanceof Chest){
                Chest chest = (Chest)e.getInventory().getHolder();
                ChestItemManager manager = ChestItemManager.getInstance();
                if(!manager.isOpened(chest)) {
                    //TODO : rework this
                    /*TileEntity te = ((CraftWorld)chest.getBlock().getWorld()).getHandle().getTileEntity(new BlockPosition(chest.getX(),chest.getY(),chest.getZ()));
                    TileEntityChest tec = (TileEntityChest) te;
                    NBTTagCompound c = tec.d();
                    //Apply customs loots based on LootTables system
                    if(game.getPlayer(e.getPlayer().getUniqueId()).getDimension() == Dimension.OVERWORLD){
                        c.setString("LootTable", DimensionsManager.getInstance().getOverworldLootTable());
                    }
                    else{
                        c.setString("LootTable",DimensionsManager.getInstance().getParallelLootTable());
                    }
                    tec.a(c);*/

                    manager.opened(chest);
                }
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

