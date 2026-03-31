package kr.co.jparangdev.boardbuddy.api.group;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import kr.co.jparangdev.boardbuddy.api.group.dto.GroupDto;
import kr.co.jparangdev.boardbuddy.application.group.dto.GroupMemberInfo;
import kr.co.jparangdev.boardbuddy.application.group.dto.GroupMemberStatus;
import kr.co.jparangdev.boardbuddy.application.group.usecase.*;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(GroupController.class)
@AutoConfigureJson
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private GroupCommandUseCase groupCommandUseCase;

    @MockitoBean
    private GroupQueryUseCase groupQueryUseCase;

    @MockitoBean
    private UpdateGroupOrderUseCase updateGroupOrderUseCase;

    @MockitoBean
    private GroupDtoMapper mapper;


    @Test
    @DisplayName("Create Group Success")
    @WithMockUser
    void createGroupSuccess() throws Exception {
        // given
        GroupDto.CreateRequest request = GroupDto.CreateRequest.builder()
                .name("New Group")
                .memberIds(List.of(2L, 3L))
                .build();

        Group group = Group.builder()
                .id(1L)
                .name("New Group")
                .ownerId(1L)
                .createdAt(Instant.now())
                .build();

        GroupDto.Response response = GroupDto.Response.builder()
                .id(1L)
                .name("New Group")
                .ownerId(1L)
                .createdAt(Instant.now())
                .build();

        given(groupCommandUseCase.createGroup(any(String.class), anyList())).willReturn(group);
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
    @DisplayName("Get Group Members Success")
    @WithMockUser
    void getGroupMembersSuccess() throws Exception {
        // given
        Long groupId = 1L;
        GroupMemberInfo info = new GroupMemberInfo(2L, "nick", "0001", "nick#0001", null, GroupMemberStatus.ACTIVE);
        List<GroupMemberInfo> members = List.of(info);

        GroupDto.MemberResponse memberResponse = GroupDto.MemberResponse.builder()
                .id(2L)
                .status("ACTIVE")
                .build();
        GroupDto.MemberListResponse response = GroupDto.MemberListResponse.builder()
                .members(List.of(memberResponse))
                .build();

        given(groupQueryUseCase.getGroupMembers(groupId)).willReturn(members);
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

        given(groupQueryUseCase.getGroupDetail(groupId)).willReturn(group);
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

        given(groupQueryUseCase.getMyGroups()).willReturn(groups);
        given(mapper.toGroupListResponse(groups)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/groups"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups[0].name").value("My Group"));
    }

    @Test
    @DisplayName("Delete Group Success")
    @WithMockUser
    void deleteGroupSuccess() throws Exception {
        // given
        Long groupId = 1L;
        doNothing().when(groupCommandUseCase).deleteGroup(groupId);

        // when & then
        mockMvc.perform(delete("/api/v1/groups/{id}", groupId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Reorder Groups Success")
    @WithMockUser
    void reorderGroupsSuccess() throws Exception {
        // given
        List<Long> groupIds = List.of(3L, 1L, 2L);
        doNothing().when(updateGroupOrderUseCase).updateGroupOrder(groupIds);

        // when & then
        mockMvc.perform(put("/api/v1/groups/order")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(groupIds)))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
