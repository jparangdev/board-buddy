package kr.co.jparangdev.boardbuddy.application.invitation.usecase;

import java.util.List;

import kr.co.jparangdev.boardbuddy.application.invitation.dto.InvitationInfo;

public interface InvitationQueryUseCase {
    List<InvitationInfo> getMyPendingInvitations();
}
