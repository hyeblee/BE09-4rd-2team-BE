package com.playblog.blogservice.common.config;

import com.playblog.blogservice.common.jwt.HeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HeaderAuthenticationFilter headerAuthenticationFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/posts/neighbors", "/api/posts/neighbors/**").authenticated()
                        .anyRequest().permitAll()
                )
                // gateway에서 전달한 헤더를 읽어 인증 처리하는 필터 등록
                .addFilterBefore(headerAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//                .requestMatchers("/api/posts", "/api/posts/all", "/ftp/**").permitAll()
//                .anyRequest().authenticated();
        return http.build();
    }
    @Bean
    public HeaderAuthenticationFilter headerAuthenticationFilter() {
        return new HeaderAuthenticationFilter();
    }
}
