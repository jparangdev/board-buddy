package kr.co.jparangdev.boardbuddy.application.auth.usecase;

public interface RegisterUseCase {

    void register(String email, String rawPassword, String nickname);
}
