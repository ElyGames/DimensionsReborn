package net.samagames.dimensionsv2.game.entity.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.samagames.api.games.IGameProperties;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.dimensionsv2.game.entity.chestitem.ChestItemManager;
import net.samagames.tools.LocationUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Manage dimensions and playuer swag
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
    private String overworldLootTable;
    private String parallelLootTable;
    private List<Location> overworldEnchantings;
    private List<Location> overworldAnvils;
    private List<Location> parallelEnchantings;
    private List<Location> paralleldAnvils;

    private DimensionsManager() {
        overworldEnchantings = new ArrayList<>();
        overworldAnvils = new ArrayList<>();
        parallelEnchantings = new ArrayList<>();
        paralleldAnvils = new ArrayList<>();

        IGameProperties prop =Dimensions.getInstance().getApi().getGameManager().getGameProperties();

        for(JsonElement elt : prop.getConfig("enchantingTablesOverworld",new JsonArray()).getAsJsonArray()){
            overworldEnchantings.add(LocationUtils.str2loc(elt.getAsString()));
        }
        for(JsonElement elt : prop.getConfig("anvilsOverworld",new JsonArray()).getAsJsonArray()){
            overworldAnvils.add(LocationUtils.str2loc(elt.getAsString()));
        }
        for(JsonElement elt : prop.getConfig("enchantingTablesParallel",new JsonArray()).getAsJsonArray()){
            parallelEnchantings.add(LocationUtils.str2loc(elt.getAsString()));
        }
        for(JsonElement elt : prop.getConfig("anvilsParallel",new JsonArray()).getAsJsonArray()){
            paralleldAnvils.add(LocationUtils.str2loc(elt.getAsString()));
        }

        this.offsetX =   prop.getConfig("offsetX",new JsonPrimitive("100")).getAsInt();
        this.offsetZ =   prop.getConfig("offsetZ",new JsonPrimitive("100")).getAsInt();
        this.overworldName = prop.getConfig("overworldName",new JsonPrimitive("No set")).getAsString();
        this.hardName = prop.getConfig("parallelName",new JsonPrimitive("No set")).getAsString();
        this.overworldLootTable = prop.getConfig("overworldLootTable",new JsonPrimitive("sg:dimensions/dim_normal")).getAsString();
        this.parallelLootTable = prop.getConfig("parallelLootTable",new JsonPrimitive("sg:dimensions/dim_parallel")).getAsString();
    }

    /**
     * Get a list of player in a specific dimension
     * @param dim The dimension
     * @return The list of player in the dimension
     */
    public List<DimensionsPlayer> getPlayersInDimension(Dimension dim)
    {
        return Dimensions.getInstance().getGame().getInGamePlayers().values().stream().filter(pl ->  pl.getDimension() == dim).collect(Collectors.toList());
    }

    public String getOverworldLootTable() {
        return overworldLootTable;
    }

    public String getParallelLootTable() {
        return parallelLootTable;
    }

    public List<Location> getAnvils(Dimension dim){
        switch (dim){
            case OVERWORLD: return overworldAnvils;
            case PARALLEL: return paralleldAnvils;
        }
        return null;
    }

    public List<Location> getEnchanting(Dimension dim){
        switch (dim){
            case OVERWORLD: return overworldEnchantings;
            case PARALLEL: return paralleldAnvils;
        }
        return null;
    }

    /**
     * Change dimension of a plyer
     * @param p The player
     */
    public void swap(Player p)
    {
        DimensionsGame game = Dimensions.getInstance().getGame();
        DimensionsPlayer dp = game.getPlayer(p.getUniqueId());

        if ( game.isNonGameStep()|| game.getGameStep() == GameStep.DEATHMATCH)
            return ;
        int nextSwap =dp.getNextSwapDelay();
        if (nextSwap>0)
        {
            p.sendMessage("§eMerci d'attendre §c" +nextSwap + " seconde(s) §eavant de changer de dimension.");
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
            if (dim == Dimension.OVERWORLD){
                p.sendMessage( "§2Vous êtes maintenant dans la dimension §a"  + this.overworldName);
            }
            else{
                p.sendMessage("§cVous êtes maintenant dans la dimension §4" + this.hardName);
            }
            if(dp.getTarget()!=null){
                dp.setTarget(null);
                p.sendMessage("§cVous avez changé de dimension, la boussole ne pointe plus personne.");
            }
            else if(dp.getTargetLoc()!=null){
                dp.setTargetLoc(null);
                p.sendMessage("§cVous avez changé de dimension, la boussole ne pointe plus rien.");
            }


            for(DimensionsPlayer dimPlayer : game.getInGamePlayers().values()){
                if(dimPlayer.getTarget()== p.getUniqueId()){
                    dimPlayer.setTarget(null);
                    dimPlayer.getPlayerIfOnline().sendMessage("§cVotre cible a changé de dimension, la boussole ne pointe plus personne.");
                }
            }
        }
        else
            p.sendMessage("§cOn dirait que vous ne pouvez pas changer de dimension ici...");

    }


    /**
     * Swap random nea
     * @param loc1
     * @param loc2
     */
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
