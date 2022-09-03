package fr.elygames.cube.dimensions.game.utils;
import org.bukkit.Location;
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
public class DistanceUtil {
    /**
     * Get the closest location of a location
     * @param l The location
     * @param locs List of other locations
     * @return The closest location
     */
    public static Location getNearbyLocation(Location l ,List<Location> locs){
        Location nearby = locs.get(0);
        for(Location loc : locs){
            if(loc.distance(l) < nearby.distance(l)){
                nearby=loc;
            }
        }
        return nearby;
    }
}
