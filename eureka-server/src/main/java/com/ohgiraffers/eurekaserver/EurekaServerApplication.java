package com.ohgiraffers.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
/* 해당 Application을 Eureka Server로 활성화 */
@EnableEurekaServer
public class EurekaServerApplication {
// test
	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
