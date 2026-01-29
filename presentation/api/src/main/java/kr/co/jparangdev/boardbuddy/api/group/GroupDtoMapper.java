package kr.co.jparangdev.boardbuddy.api.group;

import kr.co.jparangdev.boardbuddy.api.group.dto.GroupDto;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.group.GroupMember;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GroupDtoMapper {

    public GroupDto.Response toResponse(Group group) {
        return GroupDto.Response.builder()
            .id(group.getId())
            .name(group.getName())
            .ownerId(group.getOwnerId())
            .createdAt(group.getCreatedAt())
            .build();
    }

    public GroupDto.MemberResponse toMemberResponse(User user, GroupMember membership) {
        return GroupDto.MemberResponse.builder()
            .id(user.getId())
            .nickname(user.getNickname())
            .discriminator(user.getDiscriminator())
            .userTag(user.getUserTag())
            .joinedAt(membership != null ? membership.getJoinedAt() : null)
            .build();
    }

    public GroupDto.MemberResponse toMemberResponse(User user) {
        return GroupDto.MemberResponse.builder()
            .id(user.getId())
            .nickname(user.getNickname())
            .discriminator(user.getDiscriminator())
            .userTag(user.getUserTag())
            .build();
    }

    public GroupDto.GroupListResponse toGroupListResponse(List<Group> groups) {
        List<GroupDto.Response> responses = groups.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return GroupDto.GroupListResponse.builder()
            .groups(responses)
            .build();
    }

    public GroupDto.MemberListResponse toMemberListResponse(List<User> users) {
        List<GroupDto.MemberResponse> members = users.stream()
            .map(this::toMemberResponse)
            .collect(Collectors.toList());
        return GroupDto.MemberListResponse.builder()
            .members(members)
            .build();
    }
}
