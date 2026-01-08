package kr.co.jparangdev.boardbuddy.application;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ApplicationModuleConfig.class)
public @interface EnableApplication {
}
