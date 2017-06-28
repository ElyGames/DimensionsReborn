package net.samagames.dimensionsv2.game.utils;
import net.samagames.dimensionsv2.game.entity.TargetType;
import net.samagames.tools.chat.ActionBarAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 * Utils for items  (swap item and target item)
 * Created by Tigger_San on 27/04/2017.
 */
public class ItemUtils {

    private static ItemStack swapItem;

    
    {
        swapItem = new ItemStack(Material.EYE_OF_ENDER);
        ItemMeta im = swapItem.getItemMeta();
        im.setDisplayName("§5Changer de dimension");
        swapItem.setItemMeta(im);

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
    public static ItemStack getTargetItem(Player p){
        ItemStack i  = new ItemStack(Material.NETHER_STAR);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName("§aSamaTracker §7| " + p.getDisplayName());
        i.setItemMeta(im);
        return i;
    }

    /**
     * Display a target message in action bar
     * @param p The receiver
     * @param target The target
     */
    public static void displayActionBarTarget(Player p , Player target){
        ActionBarAPI.sendMessage(p,target.getDisplayName() + "§7 : §b"+ getDirection(p,target.getLocation().clone())+ "§c "+
        new Double(p.getLocation().distance(target.getLocation())).intValue()+"m" );
    }

    public static void displayActionBarArrow(Player p , Player target){
        ActionBarAPI.sendMessage(p,target.getDisplayName() + "§7 : §b"+ getDirection(p,target.getLocation().clone()));
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
        ActionBarAPI.sendMessage(p,"§2" +s+ "§7 : §b"+ getDirection(p,l.clone()) + "§c "+
        new Double(p.getLocation().distance(l)).intValue()+"m"  );
    }
    public static void displayActionBarArrow(Player p , TargetType type, Location l){
        String s = "";
        switch (type){
            case ENCHANTING: s = "Table d'enchantement"; break;
            case ANVIL: s = "Enclume";
        }
        ActionBarAPI.sendMessage(p,"§2" +s+ "§7 : §b"+ getDirection(p,l.clone()));
    }

    private static char getDirection(Player p, Location point)
    {
        Location ploc = p.getLocation();
        // on ignore l'axe y
        ploc.setY(0);
        point.setY(0);
        // on récupère la direction de la tête du player
        Vector d = ploc.getDirection();
        // on récupère la direction du point par rapport au player
        Vector v = point.subtract(ploc).toVector().normalize();
        // on convertit le tout en un angle en degrés
        double a = Math.toDegrees(Math.atan2(d.getX(), d.getZ()));
        a -= Math.toDegrees(Math.atan2(v.getX(), v.getZ()));
        // on se décale de 22.5 degrés pour se caler sur les demi points cardinaux
        a = (int)(a + 22.5) % 360;
        // on  s'assure d'avoir un angle strictement positif
        if (a < 0)
            a += 360;
        // on récupère le caractère correspondant à l'angle
        return "⬆⬈➡⬊⬇⬋⬅⬉".charAt((int)a / 45);

    }
}
