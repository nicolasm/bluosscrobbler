package com.nicolasm.bluosscrobbler.bluos.config;

import com.nicolasm.service.bluosscrobbler.bluos.model.BluOSRawStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
@ConfigurationProperties("application.bluos")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BluOSConfig {
    private String host;
    private int port;
    private List<String> excludedServices;

    public String getBaseEndpoint() {
        return String.format("http://%s:%d", host, port);
    }

    public boolean isServiceEnabled(BluOSRawStatus status) {
        return Optional.ofNullable(excludedServices)
                .map(services -> services.stream()
                        .noneMatch(s -> StringUtils.containsAnyIgnoreCase(s, status.getService(), status.getServiceName())))
                .orElse(true);
    }
}
