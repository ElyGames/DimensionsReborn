package net.samagames.dimensionsv2.game.entity.dimension;

import com.google.gson.JsonPrimitive;
import net.samagames.api.games.IGameProperties;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.dimensionsv2.game.entity.chestitem.ChestItemManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;
import java.util.Random;
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


        Location oldLoc = p.getLocation().clone().add(0,-1,0);
        Location tpTo =p.getLocation().clone().add(0.5,0,0.5);

        if (dim == Dimension.OVERWORLD)
        {
            tpTo.setX(tpTo.getX() - this.offsetX);
            tpTo.setZ(tpTo.getZ() - this.offsetZ);
            dim = Dimension.PARALLEL;
        }
        else
        {
            tpTo.setX(tpTo.getX() + this.offsetX);
            tpTo.setZ(tpTo.getZ() + this.offsetZ);
            dim = Dimension.OVERWORLD;
        }
        Location verifier =tpTo.clone().add(0,1,0);
        Block newBlock = tpTo.getBlock();
        Block newBlockUp = verifier.getBlock();
        if (this.isEmpty(newBlock) && this.isEmpty(newBlockUp))
        {
            verifier.add(0,-1,0);
            while (verifier.getBlock().isEmpty())
            {
                verifier.add(0,-1,0);
                if (verifier.getY() < 2.0)
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
            tpTo.setY(verifier.getY() + 2.0);
            p.teleport(tpTo);
            oldLoc.setY(verifier.getY());
            this.swapBlocks(oldLoc, verifier);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0));
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0));


            dp.setLastSwap(System.currentTimeMillis());
            if (dim == Dimension.OVERWORLD)
                p.sendMessage( "§2Vous êtes maintenant dans la dimension §a"  + this.overworldName);
            else
                p.sendMessage("§cVous êtes maintenant dans la dimension §4" + this.hardName);

            for(DimensionsPlayer dimPlayer : game.getInGamePlayers().values()){
                if(dimPlayer.getTarget()== dimPlayer.getPlayerIfOnline().getUniqueId()){
                    dimPlayer.setTarget(null);
                    dimPlayer.getPlayerIfOnline().sendMessage("§cVotre cible a changé de dimension, la boussole ne pointe plus personne.");
                    break;
                }
            }
        }
        else
            p.sendMessage("§cOn dirait que vous ne pouvez pas changer de dimension ici...");

    }


    private void swapBlocks(final Location loc1, final Location loc2)
    {
        Random random = Dimensions.getInstance().getGame().getRandom();
        for(int x=-2 ; x<=2;x++){
            for(int z=-2; z<=2;z++){
                for(int y=-1; y<=1;y++){
                    if(!(x==0 && z==0 && y==0)){
                        if(random.nextBoolean()){
                            Block b1 = loc1.clone().add(x,y,z).getBlock();
                            Block b2 = loc2.clone().add(x,y,z).getBlock();

                            byte data1 = b1.getData();
                            Material mat1 = b1.getType();

                            b1.setType(b2.getType());
                            b1.setData(b2.getData());

                            b2.setType(mat1);
                            b2.setData(data1);


                        }
                    }
                }
            }

        }

        ChestItemManager.getInstance().launchAndExplode(loc1, FireworkEffect.builder().withColor(new Color[]{Color.RED,Color.BLACK}).with(FireworkEffect.Type.BURST).build());
        loc1.getWorld().createExplosion((double)loc1.getBlockX(), (double)loc1.getBlockY(), (double)loc1.getBlockZ(), 1.0f, true, false);
        loc2.getWorld().createExplosion((double)loc2.getBlockX(), (double)loc2.getBlockY(), (double)loc2.getBlockZ(), 1.0f, false, false);
    }
    private boolean isEmpty(final Block block)
    {
        return block.isEmpty() || block.getType() == Material.CARPET || block.getType() == Material.SIGN || block.getType() == Material.LADDER || block.getType() == Material.SIGN_POST;
    }


}
