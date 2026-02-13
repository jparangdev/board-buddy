package kr.co.jparangdev.boardbuddy;

import kr.co.jparangdev.boardbuddy.application.config.EnableApplication;
import kr.co.jparangdev.boardbuddy.batch.EnablePresentationBatch;
import kr.co.jparangdev.boardbuddy.persistence.EnablePersistence;
import kr.co.jparangdev.boardbuddy.transients.EnableTransients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnablePresentationBatch
@EnableApplication
@EnablePersistence
@EnableTransients
@SpringBootApplication
public class BatchServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchServerApplication.class, args);
    }
}
