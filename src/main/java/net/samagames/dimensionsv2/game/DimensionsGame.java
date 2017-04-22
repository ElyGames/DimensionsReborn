package net.samagames.dimensionsv2.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.samagames.api.games.Game;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.entity.DimensionsPlayer;
import net.samagames.tools.LocationUtils;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tigger_San on 21/04/2017.
 */
public class DimensionsGame extends Game<DimensionsPlayer>{


    private Dimensions instance;
    private List<Location> spawns;


    public DimensionsGame(Dimensions instance) {
        super("dimensions", "Dimensions", "", DimensionsPlayer.class);
        this.instance=instance;
        spawns = new ArrayList<>();

        for(JsonElement elt : instance.getApi().getGameManager().getGameProperties().getConfig("spawns",new JsonArray()).getAsJsonArray()){
            spawns.add(LocationUtils.str2loc(elt.getAsString()));
        }
    }

    @Override
    public void startGame()
    {
        super.startGame();

    }


}
