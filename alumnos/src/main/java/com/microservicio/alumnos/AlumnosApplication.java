package com.microservicio.alumnos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AlumnosApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlumnosApplication.class, args);
    }

}
