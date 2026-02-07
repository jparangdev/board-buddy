package kr.co.jparangdev.boardbuddy.api.group;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.jparangdev.boardbuddy.api.group.dto.GroupDto;
import kr.co.jparangdev.boardbuddy.application.group.usecase.*;
import kr.co.jparangdev.boardbuddy.domain.group.Group;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Group", description = "모임 관리 API")
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final InviteMemberUseCase inviteMemberUseCase;
    private final GetGroupMembersUseCase getGroupMembersUseCase;
    private final GetGroupDetailUseCase getGroupDetailUseCase;
    private final GetMyGroupsUseCase getMyGroupsUseCase;
    private final DeleteGroupUseCase deleteGroupUseCase;
    private final GroupDtoMapper mapper;

    @PostMapping
    @Operation(summary = "모임 생성", description = "새로운 모임을 생성합니다. 생성자는 자동으로 owner이자 첫 번째 멤버가 됩니다.")
    public ResponseEntity<GroupDto.Response> createGroup(
            @Valid @RequestBody GroupDto.CreateRequest request) {
        List<Long> memberIds = request.getMemberIds() != null ? request.getMemberIds() : List.of();
        Group group = createGroupUseCase.createGroup(request.getName(), memberIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(group));
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "멤버 초대", description = "모임에 새로운 멤버를 초대합니다. Owner만 초대할 수 있습니다.")
    public ResponseEntity<GroupDto.MemberResponse> inviteMember(
            @PathVariable("id") Long id,
            @Valid @RequestBody GroupDto.InviteMemberRequest request) {
        inviteMemberUseCase.inviteMember(id, request.getUserTag());
        List<User> members = getGroupMembersUseCase.getGroupMembers(id);
        User invitedUser = members.stream()
                .filter(user -> user.getUserTag().equals(request.getUserTag()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Invited user not found in group members"));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toMemberResponse(invitedUser));
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "멤버 조회", description = "모임의 멤버 목록을 조회합니다. 모임 멤버만 조회할 수 있습니다.")
    public ResponseEntity<GroupDto.MemberListResponse> getGroupMembers(@PathVariable("id") Long id) {
        List<User> members = getGroupMembersUseCase.getGroupMembers(id);
        return ResponseEntity.ok(mapper.toMemberListResponse(members));
    }

    @GetMapping("/{id}")
    @Operation(summary = "모임 상세 조회", description = "모임의 상세 정보를 조회합니다. 모임 멤버만 조회할 수 있습니다.")
    public ResponseEntity<GroupDto.Response> getGroupDetail(@PathVariable("id") Long id) {
        Group group = getGroupDetailUseCase.getGroupDetail(id);
        return ResponseEntity.ok(mapper.toResponse(group));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "모임 삭제", description = "모임을 삭제합니다. Owner만 삭제할 수 있습니다.")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long id) {
        deleteGroupUseCase.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "내 모임 목록 조회", description = "현재 로그인한 사용자가 속한 모임 목록을 조회합니다.")
    public ResponseEntity<GroupDto.GroupListResponse> getMyGroups() {
        List<Group> groups = getMyGroupsUseCase.getMyGroups();
        return ResponseEntity.ok(mapper.toGroupListResponse(groups));
    }
}
