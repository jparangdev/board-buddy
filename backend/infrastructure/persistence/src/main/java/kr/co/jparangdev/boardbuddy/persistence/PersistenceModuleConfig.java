package kr.co.jparangdev.boardbuddy.persistence;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = "kr.co.jparangdev.boardbuddy.persistence")
@EnableJpaRepositories(basePackages = "kr.co.jparangdev.boardbuddy.persistence")
@EntityScan(basePackages = "kr.co.jparangdev.boardbuddy.persistence")
@EnableScheduling
public class PersistenceModuleConfig {
}
