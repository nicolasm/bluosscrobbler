package com.nicolasm.bluosscrobbler.bluos.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("application.bluos")
@Data
public class BluOSConfig {
    private String host;
    private int port;
    private int timeout;
    private List<String> excludedServices;

    public String getBaseEndpoint() {
        return String.format("http://%s:%d", host, port);
    }
}
