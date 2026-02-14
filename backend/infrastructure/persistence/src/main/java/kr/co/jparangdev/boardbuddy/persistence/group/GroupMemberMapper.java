package kr.co.jparangdev.boardbuddy.persistence.group;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;

@Component
public class GroupMemberMapper {

    public GroupMemberJpaEntity toEntity(GroupMember groupMember) {
        return new GroupMemberJpaEntity(
            groupMember.getId(),
            groupMember.getGroupId(),
            groupMember.getUserId(),
            groupMember.getJoinedAt(),
            groupMember.getDisplayOrder()
        );
    }

    public GroupMember toDomain(GroupMemberJpaEntity entity) {
        return GroupMember.builder()
            .id(entity.getId())
            .groupId(entity.getGroupId())
            .userId(entity.getUserId())
            .joinedAt(entity.getJoinedAt())
            .displayOrder(entity.getDisplayOrder())
            .build();
    }
}
