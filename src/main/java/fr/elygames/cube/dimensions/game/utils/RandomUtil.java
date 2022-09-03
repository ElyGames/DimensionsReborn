package fr.elygames.cube.dimensions.game.utils;
import java.util.Random;

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
public class RandomUtil {
    /**
     * Pick a boolean based on a percent
     * @param random Instance of random
     * @param percent The percent
     * @return Yes or no
     */
    public static boolean pickBoolean(Random random, int percent){
        int i = random.nextInt(100);
        return i<percent;
    }
}
