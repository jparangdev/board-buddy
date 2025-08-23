package kr.co.jparangdev.boardbuddies.dal.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity for User
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String nickname;

	public UserJpaEntity(Long id, String username, String email, String nickname) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.nickname = nickname;
	}
}
