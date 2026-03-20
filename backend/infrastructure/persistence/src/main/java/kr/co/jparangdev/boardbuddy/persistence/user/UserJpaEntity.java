package kr.co.jparangdev.boardbuddy.persistence.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"provider", "provider_id"}),
        @UniqueConstraint(columnNames = {"nickname", "discriminator"})
    })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 4, updatable = false)
    private String discriminator;

    @Column(nullable = false, length = 20, updatable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false, updatable = false)
    private String providerId;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public UserJpaEntity(Long id, String email, String nickname, String discriminator,
                         String provider, String providerId, String passwordHash) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.discriminator = discriminator;
        this.provider = provider;
        this.providerId = providerId;
        this.passwordHash = passwordHash;
    }
}
