package net.samagames.dimensionsv2.game.utils;
import net.samagames.dimensionsv2.game.entity.TargetType;
import net.samagames.tools.chat.ActionBarAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utils for items  (swap item and target item)
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

    /**
     * Get the swap item
     * @return The swap item
     */
    public static ItemStack getSwapItem(){
        return swapItem;
    }

    /**
     * Get the target item
     * @return The target item
     */
    public static ItemStack getTargetItem(){
      return targetItem;
    }

    /**
     * Display a target message in action bar
     * @param p The receiver
     * @param target The target
     */
    public static void displayActionBarTarget(Player p , Player target){
        ActionBarAPI.sendMessage(p,target.getDisplayName() + "§7 : §c"+
                new Double(p.getLocation().distance(target.getLocation())).intValue()+"m");
    }

    /**
     * Display a target message in action bar
     * @param p The receiver
     * @param type The type of target
     * @param l The targeted location
     */
    public static void displayActionBarTarget(Player p , TargetType type, Location l){
        String s = "";
        switch (type){
            case ENCHANTING: s = "Table d'enchantement"; break;
            case ANVIL: s = "Enclume";
        }
        ActionBarAPI.sendMessage(p,"§2" +s+ "§7 : §c"+
                new Double(p.getLocation().distance(l)).intValue()+"m");
    }
}
