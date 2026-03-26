package kr.co.jparangdev.boardbuddy.domain.invitation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InvitationTest {

    @Test
    void create_sets_groupId_inviterId_inviteeId() {
        Invitation invitation = Invitation.create(10L, 1L, 2L);

        assertThat(invitation.getGroupId()).isEqualTo(10L);
        assertThat(invitation.getInviterId()).isEqualTo(1L);
        assertThat(invitation.getInviteeId()).isEqualTo(2L);
    }

    @Test
    void create_sets_status_to_pending() {
        Invitation invitation = Invitation.create(10L, 1L, 2L);

        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(invitation.isPending()).isTrue();
    }

    @Test
    void create_sets_createdAt_automatically() {
        Invitation invitation = Invitation.create(10L, 1L, 2L);

        assertThat(invitation.getCreatedAt()).isNotNull();
    }

    @Test
    void create_does_not_assign_id() {
        Invitation invitation = Invitation.create(10L, 1L, 2L);

        assertThat(invitation.getId()).isNull();
    }

    @Test
    void accept_returns_new_invitation_with_accepted_status() {
        Invitation invitation = Invitation.builder()
                .id(1L)
                .groupId(10L)
                .inviterId(1L)
                .inviteeId(2L)
                .status(InvitationStatus.PENDING)
                .build();

        Invitation accepted = invitation.accept();

        assertThat(accepted.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
        assertThat(accepted.getRespondedAt()).isNotNull();
        assertThat(accepted.getId()).isEqualTo(1L);
    }

    @Test
    void reject_returns_new_invitation_with_rejected_status() {
        Invitation invitation = Invitation.builder()
                .id(1L)
                .groupId(10L)
                .inviterId(1L)
                .inviteeId(2L)
                .status(InvitationStatus.PENDING)
                .build();

        Invitation rejected = invitation.reject();

        assertThat(rejected.getStatus()).isEqualTo(InvitationStatus.REJECTED);
        assertThat(rejected.getRespondedAt()).isNotNull();
    }

    @Test
    void isPending_returns_false_after_accept() {
        Invitation invitation = Invitation.create(10L, 1L, 2L);
        Invitation accepted = invitation.accept();

        assertThat(accepted.isPending()).isFalse();
    }
}
