package kr.co.jparangdev.boardbuddy;

import kr.co.jparangdev.boardbuddy.api.EnablePresentationApi;
import kr.co.jparangdev.boardbuddy.application.config.EnableApplication;
import kr.co.jparangdev.boardbuddy.client.EnableClient;
import kr.co.jparangdev.boardbuddy.persistence.EnablePersistence;
import kr.co.jparangdev.boardbuddy.transients.EnableTransients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableClient
@EnablePresentationApi
@EnableApplication
@EnablePersistence
@EnableTransients
@SpringBootApplication
public class ApiServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiServerApplication.class, args);
    }
}
