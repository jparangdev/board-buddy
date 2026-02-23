package kr.co.jparangdev.boardbuddy.domain.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GroupStatsTest {

    @Test
    void constructor_stores_all_stats() {
        var playerStat = new GroupStats.PlayerStat(1L, "Alice", "Alice#AA01", 5L);
        var winRateStat = new GroupStats.WinRateStat(1L, "Alice", "Alice#AA01", 0.8, 5L, 4L);
        var gamePlayStat = new GroupStats.GamePlayStat("Catan", 3L);

        GroupStats stats = new GroupStats(10L, 25L, List.of(playerStat), List.of(playerStat), List.of(winRateStat), List.of(gamePlayStat));

        assertThat(stats.totalSessions()).isEqualTo(10L);
        assertThat(stats.totalParticipations()).isEqualTo(25L);
        assertThat(stats.mostActivePlayers()).containsExactly(playerStat);
        assertThat(stats.mostWins()).containsExactly(playerStat);
        assertThat(stats.winRateRanking()).containsExactly(winRateStat);
        assertThat(stats.mostPlayedGames()).containsExactly(gamePlayStat);
    }

    @Test
    void PlayerStat_stores_fields() {
        var stat = new GroupStats.PlayerStat(2L, "Bob", "Bob#BB02", 3L);

        assertThat(stat.userId()).isEqualTo(2L);
        assertThat(stat.nickname()).isEqualTo("Bob");
        assertThat(stat.userTag()).isEqualTo("Bob#BB02");
        assertThat(stat.count()).isEqualTo(3L);
    }

    @Test
    void WinRateStat_stores_fields() {
        var stat = new GroupStats.WinRateStat(3L, "Carol", "Carol#CC03", 0.6, 10L, 6L);

        assertThat(stat.userId()).isEqualTo(3L);
        assertThat(stat.winRate()).isEqualTo(0.6);
        assertThat(stat.totalGames()).isEqualTo(10L);
        assertThat(stat.wins()).isEqualTo(6L);
    }

    @Test
    void GamePlayStat_stores_fields() {
        var stat = new GroupStats.GamePlayStat("Splendor", 7L);

        assertThat(stat.gameName()).isEqualTo("Splendor");
        assertThat(stat.playCount()).isEqualTo(7L);
    }

    @Test
    void empty_stats_are_valid() {
        GroupStats stats = new GroupStats(0L, 0L, List.of(), List.of(), List.of(), List.of());

        assertThat(stats.totalSessions()).isEqualTo(0L);
        assertThat(stats.mostActivePlayers()).isEmpty();
    }
}
