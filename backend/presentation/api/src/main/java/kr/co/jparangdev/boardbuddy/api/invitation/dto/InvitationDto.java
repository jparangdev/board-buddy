package kr.co.jparangdev.boardbuddy.api.invitation.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvitationDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "Invitee ID is required")
        private Long inviteeId;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long groupId;
        private String groupName;
        private Long inviterId;
        private String inviterNickname;
        private Long inviteeId;
        private Instant createdAt;
    }

    @Getter
    @Builder
    public static class InvitationListResponse {
        private List<Response> invitations;
    }
}
