package net.samagames.dimensionsv2.game.entity;
import net.samagames.api.shops.IPlayerShop;

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
public enum PowerUp {
    HEAL_AT_KILL(new int[]{122, 123, 124, 125, 126} ,new int[]{2, 4, 6, 8, 10} ),
    STRENGHT_AT_KILL(new int[]{ 134, 135, 136, 137, 138, 139},new int[]{3, 5, 7, 9, 10,11}),
    HEAL_AT_STRIKE(new int[]{127, 128, 129, 130, 131, 132, 133},new int[]{2, 4, 6, 7, 8,9,10}),
    TP_TIME(new int[]{117, 118, 119, 120, 121},new int[]{14, 12, 10, 8, 7} );

    private int[] shopValues;
    private int[] gameValues;

    PowerUp(int[] shopValues, int[] gameValues) {
        this.shopValues = shopValues;
        this.gameValues = gameValues;
    }

    /**
     * Get the power up level of a player (buyable by shop)
     * @param shop The player shop
     * @return The level of the powerup
     */
    public int getPowerUpLevelForPlayer(IPlayerShop shop)
    {
        int[] items = this.shopValues;

        try {
            int selected = shop.getSelectedItemFromList(items);
            for (int i = 0; i < items.length; i++) {
                if (items[i] == selected) {
                    return this.gameValues[i];
                }
            }
        }
        catch (Exception e) {}
        return 1;
      }
}
