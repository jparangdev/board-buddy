package kr.co.jparangdev.boardbuddy.domain.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void fromOAuth_sets_all_fields_correctly() {
        User user = User.fromOAuth("john@test.com", "KAKAO", "kakao-123", "John", "AB12");

        assertThat(user.getEmail()).isEqualTo("john@test.com");
        assertThat(user.getProvider()).isEqualTo("KAKAO");
        assertThat(user.getProviderId()).isEqualTo("kakao-123");
        assertThat(user.getNickname()).isEqualTo("John");
        assertThat(user.getDiscriminator()).isEqualTo("AB12");
        assertThat(user.getId()).isNull();
    }

    @Test
    void getUserTag_returns_nickname_hash_discriminator() {
        User user = User.fromOAuth("john@test.com", "KAKAO", "kakao-123", "John", "AB12");

        assertThat(user.getUserTag()).isEqualTo("John#AB12");
    }

    @Test
    void getUserTag_includes_discriminator_with_numbers() {
        User user = User.fromOAuth("jane@test.com", "KAKAO", "kakao-456", "Jane", "1234");

        assertThat(user.getUserTag()).isEqualTo("Jane#1234");
    }

    @Test
    void createLocal_sets_all_fields_correctly() {
        User user = User.createLocal("alice@test.com", "hashed-pw", "Alice", "AB12");

        assertThat(user.getEmail()).isEqualTo("alice@test.com");
        assertThat(user.getPasswordHash()).isEqualTo("hashed-pw");
        assertThat(user.getNickname()).isEqualTo("Alice");
        assertThat(user.getDiscriminator()).isEqualTo("AB12");
        assertThat(user.getProvider()).isEqualTo("LOCAL");
        assertThat(user.getProviderId()).isEqualTo("alice@test.com");
        assertThat(user.getId()).isNull();
    }

    @Test
    void createLocal_getUserTag_returns_correct_tag() {
        User user = User.createLocal("alice@test.com", "hashed-pw", "Alice", "AB12");

        assertThat(user.getUserTag()).isEqualTo("Alice#AB12");
    }
}
