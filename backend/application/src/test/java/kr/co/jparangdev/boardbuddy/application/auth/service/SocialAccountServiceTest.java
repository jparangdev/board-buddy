package kr.co.jparangdev.boardbuddy.application.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import kr.co.jparangdev.boardbuddy.application.auth.dto.OAuthUserInfo;
import kr.co.jparangdev.boardbuddy.application.auth.dto.SocialAccountDto;
import kr.co.jparangdev.boardbuddy.domain.auth.ProviderType;
import kr.co.jparangdev.boardbuddy.domain.auth.SocialAccount;
import kr.co.jparangdev.boardbuddy.domain.auth.exception.CannotUnlinkLastMethodException;
import kr.co.jparangdev.boardbuddy.domain.auth.exception.ProviderAlreadyLinkedException;
import kr.co.jparangdev.boardbuddy.domain.auth.repository.SocialAccountRepository;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import kr.co.jparangdev.boardbuddy.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SocialAccountServiceTest {

    private SocialAccountService service;

    @Mock
    private OAuthProvider kakaoProvider;

    @Mock
    private SocialAccountRepository socialAccountRepository;

    @Mock
    private UserRepository userRepository;

    private static final OAuthUserInfo KAKAO_INFO = new OAuthUserInfo("kakao-111", "u@k.com", "KUser");

    @BeforeEach
    void setUp() {
        given(kakaoProvider.getProviderType()).willReturn(ProviderType.KAKAO);
        service = new SocialAccountService(List.of(kakaoProvider), socialAccountRepository, userRepository);
    }

    @Test
    @DisplayName("getLinkedAccounts returns mapped DTOs")
    void getLinkedAccounts_returnsMapped() {
        SocialAccount sa = new SocialAccount(1L, "KAKAO", "kakao-111", Instant.now());
        given(socialAccountRepository.findAllByUserId(1L)).willReturn(List.of(sa));

        List<SocialAccountDto> result = service.getLinkedAccounts(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).provider()).isEqualTo("KAKAO");
    }

    @Test
    @DisplayName("linkAccount saves social account")
    void linkAccount_success() {
        given(kakaoProvider.fetchUserInfo("code", "http://r")).willReturn(KAKAO_INFO);
        given(socialAccountRepository.existsByUserIdAndProvider(1L, "KAKAO")).willReturn(false);
        given(socialAccountRepository.findUserIdByProviderAndProviderId("KAKAO", "kakao-111"))
                .willReturn(Optional.empty());
        given(userRepository.findByProviderAndProviderId("KAKAO", "kakao-111"))
                .willReturn(Optional.empty());

        service.linkAccount(1L, "kakao", "code", "http://r");

        verify(socialAccountRepository).save(1L, "KAKAO", "kakao-111");
    }

    @Test
    @DisplayName("linkAccount throws when provider already linked to this user")
    void linkAccount_alreadyLinkedToSameUser() {
        given(socialAccountRepository.existsByUserIdAndProvider(1L, "KAKAO")).willReturn(true);

        assertThatThrownBy(() -> service.linkAccount(1L, "kakao", "code", "http://r"))
                .isInstanceOf(ProviderAlreadyLinkedException.class);
    }

    @Test
    @DisplayName("linkAccount throws when provider linked to different user")
    void linkAccount_linkedToDifferentUser() {
        given(kakaoProvider.fetchUserInfo("code", "http://r")).willReturn(KAKAO_INFO);
        given(socialAccountRepository.existsByUserIdAndProvider(1L, "KAKAO")).willReturn(false);
        given(socialAccountRepository.findUserIdByProviderAndProviderId("KAKAO", "kakao-111"))
                .willReturn(Optional.of(99L));

        assertThatThrownBy(() -> service.linkAccount(1L, "kakao", "code", "http://r"))
                .isInstanceOf(ProviderAlreadyLinkedException.class);
    }

    @Test
    @DisplayName("unlinkAccount removes the linked provider")
    void unlinkAccount_success() {
        User user = User.builder().id(1L).provider("LOCAL").passwordHash("hash").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(socialAccountRepository.findAllByUserId(1L))
                .willReturn(List.of(new SocialAccount(1L, "KAKAO", "kakao-111", Instant.now())));

        service.unlinkAccount(1L, "KAKAO");

        verify(socialAccountRepository).deleteByUserIdAndProvider(1L, "KAKAO");
    }

    @Test
    @DisplayName("unlinkAccount throws when no other login method exists")
    void unlinkAccount_lastMethod_throws() {
        User user = User.builder().id(1L).provider("KAKAO").passwordHash(null).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(socialAccountRepository.findAllByUserId(1L))
                .willReturn(List.of(new SocialAccount(1L, "KAKAO", "kakao-111", Instant.now())));

        assertThatThrownBy(() -> service.unlinkAccount(1L, "KAKAO"))
                .isInstanceOf(CannotUnlinkLastMethodException.class);
    }
}
