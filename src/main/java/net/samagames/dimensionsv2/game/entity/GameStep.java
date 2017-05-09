package net.samagames.dimensionsv2.game.entity;

/**
 * Represent the game step (state of the game)
 * Created by Tigger_San on 22/04/2017.
 */
public enum GameStep {
    WAIT,
    PRE_TELEPORT,
    IN_GAME,
    PVP,
    DEATHMATCH_PLANNED,
    DEATHMATCH,
    FINISH;
}
