package kr.co.jparangdev.boardbuddy.application.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kr.co.jparangdev.boardbuddy.domain.auth.exception.InvalidTokenException;

@Component
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public JwtTokenProviderImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiry:3600000}") long accessTokenExpiry,
            @Value("${jwt.refresh-token-expiry:2592000000}") long refreshTokenExpiry) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry; // 1 hour
        this.refreshTokenExpiry = refreshTokenExpiry; // 30 days
    }

    @Override
    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiry);

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .claim("type", "access")
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    @Override
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiry);

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .claim("type", "refresh")
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    @Override
    public Long validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token expired");
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid token");
        }
    }

    @Override
    public Long getAccessTokenExpiry() {
        return accessTokenExpiry / 1000; // Return in seconds
    }
}
