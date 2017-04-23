package net.samagames.dimensionsv2.game;

import com.google.gson.*;
import net.samagames.api.games.Game;
import net.samagames.api.games.IGameProperties;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.entity.GameStep;
import net.samagames.dimensionsv2.game.tasks.TimeTask;
import net.samagames.tools.LocationUtils;
import net.samagames.tools.Titles;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
    private Map<DimensionsPlayer,DimensionsPlayer> lastDamager;



    public DimensionsGame() {
        super("dimensions", "Dimensions", "", DimensionsPlayer.class);
        spawns = new ArrayList<>();
        deathMatchSpawns = new ArrayList<>();
        gameTime=0;
        pvpIn = 120;
        deathMatchIn = 60;
        gameStep = GameStep.WAIT;
        blockPlaceAndBreakWhitelist = new ArrayList<>();
        lastDamager = new HashMap<>();

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
        setLastDamager((getPlayer(p.getUniqueId())), getPlayer(damager.getUniqueId()));
        //TODO
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
        //TODO
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

    public void setLastDamager(DimensionsPlayer damager, DimensionsPlayer player){
        lastDamager.put(player,damager);
    }
    public DimensionsPlayer getLastDamager(DimensionsPlayer player){
        return lastDamager.get(player);
    }
}
