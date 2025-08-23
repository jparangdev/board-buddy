package kr.co.jparangdev.boardbuddies.web.adapters.input.rest.mapper;

import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddies.domain.entity.User;
import kr.co.jparangdev.boardbuddies.web.adapters.input.rest.dto.UserDto;

/**
 * Mapper for converting between domain User and DTOs
 */
@Component
public class UserDtoMapper {

	/**
	 * Convert from CreateRequest DTO to domain User
	 * @param createRequest the create request DTO
	 * @return domain User
	 */
	public User toDomain(UserDto.CreateRequest createRequest) {
		if (createRequest == null) {
			return null;
		}

		return User.builder()
			.username(createRequest.getUsername())
			.email(createRequest.getEmail())
			.nickname(createRequest.getNickname())
			.build();
	}

	/**
	 * Convert from UpdateRequest DTO to domain User
	 * @param updateRequest the update request DTO
	 * @return domain User
	 */
	public User toDomain(UserDto.UpdateRequest updateRequest) {
		if (updateRequest == null) {
			return null;
		}

		return User.builder()
			.username(updateRequest.getUsername())
			.email(updateRequest.getEmail())
			.nickname(updateRequest.getNickname())
			.build();
	}

	/**
	 * Convert from domain User to Response DTO
	 * @param user domain User
	 * @return response DTO
	 */
	public UserDto.Response toResponse(User user) {
		if (user == null) {
			return null;
		}

		return UserDto.Response.builder()
			.id(user.getId())
			.username(user.getUsername())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.build();
	}
}
