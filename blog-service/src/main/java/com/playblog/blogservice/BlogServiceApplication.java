package com.playblog.blogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {
        "com.playblog.blogservice.comment",
        "com.playblog.blogservice.postlike",
        "com.playblog.blogservice.common",
        "com.playblog.blogservice.userInfo",
        "com.playblog.blogservice.user"
})
@EnableJpaAuditing
@EntityScan(basePackages = {
        "com.playblog.blogservice.comment.entity",
        "com.playblog.blogservice.postlike.entity",
        "com.playblog.blogservice.user",
        "com.playblog.blogservice.userInfo",

})
@EnableJpaRepositories(basePackages = {
        "com.playblog.blogservice.comment.repository",
        "com.playblog.blogservice.postlike.repository",
        "com.playblog.blogservice.common.repository",
        "com.playblog.blogservice.userInfo"
})
public class BlogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogServiceApplication.class, args);
    }

}
