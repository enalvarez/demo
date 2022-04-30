package es.meep.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MeepDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(MeepDemoApp.class, args);
    }
}
