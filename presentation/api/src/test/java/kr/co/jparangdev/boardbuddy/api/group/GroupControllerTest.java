package kr.co.jparangdev.boardbuddy.api.group;

import tools.jackson.databind.json.JsonMapper;
import kr.co.jparangdev.boardbuddy.api.group.dto.GroupDto;
import kr.co.jparangdev.boardbuddy.application.group.usecase.GroupManagementUseCase;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
@AutoConfigureJson
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private GroupManagementUseCase groupManagementUseCase;

    @MockitoBean
    private GroupDtoMapper mapper;


    @Test
    @DisplayName("Create Group Success")
    @WithMockUser
    void createGroupSuccess() throws Exception {
        // given
        GroupDto.CreateRequest request = GroupDto.CreateRequest.builder()
                .name("New Group")
                .build();

        Group group = Group.builder()
                .id(1L)
                .name("New Group")
                .ownerId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        GroupDto.Response response = GroupDto.Response.builder()
                .id(1L)
                .name("New Group")
                .ownerId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        given(groupManagementUseCase.createGroup("New Group")).willReturn(group);
        given(mapper.toResponse(any(Group.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/groups")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Group"));
    }

    @Test
    @DisplayName("Invite Member Success")
    @WithMockUser
    void inviteMemberSuccess() throws Exception {
        // given
        Long groupId = 1L;
        String userTag = "invitee#1234";
        GroupDto.InviteMemberRequest request = GroupDto.InviteMemberRequest.builder()
                .userTag(userTag)
                .build();

        User invitee = User.builder()
                .id(2L)
                .nickname("invitee")
                .discriminator("1234")
                .build();

        GroupDto.MemberResponse memberResponse = GroupDto.MemberResponse.builder()
                .id(2L)
                .nickname("invitee")
                .discriminator("1234")
                .userTag(userTag)
                .build();

        // Mock inviteMember void/return
        // Service signature: GroupMember inviteMember(Long groupId, String userTag)
        // But Controller ignores return value and calls getGroupMembers

        // Mock getGroupMembers to return list containing the invited user
        given(groupManagementUseCase.getGroupMembers(groupId)).willReturn(List.of(invitee));

        // Mock mapper
        given(mapper.toMemberResponse(any(User.class))).willReturn(memberResponse);

        // when & then
        mockMvc.perform(post("/api/v1/groups/{id}/members", groupId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userTag").value(userTag));
    }

    @Test
    @DisplayName("Get Group Members Success")
    @WithMockUser
    void getGroupMembersSuccess() throws Exception {
        // given
        Long groupId = 1L;
        User user = User.builder().id(2L).build();
        List<User> members = List.of(user);

        GroupDto.MemberResponse memberResponse = GroupDto.MemberResponse.builder()
                .id(2L)
                .build();
        GroupDto.MemberListResponse response = GroupDto.MemberListResponse.builder()
                .members(List.of(memberResponse))
                .build();

        given(groupManagementUseCase.getGroupMembers(groupId)).willReturn(members);
        given(mapper.toMemberListResponse(members)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/groups/{id}/members", groupId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members[0].id").value(2));
    }

    @Test
    @DisplayName("Get Group Detail Success")
    @WithMockUser
    void getGroupDetailSuccess() throws Exception {
        // given
        Long groupId = 1L;
        Group group = Group.builder().id(groupId).name("My Group").build();
        GroupDto.Response response = GroupDto.Response.builder().id(groupId).name("My Group").build();

        given(groupManagementUseCase.getGroupDetail(groupId)).willReturn(group);
        given(mapper.toResponse(group)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/groups/{id}", groupId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("My Group"));
    }

    @Test
    @DisplayName("Get My Groups Success")
    @WithMockUser
    void getMyGroupsSuccess() throws Exception {
        // given
        Group group = Group.builder().id(1L).name("My Group").build();
        List<Group> groups = List.of(group);

        GroupDto.Response groupResponse = GroupDto.Response.builder().id(1L).name("My Group").build();
        GroupDto.GroupListResponse response = GroupDto.GroupListResponse.builder()
                .groups(List.of(groupResponse))
                .build();

        given(groupManagementUseCase.getMyGroups()).willReturn(groups);
        given(mapper.toGroupListResponse(groups)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/groups"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups[0].name").value("My Group"));
    }

}
