package net.samagames.dimensionsv2.game.entity.dimension;

import com.google.gson.JsonPrimitive;
import net.samagames.api.games.IGameProperties;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.tools.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tigger_San on 26/04/2017.
 */
public class DimensionsManager {
    private static DimensionsManager ourInstance = new DimensionsManager();

    public static DimensionsManager getInstance() {
        return ourInstance;
    }


    private int offsetX;
    private int offsetZ;
    private String overworldName;
    private String hardName;


    private DimensionsManager() {
        IGameProperties prop = Dimensions.getInstance().getApi().getGameManager().getGameProperties();
        this.offsetX =   prop.getConfig("offsetX",new JsonPrimitive("100")).getAsInt();
        this.offsetZ =   prop.getConfig("offsetZ",new JsonPrimitive("100")).getAsInt();
        this.overworldName = prop.getConfig("overworldName",new JsonPrimitive("No set")).getAsString();
        this.hardName = prop.getConfig("parallelName",new JsonPrimitive("No set")).getAsString();
    }



    public List<DimensionsPlayer> getPlayersInDimension(Dimension dim)
    {
        return Dimensions.getInstance().getGame().getInGamePlayers().values().stream().filter(pl ->  pl.getDimension() == dim).collect(Collectors.toList());
    }


    public void swap(Player p)
    {
        DimensionsGame game = Dimensions.getInstance().getGame();
        DimensionsPlayer dp = game.getPlayer(p.getUniqueId());

        if ( game.isNonGameStep()|| game.getGameStep() == GameStep.DEATHMATCH)
            return ;
        int nextSwap =dp.getNextSwapDelay();
        if (nextSwap>0)
        {
            p.sendMessage("§cMerci d'attendre " +nextSwap + " seconde(s) avant de changer de dimension.");
            return ;
        }
        Dimension dim = dp.getDimension();


        Location oldLoc = new Location(p.getLocation().getWorld(), p.getLocation().getX(), p.getLocation().getY() - 1.0, p.getLocation().getZ());
        Location tpTo = new Location(p.getLocation().getWorld(), p.getLocation().getBlockX() + 0.5, (double)p.getLocation().getBlockY(), p.getLocation().getBlockZ() + 0.5, p.getLocation().getYaw(), p.getLocation().getPitch());
        Location tpToWork = new Location(p.getLocation().getWorld(), p.getLocation().getBlockX() + 0.5, (double)(p.getLocation().getBlockY() + 1), p.getLocation().getBlockZ() + 0.5, p.getLocation().getYaw(), p.getLocation().getPitch());
        if (dim == Dimension.OVERWORLD)
        {
            tpTo.setX(tpTo.getX() - this.offsetX);
            tpToWork.setX(tpToWork.getX() - this.offsetX);

            tpTo.setZ(tpTo.getZ() - this.offsetZ);
            tpToWork.setZ(tpToWork.getZ() - this.offsetZ);

            dim = Dimension.PARALLEL;
        }
        else
        {
            tpTo.setX(tpTo.getX() + this.offsetX);
            tpToWork.setX(tpToWork.getX() + this.offsetX);

            tpTo.setZ(tpTo.getZ() + this.offsetZ);
            tpToWork.setZ(tpToWork.getZ() + this.offsetZ);

            dim = Dimension.OVERWORLD;
        }
        Block b = tpTo.getBlock();
        Block up = tpToWork.getBlock();
        if (this.isEmpty(b) && this.isEmpty(up))
        {
            tpToWork.setY(tpToWork.getY() - 1.0);
            while (tpToWork.getBlock().isEmpty())
            {
                tpToWork.setY(tpToWork.getY() - 1.0);
                if (tpToWork.getY() < 2.0)
                {
                    p.sendMessage( "§cOn dirait que vous ne pouvez pas changer de dimension ici...");
                    return ;
                }
            }
            dp.setDimension(dim);
            if (dim == Dimension.OVERWORLD)
                p.setPlayerTime(6000L, false);
            else
                p.setPlayerTime(17000L, false);
            tpTo.setY(tpToWork.getY() + 1.0);
            p.teleport(tpTo);
            oldLoc.setY(tpToWork.getY());
            this.swapBlocks(oldLoc, tpToWork);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0));
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0));


            dp.setLastSwap(System.currentTimeMillis());
            // Effets swag //
            if (dim == Dimension.OVERWORLD)
                p.sendMessage( "§2Vous êtes maintenant dans la dimension §a"  + this.overworldName);
            else
                p.sendMessage("§cVous êtes maintenant dans la dimension §4" + this.hardName);
        }
        else
            p.sendMessage("§cOn dirait que vous ne pouvez pas changer de dimension ici...");
    }


    private void swapBlocks(final Location loc1, final Location loc2)
    {
        final Location[] locations1 = { loc1, new Location(loc1.getWorld(), loc1.getX(), loc1.getY(), loc1.getZ() - 2.0), new Location(loc1.getWorld(), loc1.getX(), loc1.getY(), loc1.getZ() - 1.0), new Location(loc1.getWorld(), loc1.getX(), loc1.getY(), loc1.getZ() + 1.0), new Location(loc1.getWorld(), loc1.getX(), loc1.getY(), loc1.getZ() + 2.0), new Location(loc1.getWorld(), loc1.getX() - 2.0, loc1.getY(), loc1.getZ()), new Location(loc1.getWorld(), loc1.getX() - 1.0, loc1.getY(), loc1.getZ()), new Location(loc1.getWorld(), loc1.getX() + 1.0, loc1.getY(), loc1.getZ()), new Location(loc1.getWorld(), loc1.getX() + 2.0, loc1.getY(), loc1.getZ()), new Location(loc1.getWorld(), loc1.getX() + 1.0, loc1.getY(), loc1.getZ() + 1.0), new Location(loc1.getWorld(), loc1.getX() + 1.0, loc1.getY(), loc1.getZ() - 1.0), new Location(loc1.getWorld(), loc1.getX() - 1.0, loc1.getY(), loc1.getZ() - 1.0), new Location(loc1.getWorld(), loc1.getX() - 1.0, loc1.getY(), loc1.getZ() + 1.0) };
        final Location[] locations2 = { loc2, new Location(loc2.getWorld(), loc2.getX(), loc2.getY(), loc2.getZ() - 2.0), new Location(loc2.getWorld(), loc2.getX(), loc2.getY(), loc2.getZ() - 1.0), new Location(loc2.getWorld(), loc2.getX(), loc2.getY(), loc2.getZ() + 1.0), new Location(loc2.getWorld(), loc2.getX(), loc2.getY(), loc2.getZ() + 2.0), new Location(loc2.getWorld(), loc2.getX() - 2.0, loc2.getY(), loc2.getZ()), new Location(loc2.getWorld(), loc2.getX() - 1.0, loc2.getY(), loc2.getZ()), new Location(loc2.getWorld(), loc2.getX() + 1.0, loc2.getY(), loc2.getZ()), new Location(loc2.getWorld(), loc2.getX() + 2.0, loc2.getY(), loc2.getZ()), new Location(loc2.getWorld(), loc2.getX() + 1.0, loc2.getY(), loc2.getZ() + 1.0), new Location(loc2.getWorld(), loc2.getX() + 1.0, loc2.getY(), loc2.getZ() - 1.0), new Location(loc2.getWorld(), loc2.getX() - 1.0, loc2.getY(), loc2.getZ() - 1.0), new Location(loc2.getWorld(), loc2.getX() - 1.0, loc2.getY(), loc2.getZ() + 1.0) };
        final List<Location> loc2list = Arrays.asList(locations2);
        final Iterator<Location> iter = loc2list.iterator();
        for (final Location l : locations1)
        {
            Block block1 = l.getBlock();
            Block block2 = iter.next().getBlock();
            Material trans = block1.getType();
            byte data = block1.getData();
            block1.setType(block2.getType());
            block1.setData(block2.getData());
            block2.setType(trans);
            block2.setData(data);
        }
        loc1.getWorld().createExplosion((double)loc1.getBlockX(), (double)loc1.getBlockY(), (double)loc1.getBlockZ(), 1.0f, true, false);
        loc2.getWorld().createExplosion((double)loc2.getBlockX(), (double)loc2.getBlockY(), (double)loc2.getBlockZ(), 1.0f, false, false);
    }
    private boolean isEmpty(final Block block)
    {
        return block.isEmpty() || block.getType() == Material.CARPET || block.getType() == Material.SIGN || block.getType() == Material.LADDER || block.getType() == Material.SIGN_POST;
    }


}
