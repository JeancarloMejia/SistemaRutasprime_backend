package com.backend.avance1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Avance1Application {

    public static void main(String[] args) {
        SpringApplication.run(Avance1Application.class, args);
    }
}