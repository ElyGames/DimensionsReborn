package net.samagames.dimensionsv2.game.entity.dimension;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IGameStatisticsHelper;

import java.util.UUID;

/**
 * Created by BlueSlime
 */
public class DimensionsStatistics implements IGameStatisticsHelper {
    @Override
    public void increasePlayedTime(UUID uuid, long playedTime) {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getDimensionsStatistics().incrByPlayedTime(playedTime);
    }

    @Override
    public void increasePlayedGames(UUID uuid) {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getDimensionsStatistics().incrByPlayedGames(1);
    }

    @Override
    public void increaseWins(UUID uuid) {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getDimensionsStatistics().incrByWins(1);
    }

    public void increaseKills(UUID uuid) {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getDimensionsStatistics().incrByKills(1);
    }

    public void increaseDeaths(UUID uuid) {
        SamaGamesAPI.get().getStatsManager().getPlayerStats(uuid).getDimensionsStatistics().incrByDeaths(1);
    }
}