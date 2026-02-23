package kr.co.jparangdev.boardbuddy.domain.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameResultTest {

    @Test
    void create_sets_all_fields() {
        GameResult result = GameResult.create(100L, 7L, 42, true, 1);

        assertThat(result.getSessionId()).isEqualTo(100L);
        assertThat(result.getUserId()).isEqualTo(7L);
        assertThat(result.getScore()).isEqualTo(42);
        assertThat(result.isWon()).isTrue();
        assertThat(result.getRank()).isEqualTo(1);
    }

    @Test
    void create_does_not_assign_id() {
        GameResult result = GameResult.create(100L, 7L, 42, true, 1);

        assertThat(result.getId()).isNull();
    }

    @Test
    void create_with_null_score_is_allowed() {
        GameResult result = GameResult.create(100L, 7L, null, false, 2);

        assertThat(result.getScore()).isNull();
        assertThat(result.isWon()).isFalse();
        assertThat(result.getRank()).isEqualTo(2);
    }
}
