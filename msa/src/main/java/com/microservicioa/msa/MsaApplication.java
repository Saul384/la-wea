package com.microservicioa.msa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MsaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsaApplication.class, args);
    }
}
