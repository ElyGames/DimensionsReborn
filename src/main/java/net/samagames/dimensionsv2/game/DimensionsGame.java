package net.samagames.dimensionsv2.game;

import com.google.gson.*;
import net.samagames.api.games.Game;
import net.samagames.api.games.IGameProperties;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.dimensionsv2.game.entity.PowerUp;
import net.samagames.dimensionsv2.game.tasks.TimeTask;
import net.samagames.dimensionsv2.game.utils.RandomUtil;
import net.samagames.tools.LocationUtils;
import net.samagames.tools.Titles;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Tigger_San on 21/04/2017.
 */
public class DimensionsGame extends Game<DimensionsPlayer>{


    private List<Location> spawns;
    private List<Location> deathMatchSpawns;
    private Location waitingRoom;
    private int gameTime;
    private int pvpIn;
    private int deathMatchIn;
    private GameStep gameStep;
    private List<Material> blockPlaceAndBreakWhitelist;
    private Random random;



    public DimensionsGame() {
        super("dimensions", "Dimensions", "", DimensionsPlayer.class);
        spawns = new ArrayList<>();
        deathMatchSpawns = new ArrayList<>();
        gameTime=0;
        pvpIn = 120;
        deathMatchIn = 60;
        gameStep = GameStep.WAIT;
        blockPlaceAndBreakWhitelist = new ArrayList<>();
        random = new Random();

        blockPlaceAndBreakWhitelist.add(Material.TNT);
        blockPlaceAndBreakWhitelist.add(Material.WORKBENCH);
        blockPlaceAndBreakWhitelist.add(Material.FURNACE);
        blockPlaceAndBreakWhitelist.add(Material.CAKE);
        blockPlaceAndBreakWhitelist.add(Material.CAKE_BLOCK);


        IGameProperties prop =Dimensions.getInstance().getApi().getGameManager().getGameProperties();

        for(JsonElement elt : prop.getConfig("spawns",new JsonArray()).getAsJsonArray()){
            spawns.add(LocationUtils.str2loc(elt.getAsString()));
        }
        Collections.shuffle(spawns);

        for(JsonElement elt : prop.getConfig("deathMatchSpawns",new JsonArray()).getAsJsonArray()){
            deathMatchSpawns.add(LocationUtils.str2loc(elt.getAsString()));
        }

        waitingRoom = LocationUtils.str2loc(prop.getConfig("waitingRoom",new JsonPrimitive("world, 0, 0, 0, 0, 0")).getAsString());
        Collections.shuffle(deathMatchSpawns);

    }


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

    public void die(Player p){
        DimensionsPlayer killer = getPlayer(getPlayer(p.getUniqueId()).getLastDamager());
        if (killer==null){
            getCoherenceMachine().getMessageManager().writeCustomMessage("§c" + p.getDisplayName() +"a été éliminé sans aide extérieure.",true);
        }
        else
        {
            getCoherenceMachine().getMessageManager().writeCustomMessage("§c" + p.getDisplayName() + "§c a été tué par " + killer.getPlayerIfOnline().getDisplayName() + "§c.",true);
            killer.addKill();
            addCoins(killer.getPlayerIfOnline(), 20, "Un joueur tué !");

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
             }
        }
    }

    @Override
    public void handleLogout(Player player)
    {
        stumpPlayer(player);
        super.handleLogout(player);
    }
    @Override
    public void handleLogin(Player player){
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(waitingRoom);
        player.setHealth(20D);
        player.setFoodLevel(20);
        super.handleLogin(player);
    }

    public void stumpPlayer(Player p){

        if(gameStep!=GameStep.DEATHMATCH_PLANNED && deathMatchSpawns.size()==getInGamePlayers().size()){
           gameStep= GameStep.DEATHMATCH_PLANNED;
        }
        setSpectator(p);
        //We check if player doesn't suicide
    }

    @Override
    public void startGame()
    {
        super.startGame();
        gameStep = GameStep.PRE_TELEPORT;
        int index=0;
        for(DimensionsPlayer dp : getInGamePlayers().values()){
            dp.getPlayerIfOnline().teleport(spawns.get(index));
            dp.getPlayerIfOnline().setGameMode(GameMode.SURVIVAL);
            index++;
        }


        getCoherenceMachine().getMessageManager().writeCustomMessage("§6Préparation du jeu ! ",true);
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
                    case 15: getCoherenceMachine().getMessageManager().writeCustomMessage("§eDémarrage du jeu dans §c" + i + " " + secFormat + "§e.",true); break;
                    case 10: case 5 : case 4 : case 3 : case 2 : case 1: playSound(Sound.BLOCK_NOTE_PLING,1.0F);sendTitle("§6Démarrage dans §c" + i + "§6sec.","§6Préparez vous au combat !"); break;
                    case 0 : playSound(Sound.BLOCK_NOTE_PLING,2.0F);begin();this.cancel(); break;

                }
                i--;
            }

        }.runTaskTimer(Dimensions.getInstance(),2L,20L );


    }

    public void setXp(int level){
        for(DimensionsPlayer dp : this.getInGamePlayers().values()){
            dp.getPlayerIfOnline().setLevel(level);
        }
    }

    public void sendTitle(String title,String subTitle){
        for(DimensionsPlayer dp : this.getInGamePlayers().values()){
            Titles.sendTitle(dp.getPlayerIfOnline(),5,50,5,title,subTitle);
        }
    }

    public void begin(){

        gameStep= GameStep.IN_GAME;
        new TimeTask().runTaskTimer(Dimensions.getInstance(),1L,20L);
        getCoherenceMachine().getMessageManager().writeCustomMessage("§6La partie commence. Bonne chance !",true);
        getCoherenceMachine().getMessageManager().writeCustomMessage("§6Le PVP sera activé dans 2 minutes  !",true);
        for(DimensionsPlayer dp : getInGamePlayers().values()){
            dp.getPlayerIfOnline().setGameMode(GameMode.SURVIVAL);
        }
    }
    public void playSound(Sound sound,float pitch){
        for(DimensionsPlayer dp : this.getInGamePlayers().values()){
            dp.getPlayerIfOnline().playSound(dp.getPlayerIfOnline().getLocation(),sound,1000F,pitch);
        }
    }


    public List<Material> getBlockPlaceAndBreakWhitelist() {
        return blockPlaceAndBreakWhitelist;
    }

    public int getDeathMatchIn() {
        return deathMatchIn;
    }

    public boolean isNonGameStep(){

        return(gameStep==GameStep.WAIT || gameStep==GameStep.PRE_TELEPORT  || gameStep==GameStep.FINISH);
    }
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

    public GameStep getGameStep() {
        return gameStep;
    }

    public void setGameStep(GameStep gameStep) {
        this.gameStep = gameStep;
    }

}
