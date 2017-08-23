package net.samagames.dimensionsv2.game.entity.chestitem;
import net.samagames.dimensionsv2.game.utils.FireworkUtils;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import java.util.ArrayList;
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
public class ChestItemManager {
    private static ChestItemManager ourInstance = new ChestItemManager();
    private List<Location> openedChests;

    /**
     * Launch and directly explode a firework
     * @param loc The location
     * @param effect The builded effect
     */
    public void launchAndExplode(Location loc, FireworkEffect effect){
        loc.add(0.5,0.5,0.5);
        FireworkUtils.launchfw(loc,effect);
    }

    /**
     * Save the fact that the chest is opened
     * @param c The chest
     */
    public void opened(Chest c){
        openedChests.add(c.getLocation());
    }


    public static ChestItemManager getInstance() {
        return ourInstance;
    }

    /**
     * Verify if a chest is opened
     * @param c The chest
     * @return Opened or not
     */
    public boolean isOpened(Chest c){
        return openedChests.contains(c.getLocation());
    }

    private ChestItemManager() {
        openedChests = new ArrayList<>();
    }
}
