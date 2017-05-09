package net.samagames.dimensionsv2.game.utils;
import java.util.Random;

/**
 * Utils for random
 * Created by Tigger_San on 23/04/2017.
 */
public class RandomUtil {
    /**
     * Pick a boolean based on a percent
     * @param random Instance of random
     * @param percent The percent
     * @return Yes or no
     */
    public static boolean pickBoolean(Random random, int percent){
        int i = random.nextInt(100);
        if(i<percent){
            return true;
        }
        return false;
    }
}
