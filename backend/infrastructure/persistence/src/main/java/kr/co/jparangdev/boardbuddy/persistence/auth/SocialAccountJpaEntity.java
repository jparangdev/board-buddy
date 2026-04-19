package kr.co.jparangdev.boardbuddy.persistence.auth;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_social_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccountJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String provider;

    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public SocialAccountJpaEntity(Long userId, String provider, String providerId, Instant createdAt) {
        this.userId = userId;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = createdAt;
    }
}
