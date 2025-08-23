package kr.co.jparangdev.boardbuddies.dal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddies.application.repostory.UserRepository;
import kr.co.jparangdev.boardbuddies.dal.entity.UserJpaEntity;
import kr.co.jparangdev.boardbuddies.dal.mapper.UserMapper;
import kr.co.jparangdev.boardbuddies.domain.entity.User;
import lombok.RequiredArgsConstructor;

/**
 * PostgreSQL Adapter implementing UserManagementOutputPort
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;
	private final UserMapper userMapper;

	@Override
	public User save(User user) {
		UserJpaEntity entity = userMapper.toEntity(user);
		UserJpaEntity saved = userJpaRepository.save(entity);
		return userMapper.toDomain(saved);
	}

	@Override
	public Optional<User> findById(Long id) {
		return userJpaRepository.findById(id).map(userMapper::toDomain);
	}

	@Override
	public List<User> findAll() {
		return userJpaRepository.findAll().stream()
			.map(userMapper::toDomain)
			.toList();
	}

	@Override
	public void deleteById(Long id) {
		userJpaRepository.deleteById(id);
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return userJpaRepository.findByUsername(username).map(userMapper::toDomain);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userJpaRepository.findByEmail(email).map(userMapper::toDomain);
	}
}
