package com.itechart.cargotrucking.core.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.itechart.cargotrucking.core")
@EntityScan("com.itechart.cargotrucking.core")
@ComponentScan("com.itechart.cargotrucking.core")
public class JpaConfiguration {
}
