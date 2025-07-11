package com.playblog.blogservice.ftp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ftp")
public class FtpProperties {
    private String server;
    private int port;
    private String user;
    private String pass;
    private String remoteDir;
    private String baseUrl;
}
