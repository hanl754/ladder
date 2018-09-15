package com.tkmao.ladder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.tkmao")
public class LadderApplication {

    public static void main(String[] args) {
        SpringApplication.run(LadderApplication.class, args);
    }
}
