package net.samagames.dimensionsv2.game.utils;
import org.bukkit.Location;
import java.util.List;

/**
 * Utils for distance
 * Created by Tigger_San on 08/05/2017.
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
