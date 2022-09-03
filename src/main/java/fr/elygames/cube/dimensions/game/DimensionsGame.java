package fr.elygames.cube.dimensions.game;

import com.google.gson.*;

import fr.elygames.cube.ElyAPI;
import fr.elygames.cube.dimensions.game.entity.DimensionsPlayer;
import fr.elygames.cube.dimensions.game.entity.GameStep;
import fr.elygames.cube.dimensions.game.entity.PowerUp;
import fr.elygames.cube.dimensions.game.tasks.RandomEffectsTask;
import fr.elygames.cube.dimensions.game.tasks.TimeTask;
import fr.elygames.cube.dimensions.game.tasks.TrakerTask;
import fr.elygames.cube.dimensions.game.utils.ItemUtils;
import fr.elygames.cube.dimensions.game.utils.RandomUtil;
import fr.elygames.cube.dimensions.Dimensions;
import fr.elygames.cube.game.Game;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import java.util.*;
import java.util.List;

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
public class DimensionsGame extends Game<DimensionsPlayer> {

    private List<Location> spawns;
    private List<Location> deathMatchSpawns;
    private Location waitingRoom;
    private int gameTime;
    private int pvpIn;
    private int deathMatchIn;
    private GameStep gameStep;
    private List<Material> blockPlaceWhitelist;
    private List<Material> blockBreakWhitelist;
    private Random random;
    private TimeTask timerTask;
    private Scoreboard lifeBoard;
    private RandomEffectsTask randomEffects;



    public DimensionsGame() {

        super("dimensions", "Dimensions", "", DimensionsPlayer.class,false);
        //rulesBook = new RulesBook("Dimensions");
        //rulesBook.addOwner("Tigger_San");
        //rulesBook.addPage("TODO","//TODO");

        spawns = new ArrayList<>();
        deathMatchSpawns = new ArrayList<>();
        gameTime=0;
        pvpIn = 120;
        deathMatchIn = 60;
        gameStep = GameStep.WAIT;
        blockBreakWhitelist = new ArrayList<>();
        blockPlaceWhitelist = new ArrayList<>();
        random = new Random();
        timerTask = new TimeTask();
        randomEffects = new RandomEffectsTask();

        this.lifeBoard = Dimensions.getInstance().getServer().getScoreboardManager().getNewScoreboard();

        blockPlaceWhitelist.add(Material.TNT); blockBreakWhitelist.add(Material.TNT);
        blockPlaceWhitelist.add(Material.CRAFTING_TABLE); blockBreakWhitelist.add(Material.CRAFTING_TABLE);
        blockPlaceWhitelist.add(Material.FURNACE); blockBreakWhitelist.add(Material.FURNACE);
        blockPlaceWhitelist.add(Material.CAKE); blockBreakWhitelist.add(Material.CAKE);
        // blockPlaceWhitelist.add(Material.CAKE_BLOCK); blockBreakWhitelist.add(Material.CAKE_BLOCK);
        blockBreakWhitelist.add(Material.COAL_ORE);
        blockBreakWhitelist.add(Material.DIAMOND_ORE);
        blockBreakWhitelist.add(Material.IRON_ORE);
        blockBreakWhitelist.add(Material.LAPIS_ORE);
        blockBreakWhitelist.add(Material.GOLD_ORE);

        //TODO : replace
        /*IGameProperties prop =Dimensions.getInstance().getApi().getGameManager().getGameProperties();

        prop.getMapProperty("spawns",new JsonArray()).getAsJsonArray().forEach(elt ->  spawns.add(LocationUtils.str2loc(elt.getAsString())));
        Collections.shuffle(spawns);

        prop.getMapProperty("allowBreak",new JsonArray()).getAsJsonArray().forEach(elt ->  blockBreakWhitelist.add(Material.matchMaterial(elt.getAsString())));
        prop.getMapProperty("deathMatchSpawns",new JsonArray()).getAsJsonArray().forEach(elt -> deathMatchSpawns.add(LocationUtils.str2loc(elt.getAsString())));

        Collections.shuffle(deathMatchSpawns);
        deleteChests(prop);

        waitingRoom = LocationUtils.str2loc(prop.getMapProperty("waitingRoom",new JsonPrimitive("world, 0, 0, 0, 0, 0")).getAsString());*/
        new TrakerTask().runTaskTimer(Dimensions.getInstance(),1L,10L);
    }

