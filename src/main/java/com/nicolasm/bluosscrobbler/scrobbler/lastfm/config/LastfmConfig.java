package com.nicolasm.bluosscrobbler.scrobbler.lastfm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("application.scrobblers.lastfm")
@Data
public class LastfmConfig {
    private String apiUrl;
    private String authUrl;
    private String apiKey;
    private String sharedSecret;
    private String sessionKey;
    private boolean enabled;
}
