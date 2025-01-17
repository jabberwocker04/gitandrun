package com.sparta.gitandrun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GitandrunApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitandrunApplication.class, args);
    }

}
