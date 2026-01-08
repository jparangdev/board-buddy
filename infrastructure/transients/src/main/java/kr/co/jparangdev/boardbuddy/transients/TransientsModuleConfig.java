package kr.co.jparangdev.boardbuddy.transients;

import kr.co.jparangdev.boardbuddy.config.RedisConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "kr.co.jparangdev.boardbuddy.transients")
@Import(RedisConfig.class)
public class TransientsModuleConfig {
}
