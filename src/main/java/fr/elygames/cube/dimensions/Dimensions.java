package fr.elygames.cube.dimensions;

import fr.elygames.cube.ElyAPI;
import fr.elygames.cube.dimensions.game.DimensionsGame;
import fr.elygames.cube.dimensions.game.entity.chestitem.ChestItemManager;
import fr.elygames.cube.dimensions.game.entity.dimension.DimensionsManager;
import fr.elygames.cube.dimensions.game.listeners.PlayerListener;;
import fr.elygames.cube.dimensions.game.listeners.ChestItemListener;
import fr.elygames.cube.dimensions.game.listeners.DamageListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
public class Dimensions extends JavaPlugin
{
    private DimensionsGame game;
    private static Dimensions instance;

    @Override
    public void onEnable(){
        instance = this;
        this.game = new DimensionsGame();
        //SamaGamesAPI.get().getGameManager().setLegacyPvP(true);
        ElyAPI.getGameManager().registerGame(game);

        getServer().getPluginManager().registerEvents(new PlayerListener(),this);
        getServer().getPluginManager().registerEvents(new ChestItemListener(),this);
        getServer().getPluginManager().registerEvents(new DamageListener(),this);
        ChestItemManager.getInstance();
        DimensionsManager.getInstance();
        Bukkit.getWorlds().get(0).setThundering(false);
        Bukkit.getWorlds().get(0).setStorm(false);
        Bukkit.getWorlds().get(0).setTime(6000);
        Bukkit.getWorlds().get(0).setGameRuleValue("doDaylightCycle","false");
    }

    public static Dimensions getInstance() {
        return instance;
    }

    public DimensionsGame getGame() {
        return game;
    }

}
