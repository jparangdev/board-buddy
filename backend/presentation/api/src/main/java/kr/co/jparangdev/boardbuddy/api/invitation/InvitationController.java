package kr.co.jparangdev.boardbuddy.api.invitation;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.invitation.dto.InvitationDto;
import kr.co.jparangdev.boardbuddy.application.invitation.usecase.InvitationCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.invitation.usecase.InvitationQueryUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationCommandUseCase invitationCommandUseCase;
    private final InvitationQueryUseCase invitationQueryUseCase;
    private final InvitationDtoMapper dtoMapper;

    @PostMapping("/api/v1/groups/{groupId}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public void inviteUser(@PathVariable Long groupId,
                            @Valid @RequestBody InvitationDto.CreateRequest request) {
        invitationCommandUseCase.inviteUser(groupId, request.getInviteeId());
    }

    @GetMapping("/api/v1/invitations/pending")
    public InvitationDto.InvitationListResponse getMyPendingInvitations() {
        List<InvitationDto.Response> responses = invitationQueryUseCase.getMyPendingInvitations()
                .stream()
                .map(dtoMapper::toResponse)
                .toList();
        return InvitationDto.InvitationListResponse.builder()
                .invitations(responses)
                .build();
    }

    @PostMapping("/api/v1/invitations/{id}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptInvitation(@PathVariable Long id) {
        invitationCommandUseCase.respondToInvitation(id, true);
    }

    @PostMapping("/api/v1/invitations/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectInvitation(@PathVariable Long id) {
        invitationCommandUseCase.respondToInvitation(id, false);
    }
}
