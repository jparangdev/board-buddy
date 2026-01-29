package kr.co.jparangdev.boardbuddy.api.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupDto {

    @Getter
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "Group name is required")
        @Size(max = 100, message = "Group name must not exceed 100 characters")
        private String name;
    }

    @Getter
    @Builder
    public static class InviteMemberRequest {
        @NotBlank(message = "User tag is required")
        private String userTag;  // 닉네임#discriminator
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private Long ownerId;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class DetailResponse {
        private Long id;
        private String name;
        private Long ownerId;
        private LocalDateTime createdAt;
        private List<MemberResponse> members;
    }

    @Getter
    @Builder
    public static class MemberResponse {
        private Long id;
        private String nickname;
        private String discriminator;
        private String userTag;
        private LocalDateTime joinedAt;
    }

    @Getter
    @Builder
    public static class GroupListResponse {
        private List<Response> groups;
    }

    @Getter
    @Builder
    public static class MemberListResponse {
        private List<MemberResponse> members;
    }
}
