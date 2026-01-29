package kr.co.jparangdev.boardbuddy.application.auth.service;

public interface JwtTokenProvider {
    String generateAccessToken(Long userId);
    String generateRefreshToken(Long userId);
    Long validateToken(String token);
    Long getAccessTokenExpiry();
}
