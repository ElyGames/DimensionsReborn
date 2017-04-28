package net.samagames.dimensionsv2.game.entity.chestitem;
import org.bukkit.inventory.ItemStack;
import java.util.Random;

/**
 * Created by Tigger_San on 22/04/2017.
 */
public class ChestItem {
    private ItemStack item;
    private int frequency;
    private int[] quantities;

    public ChestItem(ItemStack item, int frequency, int... quantities) {
        this.frequency = frequency;
        this.item = item;
        this.quantities = quantities;
    }

    public int getFrequency() {
        return frequency;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getQuantity(Random random) {
       if(quantities.length==0){
           return 1;
       }
        else{
            return quantities[random.nextInt(quantities.length)];
       }
    }
}

