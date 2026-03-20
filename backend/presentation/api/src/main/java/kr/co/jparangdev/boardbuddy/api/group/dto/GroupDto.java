package kr.co.jparangdev.boardbuddy.api.group.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Group name is required")
        @Size(max = 100, message = "Group name must not exceed 100 characters")
        private String name;

        private List<Long> memberIds;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private Long ownerId;
        private Instant createdAt;
    }

    @Getter
    @Builder
    public static class DetailResponse {
        private Long id;
        private String name;
        private Long ownerId;
        private Instant createdAt;
        private List<MemberResponse> members;
    }

    @Getter
    @Builder
    public static class MemberResponse {
        private Long id;
        private String nickname;
        private String discriminator;
        private String userTag;
        private Instant joinedAt;
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
