package kr.co.jparangdev.boardbuddies.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "kr.co.jparangdev.boardbuddies",
})
public class BoardbuddiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardbuddiesApplication.class, args);
    }
}
