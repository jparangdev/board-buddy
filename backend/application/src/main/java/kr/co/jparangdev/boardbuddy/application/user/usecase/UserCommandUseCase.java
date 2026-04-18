package kr.co.jparangdev.boardbuddy.application.user.usecase;

public interface UserCommandUseCase {
    void deleteUser(Long userId);
    void updateNickname(Long userId, String nickname);
}
