package kr.co.jparangdev.boardbuddy.domain.group;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember {
    private Long id;
    private Long groupId;
    private Long userId;
    private LocalDateTime joinedAt;

    @Builder
    public GroupMember(Long id, Long groupId, Long userId, LocalDateTime joinedAt) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }

    public static GroupMember create(Long groupId, Long userId) {
        return GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .joinedAt(LocalDateTime.now())
                .build();
    }
}
