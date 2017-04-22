package net.samagames.dimensionsv2;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamesNames;
import net.samagames.dimensionsv2.game.DimensionsGame;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Tigger_San on 21/04/2017.
 */
public class Dimensions extends JavaPlugin
{
    private SamaGamesAPI api;
    private DimensionsGame game;

    @Override
    public void onEnable(){
        this.api = SamaGamesAPI.get();
        this.game = new DimensionsGame(this);
        SamaGamesAPI.get().getGameManager().registerGame(game);
        SamaGamesAPI.get().getShopsManager().setShopToLoad(GamesNames.DIMENSION, true);

    }

    public SamaGamesAPI getApi() {
        return api;
    }

    public DimensionsGame getGame() {
        return game;
    }
}
