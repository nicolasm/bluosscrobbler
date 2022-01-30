package com.nicolasm.bluosscrobbler.scrobbler.lastfm.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("application.scrobblers.lastfm")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LastfmConfig {
    private String apiUrl;
    private String authUrl;
    private String apiKey;
    private String sharedSecret;
    private String sessionKey;
    private boolean enabled;
}
