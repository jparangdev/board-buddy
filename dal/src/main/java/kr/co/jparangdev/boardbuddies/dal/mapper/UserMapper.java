package kr.co.jparangdev.boardbuddies.dal.mapper;

import org.mapstruct.Mapper;

import kr.co.jparangdev.boardbuddies.dal.entity.UserJpaEntity;
import kr.co.jparangdev.boardbuddies.domain.entity.User;

/**
 * Mapper for converting between domain User and infrastructure UserEntity
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

	/**
	 * Convert from domain User to infrastructure UserEntity
	 * @param user domain User
	 * @return infrastructure UserEntity
	 */
	UserJpaEntity toEntity(User user);

	/**
	 * Convert from infrastructure UserEntity to domain User
	 * @param userJpaEntity infrastructure UserEntity
	 * @return domain User
	 */
	User toDomain(UserJpaEntity userJpaEntity);
}
