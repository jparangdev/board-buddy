package kr.co.jparangdev.boardbuddy.domain.auth;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SocialAccountTest {

    @Test
    void record_holds_all_fields() {
        Instant now = Instant.now();
        SocialAccount account = new SocialAccount(1L, "KAKAO", "kakao-123", now);

        assertThat(account.userId()).isEqualTo(1L);
        assertThat(account.provider()).isEqualTo("KAKAO");
        assertThat(account.providerId()).isEqualTo("kakao-123");
        assertThat(account.createdAt()).isEqualTo(now);
    }

    @Test
    void two_records_with_same_values_are_equal() {
        Instant now = Instant.now();
        SocialAccount a = new SocialAccount(1L, "KAKAO", "kakao-123", now);
        SocialAccount b = new SocialAccount(1L, "KAKAO", "kakao-123", now);

        assertThat(a).isEqualTo(b);
    }

    @Test
    void two_records_with_different_provider_are_not_equal() {
        Instant now = Instant.now();
        SocialAccount kakao = new SocialAccount(1L, "KAKAO", "id-123", now);
        SocialAccount naver = new SocialAccount(1L, "NAVER", "id-123", now);

        assertThat(kakao).isNotEqualTo(naver);
    }
}