    /**
     * Start the deathmatch
     */
    public void startDeathmatch(){
       gameStep = GameStep.DEATHMATCH;
       Iterator<Location> it = deathMatchSpawns.iterator();
        playSound(Sound.ENTITY_ENDER_DRAGON_GROWL,1F);
       getPlayers().values().forEach(
               p -> {
                   p.getPlayerIfOnline().setVelocity(new Vector(0,0,0));
                   p.getPlayerIfOnline().resetPlayerTime();
                   Arrays.asList(p.getPlayerIfOnline().getInventory().getContents()).stream().filter(i -> (i!=null && (i.getType().equals(ItemUtils.getTargetItem(p.getPlayerIfOnline()).getType()) ||  i.getType().equals(ItemUtils.getSwapItem().getType())))).forEach(i -> p.getPlayerIfOnline().getInventory().remove(i));
                   p.getPlayerIfOnline().teleport(it.next());
               }
       );

    }

    /**
     * Called when a player is damaged by a player
     * @param p The damages receiver
     * @param damager The damager
     */
    public void playerDamageByPlayer(Player p  , Player damager){

        DimensionsPlayer hitPlayer = getPlayer(p.getUniqueId());
        hitPlayer.setLastDamager(damager.getUniqueId());

        int healAtStrike = hitPlayer.getValueForPowerUP(PowerUp.HEAL_AT_STRIKE);
            new BukkitRunnable()
            {
                @Override
                public void run() {
                    if (RandomUtil.pickBoolean(random,healAtStrike))
                    {
                        double h = p.getHealth() + 2.0;
                        if (h > p.getMaxHealth()){
                            h = p.getMaxHealth();
                        }
                        p.setHealth(h);
                        p.sendMessage("§6§oUne fée vous a restauré un coeur !");
                    }
                }
            }.runTaskLater(Dimensions.getInstance(),5L);
    }

