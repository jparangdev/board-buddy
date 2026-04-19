package kr.co.jparangdev.boardbuddy.application.auth.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.application.auth.dto.SocialAccountDto;

public interface SocialAccountUseCase {

    List<SocialAccountDto> getLinkedAccounts(Long userId);

    void linkAccount(Long userId, String provider, String code, String redirectUri);

    void unlinkAccount(Long userId, String provider);
}
