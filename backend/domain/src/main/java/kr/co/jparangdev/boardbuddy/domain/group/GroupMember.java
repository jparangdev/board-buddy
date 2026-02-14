package kr.co.jparangdev.boardbuddy.domain.group;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember {
    private Long id;
    private Long groupId;
    private Long userId;
    private LocalDateTime joinedAt;
    private int displayOrder;

    @Builder
    public GroupMember(Long id, Long groupId, Long userId, LocalDateTime joinedAt, int displayOrder) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.joinedAt = joinedAt;
        this.displayOrder = displayOrder;
    }

    public static GroupMember create(Long groupId, Long userId) {
        return GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .joinedAt(LocalDateTime.now())
                .displayOrder(0)
                .build();
    }
}
