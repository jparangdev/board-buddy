package kr.co.jparangdev.boardbuddy.domain.game;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class GameSessionTest {

    private static final Instant PLAYED_AT = Instant.parse("2026-01-10T11:00:00Z");
    private static final SessionConfig DEFAULT_CONFIG = SessionConfig.defaults();

    @Test
    void create_sets_groupId_gameId_and_playedAt() {
        GameSession session = GameSession.create(1L, 10L, PLAYED_AT, DEFAULT_CONFIG);

        assertThat(session.getGroupId()).isEqualTo(1L);
        assertThat(session.getGameId()).isEqualTo(10L);
        assertThat(session.getPlayedAt()).isEqualTo(PLAYED_AT);
    }

    @Test
    void create_sets_createdAt_automatically() {
        GameSession session = GameSession.create(1L, 10L, PLAYED_AT, DEFAULT_CONFIG);

        assertThat(session.getCreatedAt()).isNotNull();
    }

    @Test
    void create_leaves_customGameId_null() {
        GameSession session = GameSession.create(1L, 10L, PLAYED_AT, DEFAULT_CONFIG);

        assertThat(session.getCustomGameId()).isNull();
    }

    @Test
    void createWithCustomGame_sets_groupId_customGameId_and_playedAt() {
        GameSession session = GameSession.createWithCustomGame(2L, 99L, PLAYED_AT, DEFAULT_CONFIG);

        assertThat(session.getGroupId()).isEqualTo(2L);
        assertThat(session.getCustomGameId()).isEqualTo(99L);
        assertThat(session.getPlayedAt()).isEqualTo(PLAYED_AT);
    }

    @Test
    void createWithCustomGame_leaves_gameId_null() {
        GameSession session = GameSession.createWithCustomGame(2L, 99L, PLAYED_AT, DEFAULT_CONFIG);

        assertThat(session.getGameId()).isNull();
    }
}
