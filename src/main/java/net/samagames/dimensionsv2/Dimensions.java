package net.samagames.dimensionsv2;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamesNames;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.chestitem.ChestItemManager;
import net.samagames.dimensionsv2.game.listeners.ChestItemListener;
import net.samagames.dimensionsv2.game.listeners.DamageListener;
import net.samagames.dimensionsv2.game.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Tigger_San on 21/04/2017.
 */
public class Dimensions extends JavaPlugin
{
    private SamaGamesAPI api;
    private DimensionsGame game;
    private static Dimensions instance;

    @Override
    public void onEnable(){
        instance = this;
        this.api = SamaGamesAPI.get();
        this.game = new DimensionsGame();
        SamaGamesAPI.get().getGameManager().registerGame(game);
        SamaGamesAPI.get().getShopsManager().setShopToLoad(GamesNames.DIMENSION, true);
        getServer().getPluginManager().registerEvents(new PlayerListener(),this);
        getServer().getPluginManager().registerEvents(new ChestItemListener(),this);
        getServer().getPluginManager().registerEvents(new DamageListener(),this);
        ChestItemManager.getInstance();

    }


    public static Dimensions getInstance() {
        return instance;
    }

    public SamaGamesAPI getApi() {
        return api;
    }

    public DimensionsGame getGame() {
        return game;
    }
}
