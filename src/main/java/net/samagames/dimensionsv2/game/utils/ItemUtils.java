package net.samagames.dimensionsv2.game.utils;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.tools.chat.ActionBarAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Tigger_San on 27/04/2017.
 */
public class ItemUtils {

    private static ItemStack swapItem;
    private static ItemStack targetItem;

    public ItemUtils(){
        swapItem = new ItemStack(Material.EYE_OF_ENDER);
        ItemMeta im = swapItem.getItemMeta();
        im.setDisplayName("§5Changer de dimension");
        swapItem.setItemMeta(im);

        targetItem = new ItemStack(Material.COMPASS);
        im = targetItem.getItemMeta();
        im.setDisplayName("§aBoussole magique");
        targetItem.setItemMeta(im);

    }

    public static ItemStack getSwapItem(){
        return swapItem;
    }

    public static ItemStack getTargetItem(){
      return targetItem;
    }

    public static void displayActionBarTarget(Player p , Player target){
        ActionBarAPI.sendMessage(p,target.getDisplayName() + "§7 : §c"+
                new Double(p.getLocation().distance(target.getLocation())).intValue()+"m");
    }
}
