package net.samagames.dimensionsv2.game.utils;

import net.minecraft.server.v1_12_R1.EntityFireworks;
import net.minecraft.server.v1_12_R1.World;
import net.samagames.dimensionsv2.Dimensions;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;


import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFirework;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * Created by BlueSlime on 28/06/2017.
 */
public class FireworkUtils
{
    public static void launchfw(Location location, final FireworkEffect effect)
    {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(effect);
        fwm.setPower(0);
        fw.setFireworkMeta(fwm);
        ((CraftFirework) fw).getHandle().setInvisible(true);

        Dimensions.getInstance().getServer().getScheduler().runTaskLater(Dimensions.getInstance(), () ->
        {
            World world = (((CraftWorld) location.getWorld()).getHandle());
            EntityFireworks fireworks = ((CraftFirework) fw).getHandle();
            world.broadcastEntityEffect(fireworks, (byte) 17);
            fireworks.die();
        }, 1);
    }
}
