package kr.co.jparangdev.boardbuddy.application.game.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.game.exception.CustomGameNotFoundException;
import kr.co.jparangdev.boardbuddy.application.game.exception.DuplicateCustomGameNameException;
import kr.co.jparangdev.boardbuddy.application.game.usecase.CustomGameCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.CustomGameQueryUseCase;
import kr.co.jparangdev.boardbuddy.application.group.exception.GroupNotFoundException;
import kr.co.jparangdev.boardbuddy.application.user.exception.UserNotGroupMemberException;
import kr.co.jparangdev.boardbuddy.domain.game.CustomGame;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import kr.co.jparangdev.boardbuddy.domain.game.repository.CustomGameRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupMemberRepository;
import kr.co.jparangdev.boardbuddy.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomGameManagementService implements CustomGameQueryUseCase, CustomGameCommandUseCase {

    private final CustomGameRepository customGameRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public List<CustomGame> getCustomGamesByGroup(Long groupId) {
        Long currentUserId = getCurrentUserId();

        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new UserNotGroupMemberException(groupId, currentUserId);
        }

        return customGameRepository.findAllByGroupId(groupId);
    }

    @Override
    public CustomGame getCustomGameDetail(Long customGameId) {
        return customGameRepository.findById(customGameId)
                .orElseThrow(() -> new CustomGameNotFoundException(customGameId));
    }

    @Override
    @Transactional
    public CustomGame createCustomGame(Long groupId, String name, int minPlayers, int maxPlayers,
                                        ScoreStrategy scoreStrategy) {
        Long currentUserId = getCurrentUserId();

        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new UserNotGroupMemberException(groupId, currentUserId);
        }

        if (customGameRepository.existsByGroupIdAndName(groupId, name)) {
            throw new DuplicateCustomGameNameException(groupId, name);
        }

        CustomGame customGame = CustomGame.create(groupId, name, minPlayers, maxPlayers, scoreStrategy);
        return customGameRepository.save(customGame);
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is missing");
        }
        return (Long) authentication.getPrincipal();
    }
}
