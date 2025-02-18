package fr.elygames.cube.dimensions.game.entity.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fr.elygames.cube.dimensions.Dimensions;
import fr.elygames.cube.dimensions.game.DimensionsGame;
import fr.elygames.cube.dimensions.game.entity.DimensionsPlayer;
import fr.elygames.cube.dimensions.game.entity.GameStep;
import fr.elygames.cube.dimensions.game.entity.chestitem.ChestItemManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/*
 * This file is part of DimensionsV2.
 *
 * DimensionsV2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DimensionsV2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DimensionsV2.  If not, see <http://www.gnu.org/licenses/>.
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

        // TODO : How to do with elyapi
        /*IGameProperties prop =Dimensions.getInstance().getApi().getGameManager().getGameProperties();

        for(JsonElement elt : prop.getMapProperty("enchantingTablesOverworld",new JsonArray()).getAsJsonArray()){
            overworldEnchantings.add(LocationUtils.str2loc(elt.getAsString()));
        }
        for(JsonElement elt : prop.getMapProperty("anvilsOverworld",new JsonArray()).getAsJsonArray()){
            overworldAnvils.add(LocationUtils.str2loc(elt.getAsString()));
        }
        for(JsonElement elt : prop.getMapProperty("enchantingTablesParallel",new JsonArray()).getAsJsonArray()){
            parallelEnchantings.add(LocationUtils.str2loc(elt.getAsString()));
        }
        for(JsonElement elt : prop.getMapProperty("anvilsParallel",new JsonArray()).getAsJsonArray()){
            paralleldAnvils.add(LocationUtils.str2loc(elt.getAsString()));
        }

        this.offsetX =   prop.getMapProperty("offsetX",new JsonPrimitive("100")).getAsInt();
        this.offsetZ =   prop.getMapProperty("offsetZ",new JsonPrimitive("100")).getAsInt();
        this.overworldName = prop.getMapProperty("overworldName",new JsonPrimitive("No set")).getAsString();
        this.hardName = prop.getMapProperty("parallelName",new JsonPrimitive("No set")).getAsString();
        this.overworldLootTable = prop.getMapProperty("overworldLootTable",new JsonPrimitive("sg:dimensions/dim_normal")).getAsString();
        this.parallelLootTable = prop.getMapProperty("parallelLootTable",new JsonPrimitive("sg:dimensions/dim_parallel")).getAsString();*/
    }

    /**
     * Get a list of player in a specific dimension
     * @param dim The dimension
     * @return The list of player in the dimension
     */
    public List<DimensionsPlayer> getPlayersInDimension(Dimension dim)
    {
        return Dimensions.getInstance().getGame().getPlayers().values().stream().filter(pl ->  pl.getDimension() == dim).collect(Collectors.toList());
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
        return new ArrayList<>();
    }

    public List<Location> getEnchanting(Dimension dim){
        switch (dim){
            case OVERWORLD: return overworldEnchantings;
            case PARALLEL: return paralleldAnvils;
        }
        return new ArrayList<>();
    }

    /**
     * Change dimension of a plyer
     * @param p The player
     */
    public void swap(Player p)
    {
        DimensionsGame game = Dimensions.getInstance().getGame();
        DimensionsPlayer dp = game.getPlayer(p.getUniqueId());

        if ( game.isNonGameStep()|| game.getDimensionsGameStep() == GameStep.DEATHMATCH)
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
        //Location for verifications
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
            if (dim == Dimension.OVERWORLD){
                p.setPlayerTime(6000L, false);
            }
            else{
                p.setPlayerTime(17000L, false);
            }
            tpTo.setY(verifier.getY() + 2.0);
            p.teleport(tpTo);
            oldLoc.setY(verifier.getY());
            this.swapBlocks(oldLoc, verifier);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
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


            for(DimensionsPlayer dimPlayer : game.getPlayers().values()){
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
                        //Choose randomly for better effects
                        if(random.nextBoolean()){
                            Block b1 = loc1.clone().add(x,y,z).getBlock();
                            Block b2 = loc2.clone().add(x,y,z).getBlock();
                            if(!(b1.getType() == Material.ANVIL || b1.getType() == Material.ENCHANTING_TABLE ||
                                    b2.getType() == Material.ANVIL || b2.getType() == Material.ENCHANTING_TABLE )){

                                byte data1 = b1.getData();
                                Material mat1 = b1.getType();

                                b1.setType(b2.getType());
                                //TODO : check how to do now
                                //b1.setData(b2.getData());

                                b2.setType(mat1);
                                //b2.setData(data1);

                            }
                        }
                    }
                }
            }

        }

        ChestItemManager.getInstance().launchAndExplode(loc1, FireworkEffect.builder().withColor(Color.RED,Color.BLACK).with(FireworkEffect.Type.BURST).build());
        loc1.getWorld().createExplosion((double)loc1.getBlockX(), (double)loc1.getBlockY(), (double)loc1.getBlockZ(), 1.0f, true, false);
        loc2.getWorld().createExplosion((double)loc2.getBlockX(), (double)loc2.getBlockY(), (double)loc2.getBlockZ(), 1.0f, false, false);
    }

    /**
     * Vérify if a block is empty
     * @param block The block
     * @return Is empty or not
     */
    private boolean isEmpty(final Block block)
    {
        return block.isEmpty() || block.getType().toString().toLowerCase().contains("carpet")|| block.getType().toString().toLowerCase().contains("sign") || block.getType() == Material.LADDER;
    }


}
