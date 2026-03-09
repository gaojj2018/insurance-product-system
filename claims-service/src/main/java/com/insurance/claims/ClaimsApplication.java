package com.insurance.claims;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.insurance.claims.repository")
public class ClaimsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClaimsApplication.class, args);
    }
}
