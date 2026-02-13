package kr.co.jparangdev.boardbuddy.batch;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(PresentationBatchModuleConfig.class)
public @interface EnablePresentationBatch {
}
