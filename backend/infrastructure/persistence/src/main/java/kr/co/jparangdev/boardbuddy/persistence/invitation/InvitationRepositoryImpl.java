package kr.co.jparangdev.boardbuddy.persistence.invitation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.co.jparangdev.boardbuddy.domain.invitation.Invitation;
import kr.co.jparangdev.boardbuddy.domain.invitation.InvitationStatus;
import kr.co.jparangdev.boardbuddy.domain.invitation.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InvitationRepositoryImpl implements InvitationRepository {

    private final InvitationJpaRepository jpaRepository;
    private final InvitationMapper mapper;

    @Override
    public Invitation save(Invitation invitation) {
        InvitationJpaEntity entity = mapper.toEntity(invitation);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Invitation> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Invitation> findAllByInviteeIdAndStatus(Long inviteeId, InvitationStatus status) {
        return jpaRepository.findAllByInviteeIdAndStatus(inviteeId, status).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByGroupIdAndInviteeIdAndStatus(Long groupId, Long inviteeId, InvitationStatus status) {
        return jpaRepository.existsByGroupIdAndInviteeIdAndStatus(groupId, inviteeId, status);
    }
}
