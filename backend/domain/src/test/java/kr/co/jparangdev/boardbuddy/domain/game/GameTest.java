package kr.co.jparangdev.boardbuddy.domain.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameTest {

    @Test
    void create_sets_all_fields() {
        Game game = Game.create("Catan", "카탄", "Catan", 3, 4, ScoreStrategy.HIGH_WIN);

        assertThat(game.getName()).isEqualTo("Catan");
        assertThat(game.getNameKo()).isEqualTo("카탄");
        assertThat(game.getNameEn()).isEqualTo("Catan");
        assertThat(game.getMinPlayers()).isEqualTo(3);
        assertThat(game.getMaxPlayers()).isEqualTo(4);
        assertThat(game.getScoreStrategy()).isEqualTo(ScoreStrategy.HIGH_WIN);
    }

    @Test
    void create_sets_createdAt_automatically() {
        Game game = Game.create("Catan", "카탄", "Catan", 3, 4, ScoreStrategy.HIGH_WIN);

        assertThat(game.getCreatedAt()).isNotNull();
    }

    @Test
    void create_does_not_assign_id() {
        Game game = Game.create("Catan", "카탄", "Catan", 3, 4, ScoreStrategy.HIGH_WIN);

        assertThat(game.getId()).isNull();
    }

    @Test
    void update_changes_nameKo_and_nameEn() {
        Game game = Game.create("Catan", "카탄", "Catan", 3, 4, ScoreStrategy.HIGH_WIN);

        game.update("카탄 (신판)", "Catan (New Edition)");

        assertThat(game.getNameKo()).isEqualTo("카탄 (신판)");
        assertThat(game.getNameEn()).isEqualTo("Catan (New Edition)");
    }

    @Test
    void update_does_not_change_name_or_strategy() {
        Game game = Game.create("Catan", "카탄", "Catan", 3, 4, ScoreStrategy.HIGH_WIN);

        game.update("카탄 (신판)", "Catan (New Edition)");

        assertThat(game.getName()).isEqualTo("Catan");
        assertThat(game.getScoreStrategy()).isEqualTo(ScoreStrategy.HIGH_WIN);
    }
}
