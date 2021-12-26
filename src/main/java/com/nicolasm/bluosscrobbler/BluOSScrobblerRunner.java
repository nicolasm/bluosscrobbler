package com.nicolasm.bluosscrobbler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BluOSScrobblerRunner {
    public static void main(String[] args) {
        SpringApplication.run(BluOSScrobblerRunner.class);
    }
}
