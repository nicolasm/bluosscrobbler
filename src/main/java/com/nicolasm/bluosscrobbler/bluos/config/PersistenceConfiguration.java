package com.nicolasm.bluosscrobbler.bluos.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan({"com.nicolasm.bluosscrobbler.bluos.persistence"})
@EnableJpaRepositories({"com.nicolasm.bluosscrobbler.bluos.persistence"})
public class PersistenceConfiguration {
}
