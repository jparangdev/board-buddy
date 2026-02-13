package kr.co.jparangdev.boardbuddy.persistence.group;

import kr.co.jparangdev.boardbuddy.domain.group.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public GroupJpaEntity toEntity(Group group) {
        return new GroupJpaEntity(
            group.getId(),
            group.getName(),
            group.getOwnerId(),
            group.getCreatedAt()
        );
    }

    public Group toDomain(GroupJpaEntity entity) {
        return Group.builder()
            .id(entity.getId())
            .name(entity.getName())
            .ownerId(entity.getOwnerId())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