    /**
     * Called when a player die
     * @param p The player
     */
    public void die(Player p){
        //((DimensionsStatistics) SamaGamesAPI.get().getGameManager().getGameStatisticsHelper()).increaseDeaths(p.getUniqueId());
        DimensionsPlayer killer = getPlayer(getPlayer(p.getUniqueId()).getLastDamager());
        if (killer==null){
            ElyAPI.getChatManager().sendGlobalMessage("§e" + p.getDisplayName() +" §ea été éliminé sans aide extérieure.");
        }
        else
        {
            ElyAPI.getChatManager().sendGlobalMessage("§e" + p.getDisplayName() + " §e a été tué par " + killer.getPlayerIfOnline().getDisplayName() + "§e.");
            killer.addKill();
           // ((DimensionsStatistics) SamaGamesAPI.get().getGameManager().getGameStatisticsHelper()).increaseKills(killer.getUUID());
            //addCoins(killer.getPlayerIfOnline(), 20, "Un joueur tué !");

            if (killer.getPlayerIfOnline().getHealth() >= 1.0)
             {
                 int healAtKill = killer.getValueForPowerUP(PowerUp.HEAL_AT_KILL);
                 double health = killer.getPlayerIfOnline().getHealth() + healAtKill;
                 if (health > killer.getPlayerIfOnline().getMaxHealth()){
                     health = killer.getPlayerIfOnline().getMaxHealth();
                 }
                 killer.getPlayerIfOnline().setHealth(health);
                 int strengthAtKill = killer.getValueForPowerUP(PowerUp.STRENGHT_AT_KILL);
                 killer.getPlayerIfOnline().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * strengthAtKill, 1));
                 killer.getPlayerIfOnline().sendMessage("§6§oUne fée vous a restauré " + healAtKill/2 + "§c§o ♥§6§o et vous a donné §c§o"+ strengthAtKill+ " secondes §6§ode force." );
             }
        }
    }


    public boolean isSpectator(DimensionsPlayer dp){
        return this.getSpectators().containsKey(dp.getPlayerIfOnline().getUniqueId());

    }


    @Override
    public void playerLogout(Player player)
    {
        DimensionsPlayer dp = getPlayer(player.getUniqueId());
        super.playerLogout(player);
            if((!isNonGameStep() || gameStep== GameStep.PRE_TELEPORT) && dp !=null && !isSpectator(dp)){
                stumpPlayer(player,true);
            }
    }


   /* public void deleteChests(IGameProperties prop){
        List<Location> chests = new ArrayList<>();
        prop.getMapProperty("chestsOverworld",new JsonArray()).getAsJsonArray().forEach(elt -> chests.add(LocationUtils.str2loc(elt.getAsString())));
        Collections.shuffle(chests);

        for(int i=0; i< (chests.size()/2); i++){
            chests.get(i).getBlock().setType(Material.AIR);
        }

        chests.clear();
        prop.getMapProperty("chestsParallel",new JsonArray()).getAsJsonArray().forEach(elt -> chests.add(LocationUtils.str2loc(elt.getAsString())));
        Collections.shuffle(chests);
        for(int i=0; i< (chests.size()/2); i++){
            chests.get(i).getBlock().setType(Material.AIR);
        }
    }*/

    @Override
    public void playerLogin(Player player){
        player.setScoreboard(this.lifeBoard);
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(waitingRoom);
        player.setHealth(20D);
        player.getInventory().clear();
        player.setFoodLevel(20);
        //player.getInventory().setItem(4,rulesBook.toItemStack());
        super.playerLogin(player);
    }

    public List<Location> getDeathMatchSpawns() {
        return deathMatchSpawns;
    }

    public Random getRandom() {
        return random;
    }

    /**
     * Called when a player loose
     * @param p The player
     * @param logout If the player logour
     */
    public void stumpPlayer(Player p, boolean logout){

        playSound(Sound.ENTITY_WITHER_BREAK_BLOCK,1F);

        for(DimensionsPlayer dimPlayer : getPlayers().values()){

            if(dimPlayer.getTarget()== p.getUniqueId()){
                dimPlayer.setTarget(null);
                dimPlayer.getPlayerIfOnline().sendMessage("§cVotre cible a disparu du jeu, la boussole ne pointe plus personne.");
            }
        }

        //setSpectator(p);
        DimensionsPlayer dp = getPlayer(p.getUniqueId());

        int left = getPlayers().values().size();
        if(!logout){
            p.resetPlayerTime();
            if ((!dp.getPlayerIfOnline().getUniqueId().equals(dp.getLastDamager())) || dp.getLastDamager()==null) {
                if (left == 2) {
                    //addCoins(p, 20, "Troisième !");
                } else if (left == 1) {
                   // addCoins(p, 40, "Second !");
                }
            }
        }
        else{
            ElyAPI.getChatManager().sendGlobalMessage(p.getDisplayName() + " s'est déconnecté.");
        }


            if (left!=1) {
                ElyAPI.getChatManager().sendGlobalMessage("§eIl reste encore §b" + + left + " §ejoueurs en vie.");
                if( pvpIn<= 0&& gameStep!=GameStep.DEATHMATCH_PLANNED && getPlayers().size()<=deathMatchSpawns.size()){
                    gameStep= GameStep.DEATHMATCH_PLANNED;
                }
            }
             else {
                end();
            }
        }


    /**
     * Called when the game finish
     */
    public void end(){
        gameStep = GameStep.FINISH;
        try{
            Bukkit.getServer().getScheduler().runTaskLater(Dimensions.getInstance(),() ->  timerTask.cancel(),30L);
        }
        catch (Exception ignored){}
        DimensionsPlayer winner = getPlayers().values().iterator().next();
        //Titles.sendTitle(winner.getPlayerIfOnline(), 5, 80, 5,"§6Victoire !","§aVous gagnez la partie en §a" +  + winner.getKills() + " §akills !");

        //Bukkit.getServer().getOnlinePlayers().stream().filter(p -> !p.equals(winner.getPlayerIfOnline())).forEach(p ->
            //Titles.sendTitle(p, 5, 80, 5, ChatColor.GOLD + "Fin de partie !", ChatColor.GREEN + "Bravo à " + winner.getPlayerIfOnline().getDisplayName())
        //);
        //this.coherenceMachine.getTemplateManager().getPlayerWinTemplate().execute(winner.getPlayerIfOnline(), winner.getKills());
        //addCoins(winner.getPlayerIfOnline(), 60, "Victoire !");
        //this.effectsOnWinner(winner.getPlayerIfOnline());
        winner(winner.getPlayerIfOnline().getUniqueId());
        //handleGameEnd();

    }



    @Override
    public void start()
    {
        super.start();
        this.lifeBoard.registerNewObjective("vie", "health").setDisplaySlot(DisplaySlot.BELOW_NAME);
        this.lifeBoard.getObjective("vie").setDisplayName(ChatColor.RED + "♥");
        gameStep = GameStep.PRE_TELEPORT;
        int index=0;
        for(DimensionsPlayer dp : getPlayers().values()){
            dp.getPlayerIfOnline().setGameMode(GameMode.SURVIVAL);
            dp.getPlayerIfOnline().teleport(spawns.get(index).clone().add(0,1,0));
            this.lifeBoard.getObjective("vie").getScore(dp.getPlayerIfOnline().getName()).setScore(20);
            index++;
        }

        ElyAPI.getChatManager().sendGlobalMessage("§6Préparation du jeu ! ");
        new BukkitRunnable(){
            int i =15;
            @Override
            public void run() {
                String secFormat = "secondes";
                if(i==1){
                    secFormat = "seconde";
                }
                setXp(i);
                switch(i){
                    case 15: ElyAPI.getChatManager().sendGlobalMessage("§eDémarrage du jeu dans §c" + i + " " + secFormat + "§e."); break;
                    case 10: case 5 : case 4 : case 3 : case 2 : case 1: playSound(Sound.BLOCK_NOTE_BLOCK_PLING,1.0F);sendTitle("§6Démarrage dans §c" + i + " §6sec.","§6Préparez vous au combat !"); break;
                    case 0 : playSound(Sound.BLOCK_NOTE_BLOCK_PLING,2.0F);begin();this.cancel(); break;

                }
                i--;
            }

        }.runTaskTimer(Dimensions.getInstance(),2L,20L );
    }

    public void setXp(int level){
        this.getPlayers().values().forEach(dp ->    dp.getPlayerIfOnline().setLevel(level) );
    }

    public void sendTitle(String title,String subTitle){
       // this.getPlayers().values().forEach(dp ->   Titles.sendTitle(dp.getPlayerIfOnline(),5,50,5,title,subTitle));
    }

    public void sendActionBar(String message){
     //  this.getInGamePlayers().values().forEach(dp ->  ActionBarAPI.sendMessage(dp.getPlayerIfOnline(),message));
    }

    /**
     * Called when the game begin
     */
    public void begin(){

        this.randomEffects.runTaskTimerAsynchronously(Dimensions.getInstance(),1L,20L);
        gameStep= GameStep.IN_GAME;
        timerTask.runTaskTimer(Dimensions.getInstance(),1L,20L);
        ElyAPI.getChatManager().sendGlobalMessage("§6La partie commence. Bonne chance !");
        ElyAPI.getChatManager().sendGlobalMessage("§6Le PVP sera activé dans 2 minutes  !");
        for(DimensionsPlayer dp : getPlayers().values()){
            dp.getPlayerIfOnline().getInventory().clear();
            dp.getPlayerIfOnline().setVelocity(new Vector(0,0,0));
            dp.getPlayerIfOnline().setGameMode(GameMode.SURVIVAL);
            dp.getPlayerIfOnline().getInventory().setItem(8, ItemUtils.getSwapItem());
            dp.getPlayerIfOnline().getInventory().setItem(7, ItemUtils.getTargetItem(dp.getPlayerIfOnline()));
        }

    }

    /**
     * Play sound for every players in the game
     * @param sound The souns
     * @param pitch The pitch of the sound
     */
    public void playSound(Sound sound,float pitch){
        for(DimensionsPlayer dp : this.getPlayers().values()){
            dp.getPlayerIfOnline().playSound(dp.getPlayerIfOnline().getLocation(),sound,1000F,pitch);
        }
    }

    public List<Material> getBlockPlaceWhitelist() {
        return blockPlaceWhitelist;
    }

    public List<Material> getBlockBreakWhitelist() {
        return blockBreakWhitelist;
    }

    public int getDeathMatchIn() {
        return deathMatchIn;
    }

    /**
     * Check if it's a non-game GameStep
     * @return Yes or no
     */
    public boolean isNonGameStep(){

        return(gameStep==GameStep.WAIT || gameStep==GameStep.PRE_TELEPORT  || gameStep==GameStep.FINISH);
    }

    /**
     * Check if it's a non-pvp-active GameStep
     * @return Yes or no
     */
    public boolean isNonPVPActive(){

        return(gameStep==GameStep.WAIT || gameStep==GameStep.PRE_TELEPORT  || gameStep==GameStep.FINISH || gameStep==GameStep.IN_GAME);
    }

    public void decreaseDeathmatchIn(){
        deathMatchIn--;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void increaseGameTime() {
       gameTime++;
    }

    public int getPvpIn() {
        return pvpIn;
    }

    public void decreasePvpIn(){
        pvpIn--;
    }

    public GameStep getDimensionsGameStep() {
        return gameStep;
    }

    public void setGameStep(GameStep gameStep) {
        this.gameStep = gameStep;
    }

}
