package net.samagames.dimensionsv2.game.utils;

import java.util.Random;

/**
 * Created by Tigger_San on 23/04/2017.
 */
public class RandomUtil {
    public static boolean pickBoolean(Random random, int percent){
        int i = random.nextInt(100);
        if(i<percent){
            return true;
        }
        return false;
    }
}
