package kr.co.jparangdev.boardbuddies.dal.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"kr.co.jparangdev.boardbuddies.dal.repository"})
@EntityScan(basePackages = "kr.co.jparangdev.boardbuddies.dal.entity")
public class JpaConfig {
}
