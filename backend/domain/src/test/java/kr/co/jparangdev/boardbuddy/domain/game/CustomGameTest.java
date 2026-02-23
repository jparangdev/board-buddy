package kr.co.jparangdev.boardbuddy.domain.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomGameTest {

    @Test
    void create_sets_all_fields() {
        CustomGame game = CustomGame.create(5L, "House Rules Chess", "우리집 룰 체스", "House Rules Chess", 2, 2, ScoreStrategy.WIN_LOSE);

        assertThat(game.getGroupId()).isEqualTo(5L);
        assertThat(game.getName()).isEqualTo("House Rules Chess");
        assertThat(game.getNameKo()).isEqualTo("우리집 룰 체스");
        assertThat(game.getNameEn()).isEqualTo("House Rules Chess");
        assertThat(game.getMinPlayers()).isEqualTo(2);
        assertThat(game.getMaxPlayers()).isEqualTo(2);
        assertThat(game.getScoreStrategy()).isEqualTo(ScoreStrategy.WIN_LOSE);
    }

    @Test
    void create_sets_createdAt_automatically() {
        CustomGame game = CustomGame.create(5L, "House Rules Chess", "우리집 룰 체스", "House Rules Chess", 2, 2, ScoreStrategy.WIN_LOSE);

        assertThat(game.getCreatedAt()).isNotNull();
    }

    @Test
    void create_does_not_assign_id() {
        CustomGame game = CustomGame.create(5L, "House Rules Chess", "우리집 룰 체스", "House Rules Chess", 2, 2, ScoreStrategy.WIN_LOSE);

        assertThat(game.getId()).isNull();
    }
}
