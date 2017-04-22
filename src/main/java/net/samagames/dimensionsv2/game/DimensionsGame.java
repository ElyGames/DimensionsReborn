package net.samagames.dimensionsv2.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.samagames.api.games.Game;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.dimensionsv2.game.tasks.TimeTask;
import net.samagames.tools.LocationUtils;
import net.samagames.tools.Titles;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tigger_San on 21/04/2017.
 */
public class DimensionsGame extends Game<DimensionsPlayer>{


    private List<Location> spawns;
    private List<Location> deathMatchSpawns;
    private int gameTime;
    private int pvpIn;
    private int deathMatchIn;
    private boolean deathMatchPlanned;


    public DimensionsGame() {
        super("dimensions", "Dimensions", "", DimensionsPlayer.class);
        spawns = new ArrayList<>();
        deathMatchSpawns = new ArrayList<>();
        gameTime=0;
        pvpIn = 120;
        deathMatchIn = 60;
        deathMatchPlanned=false;

        for(JsonElement elt : Dimensions.getInstance().getApi().getGameManager().getGameProperties().getConfig("spawns",new JsonArray()).getAsJsonArray()){
            spawns.add(LocationUtils.str2loc(elt.getAsString()));
        }
        for(JsonElement elt : Dimensions.getInstance().getApi().getGameManager().getGameProperties().getConfig("deathMatchSpawns",new JsonArray()).getAsJsonArray()){
            deathMatchSpawns.add(LocationUtils.str2loc(elt.getAsString()));
        }

    }

    @Override
    public void handleLogout(Player player)
    {
        getCoherenceMachine().getMessageManager().writeCustomMessage("§f" + player.getDisplayName() + " §fs'est déconnecté.",true);
        stumpPlayer(player);
        super.handleLogout(player);
    }

    public void stumpPlayer(Player p){

        if(!deathMatchPlanned && deathMatchSpawns.size()==getInGamePlayers().size()){
            deathMatchPlanned=true;
        }
    }



    @Override
    public void startGame()
    {
        super.startGame();

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
                    case 15: playSound(Sound.BLOCK_NOTE_PLING,1.0F);getCoherenceMachine().getMessageManager().writeCustomMessage("§eDémarrage du jeu dans §c" + i + " " + secFormat + "§e.",true); break;
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

        new TimeTask().runTaskTimer(Dimensions.getInstance(),1L,20L);
        getCoherenceMachine().getMessageManager().writeCustomMessage("§6La partie commence. Bonne chance !",true);
        getCoherenceMachine().getMessageManager().writeCustomMessage("§6Le PVP sera activé dans 2 minutes  !",true);
    }
    public void playSound(Sound sound,float pitch){
        for(DimensionsPlayer dp : this.getInGamePlayers().values()){
            dp.getPlayerIfOnline().playSound(dp.getPlayerIfOnline().getLocation(),sound,1000F,pitch);
        }
    }


    public int getDeathMatchIn() {
        return deathMatchIn;
    }

    public boolean isDeathMatchPlanned() {
        return deathMatchPlanned;
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

    public void setDeathMatchPlanned(boolean deathMatchPlanned) {
        this.deathMatchPlanned = deathMatchPlanned;
    }

    public void decreasePvpIn(){
        pvpIn--;
    }
}
