package net.samagames.dimensionsv2.game.utils;
import org.bukkit.Location;

import java.util.List;

/**
 * Created by Tigger_San on 08/05/2017.
 */
public class DistanceUtil {
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
