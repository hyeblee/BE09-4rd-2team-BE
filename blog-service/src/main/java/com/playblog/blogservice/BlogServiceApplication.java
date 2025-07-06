package com.playblog.blogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@EnableJpaAuditing
public class BlogServiceApplication {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 토큰 설정이 없어서 일단 실행될 수 있게 security 비활성화
        http.csrf(csrf -> csrf.disable())  // 새로운 문법
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(BlogServiceApplication.class, args);
    }

}
