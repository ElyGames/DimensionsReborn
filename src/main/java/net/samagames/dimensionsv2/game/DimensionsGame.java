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
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
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
    private List<Material> blockPlaceWhitelist;
    private List<Material> blockBreakWhitelist;
    private Random random;
    private TimeTask timerTask;


    public DimensionsGame() {

        super("dimensions", "Dimensions", "", DimensionsPlayer.class);
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

        blockPlaceWhitelist.add(Material.TNT); blockBreakWhitelist.add(Material.TNT);
        blockPlaceWhitelist.add(Material.WORKBENCH); blockBreakWhitelist.add(Material.WORKBENCH);
        blockPlaceWhitelist.add(Material.FURNACE); blockBreakWhitelist.add(Material.FURNACE);
        blockPlaceWhitelist.add(Material.CAKE); blockBreakWhitelist.add(Material.CAKE);
        blockPlaceWhitelist.add(Material.CAKE_BLOCK); blockBreakWhitelist.add(Material.CAKE_BLOCK);


        IGameProperties prop =Dimensions.getInstance().getApi().getGameManager().getGameProperties();

        for(JsonElement elt : prop.getConfig("spawns",new JsonArray()).getAsJsonArray()){
            spawns.add(LocationUtils.str2loc(elt.getAsString()));
        }
        Collections.shuffle(spawns);


        for(JsonElement elt : prop.getConfig("allowBreak",new JsonArray()).getAsJsonArray()){
            blockBreakWhitelist.add(Material.matchMaterial(elt.getAsString()));
        }
        Collections.shuffle(spawns);

        for(JsonElement elt : prop.getConfig("deathMatchSpawns",new JsonArray()).getAsJsonArray()){
            deathMatchSpawns.add(LocationUtils.str2loc(elt.getAsString()));
        }
        Collections.shuffle(deathMatchSpawns);

        waitingRoom = LocationUtils.str2loc(prop.getConfig("waitingRoom",new JsonPrimitive("world, 0, 0, 0, 0, 0")).getAsString());


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
            getCoherenceMachine().getMessageManager().writeCustomMessage("§c" + p.getDisplayName() +" a été éliminé sans aide extérieure.",true);
        }
        else
        {
            getCoherenceMachine().getMessageManager().writeCustomMessage("§c" + p.getDisplayName() + " §c a été tué par " + killer.getPlayerIfOnline().getDisplayName() + "§c.",true);
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
                 killer.getPlayerIfOnline().sendMessage("§6§oUne fée vous a restauré " + healAtKill/2 + "§c§o ♥§6§o et vous a donné §c§o"+ strengthAtKill+ " secondes §6§ode force." );
             }
        }
    }

    @Override
    public void handleLogout(Player player)
    {
        super.handleLogout(player);
        if(!isNonGameStep()){
            stumpPlayer(player,true);
        }

    }
    @Override
    public void handleLogin(Player player){
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(waitingRoom);
        player.setHealth(20D);
        player.getInventory().clear();
        player.setFoodLevel(20);
        super.handleLogin(player);
    }

    public Random getRandom() {
        return random;
    }

    public void stumpPlayer(Player p, boolean logout){
        setSpectator(p);
        DimensionsPlayer dp = getPlayer(p.getUniqueId());
        int left = getInGamePlayers().values().size();
        if(!logout){
            if ((!dp.getUUID().equals(dp.getLastDamager())) || dp.getLastDamager()==null) {
                if (left == 2) {
                    addCoins(p, 20, "Troisième !");
                } else if (left == 1) {
                    addCoins(p, 40, "Second !");
                }
            }
        }

            this.coherenceMachine.getMessageManager().writeCustomMessage(p.getDisplayName() + " a été éliminé.", true);

            if (left!=1) {
                getCoherenceMachine().getMessageManager().writeCustomMessage("§eIl reste encore §b" + + left + " §ejoueurs en vie.",false);
                if(gameStep!=GameStep.DEATHMATCH_PLANNED && deathMatchSpawns.size()==getInGamePlayers().size()){
                    gameStep= GameStep.DEATHMATCH_PLANNED;
                }
            }
             else {
                end();
            }
        }



    public void end(){
        gameStep = GameStep.PVP;
        timerTask.cancel();
        DimensionsPlayer winner = getInGamePlayers().values().iterator().next();
        Titles.sendTitle(winner.getPlayerIfOnline(), 5, 80, 5,"§6Victoire !","§aVous gagnez la partie en §a" +  + winner.getKills() + " §akills !");

        Bukkit.getServer().getOnlinePlayers().stream().filter(p -> !p.equals(winner.getPlayerIfOnline())).forEach(p -> {
            Titles.sendTitle(p, 5, 80, 5, ChatColor.GOLD + "Fin de partie !", ChatColor.GREEN + "Bravo à " + winner.getPlayerIfOnline().getDisplayName());
        });
        this.coherenceMachine.getTemplateManager().getPlayerWinTemplate().execute(winner.getPlayerIfOnline(), winner.getKills());
        addCoins(winner.getPlayerIfOnline(), 60, "Victoire !");
        handleGameEnd();
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
        timerTask.runTaskTimer(Dimensions.getInstance(),1L,20L);
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


    public List<Material> getBlockPlaceWhitelist() {
        return blockPlaceWhitelist;
    }

    public List<Material> getBlockBreakWhitelist() {
        return blockBreakWhitelist;
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
