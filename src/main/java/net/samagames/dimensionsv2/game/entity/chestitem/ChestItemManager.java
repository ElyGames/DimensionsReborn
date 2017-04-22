package net.samagames.dimensionsv2.game.entity.chestitem;

import net.samagames.dimensionsv2.Dimensions;
import net.samagames.tools.MojangShitUtils;
import org.bukkit.DyeColor;
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

    public static ChestItemManager getInstance() {
        return ourInstance;
    }

    private List<ChestItem> items;
    private Random random;
    private List<Location> openedChests;

    public void launchAndExplode(Location loc, FireworkEffect effect){
        loc = loc.add(0.5,0.5,0.5);
        final Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(effect);
        fwm.setPower(0);
        fw.setFireworkMeta(fwm);
        Dimensions.getInstance().getServer().getScheduler().runTaskLater(Dimensions.getInstance(), fw::detonate, 2);
    }
    public void opened(Chest c){
        openedChests.add(c.getLocation());
    }

    public boolean isOpened(Chest c){
        return openedChests.contains(c.getLocation());
    }

    public void fillRandomChestInventory(Inventory inv){

        inv.clear();
        Collections.shuffle(this.items);
        int addedItems = 0;
        int slot = 0;
        for (ChestItem item : this.items)
        {
            if (addedItems > 20)
                break ;
            int freq = item.getFrequency();
            if (random.nextInt(10000) <= freq)
            {
                ItemStack stack = item.getItem();
                stack.setAmount(item.getQuantity(random));
                while (inv.getItem(slot) != null)
                    slot++;
                inv.setItem(slot, item.getItem());
                addedItems++;
            }
            slot++;
            if (slot > 26)
                slot = 0;
        }
    }
    private ChestItemManager() {
        items = new ArrayList<>();
        random = new Random();
        openedChests= new ArrayList<>();

        // Armors

        items.add(new ChestItem(new ItemStack(Material.LEATHER_LEGGINGS, 1), 700));
        items.add(new ChestItem(new ItemStack(Material.LEATHER_BOOTS, 1), 1000));
        items.add(new ChestItem(new ItemStack(Material.LEATHER_CHESTPLATE, 1), 700));
        items.add(new ChestItem(new ItemStack(Material.LEATHER_HELMET, 1), 1000));
        items.add(new ChestItem(new ItemStack(Material.CHAINMAIL_HELMET, 1), 700));
        items.add(new ChestItem(new ItemStack(Material.CHAINMAIL_BOOTS, 1), 700));
        items.add(new ChestItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1), 1000));
        items.add(new ChestItem(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1), 1000));
        items.add(new ChestItem(new ItemStack(Material.IRON_LEGGINGS, 1), 700));
        items.add(new ChestItem(new ItemStack(Material.IRON_BOOTS, 1), 700));
        items.add(new ChestItem(new ItemStack(Material.IRON_CHESTPLATE, 1), 700));
        items.add(new ChestItem(new ItemStack(Material.IRON_HELMET, 1), 700));
        items.add(new ChestItem(new ItemStack(Material.DIAMOND_LEGGINGS, 1), 25));
        items.add(new ChestItem(new ItemStack(Material.DIAMOND_BOOTS, 1), 25));
        items.add(new ChestItem(new ItemStack(Material.DIAMOND_CHESTPLATE, 1), 25));
        items.add(new ChestItem(new ItemStack(Material.DIAMOND_HELMET, 1), 25));


        // Tools and weapon
        items.add(new ChestItem(new ItemStack(Material.STONE_PICKAXE, 1), 1000));
        items.add(new ChestItem(new ItemStack(Material.STONE_SWORD, 1), 2500));
        items.add(new ChestItem(new ItemStack(Material.IRON_SWORD, 1), 500));
        items.add(new ChestItem(new ItemStack(Material.DIAMOND_SWORD, 1), 50));

        // Ressources
        items.add(new ChestItem(new ItemStack(Material.IRON_INGOT), 4000, new int[]{2, 3, 4, 5, 6}));
        items.add(new ChestItem(new ItemStack(Material.DIAMOND), 50, new int[]{1,2,3}));
        items.add(new ChestItem(new ItemStack(Material.COOKED_CHICKEN), 2000, new int[]{4, 5, 6, 7, 8, 9, 10}));
        items.add(new ChestItem(new ItemStack(Material.COOKED_BEEF), 5000, new int[]{2, 3, 4, 5}));
        items.add(new ChestItem(new ItemStack(Material.EXP_BOTTLE), 1000, new int[]{5, 6, 7, 8, 9, 10, 11, 12}));
        items.add(new ChestItem(new ItemStack(Material.INK_SACK,1,(byte)4), 3000, new int[]{4, 5, 6, 7, 8}));
        items.add(new ChestItem(new ItemStack(Material.STICK), 2000, new int[]{2, 3, 4, 5}));
        items.add(new ChestItem(new ItemStack(Material.WORKBENCH, 1), 1000));
        items.add(new ChestItem(new ItemStack(Material.BOW), 1000));

        // Potions

        //Heal
        items.add(new ChestItem(new ItemStack(Material.POTION, 1 , (short)8197), 800));
        //Regeneration
        items.add(new ChestItem(new ItemStack(Material.POTION, 1 , (short)16385), 100));
        //Poison
        items.add(new ChestItem(new ItemStack(Material.POTION, 1 , (short)16388), 500));
        //Instant damage
        items.add(new ChestItem(new ItemStack(Material.POTION, 1 , (short)16396), 500));
        //Speed
        items.add(new ChestItem(new ItemStack(Material.POTION, 1 , (short)8194), 500));

        // Enchants
        ItemStack sharpness = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) sharpness.getItemMeta();
        meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 1, false);
        sharpness.setItemMeta(meta);
        ItemStack protection = new ItemStack(Material.ENCHANTED_BOOK);
        meta = (EnchantmentStorageMeta) protection.getItemMeta();
        meta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
        protection.setItemMeta(meta);
        items.add(new ChestItem(sharpness, 700));
        items.add(new ChestItem(protection, 700));
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
        items.add(new ChestItem(bow, 300));

        // Misc
        items.add(new ChestItem(new ItemStack(Material.ARROW), 3000, new int[]{3, 4, 5, 6, 7, 8, 9, 10}));
        items.add(new ChestItem(new ItemStack(Material.GOLDEN_APPLE), 500));
        items.add(new ChestItem(new ItemStack(Material.GOLDEN_APPLE, 1, (short)1), 2));
        items.add(new ChestItem(new ItemStack(Material.SHIELD, 1), 200));
        items.add(new ChestItem(new ItemStack(Material.ELYTRA, 1), 50));
        items.add(new ChestItem(new ItemStack(Material.WATER_BUCKET, 1), 100));
        items.add(new ChestItem(new ItemStack(Material.LAVA_BUCKET, 1), 50));
        items.add(new ChestItem(new ItemStack(Material.ENDER_PEARL), 100, new int[]{1, 2, 3}));
    }


}
