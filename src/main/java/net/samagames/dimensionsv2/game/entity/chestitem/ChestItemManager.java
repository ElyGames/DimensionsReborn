package net.samagames.dimensionsv2.game.entity.chestitem;

import net.samagames.dimensionsv2.Dimensions;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage loots chest
 * Created by Tigger_San on 22/04/2017.
 */
public class ChestItemManager {
    private static ChestItemManager ourInstance = new ChestItemManager();
    private List<Location> openedChests;

    /**
     * Launch and directly explode a firework
     * @param loc The location
     * @param effect The builded effect
     */
    public void launchAndExplode(Location loc, FireworkEffect effect){
        loc = loc.add(0.5,0.5,0.5);
        final Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(effect);
        fwm.setPower(0);
        fw.setFireworkMeta(fwm);
        Dimensions.getInstance().getServer().getScheduler().runTaskLater(Dimensions.getInstance(), fw::detonate, 1);
    }

    /**
     * Save the fact that the chest is opened
     * @param c The chest
     */
    public void opened(Chest c){
        openedChests.add(c.getLocation());
    }


    public static ChestItemManager getInstance() {
        return ourInstance;
    }

    /**
     * Verify if a chest is opened
     * @param c The chest
     * @return Opened or not
     */
    public boolean isOpened(Chest c){
        return openedChests.contains(c.getLocation());
    }

    private ChestItemManager() {
        openedChests = new ArrayList<>();
    }
}
