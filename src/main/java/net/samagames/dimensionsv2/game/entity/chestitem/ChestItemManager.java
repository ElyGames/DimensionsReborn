package net.samagames.dimensionsv2.game.entity.chestitem;

import net.samagames.dimensionsv2.Dimensions;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Tigger_San on 22/04/2017.
 */
public class ChestItemManager {
    private static ChestItemManager ourInstance = new ChestItemManager();
    private List<Location> openedChests;

    public void launchAndExplode(Location loc, FireworkEffect effect){
        loc = loc.add(0.5,0.5,0.5);
        final Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(effect);
        fwm.setPower(0);
        fw.setFireworkMeta(fwm);
        Dimensions.getInstance().getServer().getScheduler().runTaskLater(Dimensions.getInstance(), fw::detonate, 1);
    }
    public void opened(Chest c){
        openedChests.add(c.getLocation());
    }


    public static ChestItemManager getInstance() {
        return ourInstance;
    }

    public boolean isOpened(Chest c){
        return openedChests.contains(c.getLocation());
    }

    private ChestItemManager() {
        openedChests = new ArrayList<>();
    }
}
