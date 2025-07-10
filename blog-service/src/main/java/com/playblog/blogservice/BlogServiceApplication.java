package com.playblog.blogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {
        "com.playblog.blogservice.comment",
        "com.playblog.blogservice.post",
        "com.playblog.blogservice.common"
})
@EnableJpaAuditing
@EntityScan(basePackages = {
        "com.playblog.blogservice.comment.entity",
        "com.playblog.blogservice.post.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.playblog.blogservice.comment.repository",
        "com.playblog.blogservice.post.repository"
})
public class BlogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogServiceApplication.class, args);
    }

}
