package kr.co.jparangdev.boardbuddy.api.group;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.api.group.dto.GroupDto;
import kr.co.jparangdev.boardbuddy.application.group.dto.GroupMemberInfo;
import kr.co.jparangdev.boardbuddy.domain.group.Group;

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

    public GroupDto.MemberResponse toMemberResponse(GroupMemberInfo info) {
        return GroupDto.MemberResponse.builder()
            .id(info.userId())
            .nickname(info.nickname())
            .discriminator(info.discriminator())
            .userTag(info.userTag())
            .joinedAt(info.joinedAt())
            .status(info.status().name())
            .build();
    }

    public GroupDto.GroupListResponse toGroupListResponse(List<Group> groups) {
        List<GroupDto.Response> responses = groups.stream()
            .map(this::toResponse)
            .toList();
        return GroupDto.GroupListResponse.builder()
            .groups(responses)
            .build();
    }

    public GroupDto.MemberListResponse toMemberListResponse(List<GroupMemberInfo> users) {
        List<GroupDto.MemberResponse> members = users.stream()
            .map(this::toMemberResponse)
            .toList();
        return GroupDto.MemberListResponse.builder()
            .members(members)
            .build();
    }
}
