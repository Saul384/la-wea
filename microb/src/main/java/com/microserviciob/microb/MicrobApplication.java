package com.microserviciob.microb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MicrobApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicrobApplication.class, args);
    }
}
