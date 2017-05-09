package net.samagames.dimensionsv2.game.entity;
import net.samagames.api.shops.IPlayerShop;


/**
 * Represents the power ups that players can buy in the shop
 * Created by Tigger_San on 21/04/2017.
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
