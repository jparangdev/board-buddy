package kr.co.jparangdev.boardbuddies.domain.entity;

import lombok.*;

/**
 * User domain entity representing a board game player
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	private Long id;
	private String username;
	private String email;
	private String nickname;

	@Builder
	public User(Long id, String username, String email, String nickname) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.nickname = nickname;
	}

	/**
	 * Updates user information
	 */
	public void update(String username, String email, String nickname) {
		this.username = username;
		this.email = email;
		this.nickname = nickname;
	}
}
