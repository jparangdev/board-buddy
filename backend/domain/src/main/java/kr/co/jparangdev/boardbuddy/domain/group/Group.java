package kr.co.jparangdev.boardbuddy.domain.group;

import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group {
    private Long id;
    private String name;
    private Long ownerId;
    private Instant createdAt;

    @Builder
    public Group(Long id, String name, Long ownerId, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    public static Group create(String name, Long ownerId) {
        return Group.builder()
                .name(name)
                .ownerId(ownerId)
                .createdAt(Instant.now())
                .build();
    }

    public boolean isOwner(Long userId) {
        return this.ownerId.equals(userId);
    }
}
