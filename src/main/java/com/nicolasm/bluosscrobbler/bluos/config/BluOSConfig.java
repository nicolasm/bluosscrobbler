package com.nicolasm.bluosscrobbler.bluos.config;

import com.nicolasm.bluosscrobbler.bluos.model.BluOSEndpoint;
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

    public String getStatusPollingEndpoint() {
        return BluOSEndpoint.STATUS_POLLING.getEndpoint(getBaseEndpoint());
    }

    public String getStatusLongPollingEndpoint() {
        return BluOSEndpoint.STATUS_LONG_POLLING.getEndpoint(getBaseEndpoint());
    }

    private String getBaseEndpoint() {
        return String.format("http://%s:%d", host, port);
    }
}
