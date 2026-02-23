package kr.co.jparangdev.boardbuddy.domain.group;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTest {

    @Test
    void create_sets_name_and_ownerId() {
        Group group = Group.create("Friday Crew", 1L);

        assertThat(group.getName()).isEqualTo("Friday Crew");
        assertThat(group.getOwnerId()).isEqualTo(1L);
    }

    @Test
    void create_sets_createdAt_automatically() {
        Group group = Group.create("Friday Crew", 1L);

        assertThat(group.getCreatedAt()).isNotNull();
    }

    @Test
    void create_does_not_assign_id() {
        Group group = Group.create("Friday Crew", 1L);

        assertThat(group.getId()).isNull();
    }

    @Test
    void isOwner_returns_true_for_owner() {
        Group group = Group.create("Friday Crew", 42L);

        assertThat(group.isOwner(42L)).isTrue();
    }

    @Test
    void isOwner_returns_false_for_non_owner() {
        Group group = Group.create("Friday Crew", 42L);

        assertThat(group.isOwner(99L)).isFalse();
    }
}
