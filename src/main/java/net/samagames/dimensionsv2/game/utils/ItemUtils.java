package net.samagames.dimensionsv2.game.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Tigger_San on 27/04/2017.
 */
public class ItemUtils {
    public static ItemStack getSwapItem(){
        ItemStack is = new ItemStack(Material.EYE_OF_ENDER);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName("§5Changer de dimension");
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getTargetItem(){
        ItemStack is = new ItemStack(Material.COMPASS);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName("§aChanger de dimension");
        is.setItemMeta(im);
        return is;
    }
}
