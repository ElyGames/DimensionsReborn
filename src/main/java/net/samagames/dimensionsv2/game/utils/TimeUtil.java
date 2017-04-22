package net.samagames.dimensionsv2.game.utils;

/**
 * Created by Tigger_San on 22/04/2017.
 */
public class TimeUtil {

    public static String timeToString(int time){
        int minutes = time / 60;
        int seconds = time % 60;
         return (minutes > 9 ? minutes : "0" + minutes) + ":" + (seconds > 9 ? seconds : "0" + seconds);
    }
}
