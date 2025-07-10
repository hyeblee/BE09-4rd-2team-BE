package com.playblog.blogservice.postservice.conifg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .anyRequest().permitAll(); // 모든 요청 허용
//                .requestMatchers("/api/posts", "/ftp/**").permitAll()
//                .anyRequest().authenticated();
        return http.build();
    }
}
