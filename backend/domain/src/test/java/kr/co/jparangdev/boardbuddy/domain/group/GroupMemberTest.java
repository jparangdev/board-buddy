package kr.co.jparangdev.boardbuddy.domain.group;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GroupMemberTest {

    @Test
    void create_sets_groupId_and_userId() {
        GroupMember member = GroupMember.create(10L, 20L);

        assertThat(member.getGroupId()).isEqualTo(10L);
        assertThat(member.getUserId()).isEqualTo(20L);
    }

    @Test
    void create_sets_displayOrder_to_zero() {
        GroupMember member = GroupMember.create(10L, 20L);

        assertThat(member.getDisplayOrder()).isEqualTo(0);
    }

    @Test
    void create_sets_joinedAt_automatically() {
        GroupMember member = GroupMember.create(10L, 20L);

        assertThat(member.getJoinedAt()).isNotNull();
    }

    @Test
    void create_does_not_assign_id() {
        GroupMember member = GroupMember.create(10L, 20L);

        assertThat(member.getId()).isNull();
    }
}
