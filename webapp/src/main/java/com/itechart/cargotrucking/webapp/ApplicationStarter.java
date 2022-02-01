package com.itechart.cargotrucking.webapp;

import com.itechart.cargotrucking.core.common.config.JpaConfiguration;
import com.itechart.cargotrucking.core.common.config.RetryConfigure;
import com.itechart.cargotrucking.core.common.config.ScheduleConfigure;
import com.itechart.cargotrucking.core.solr.config.SolrConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({JpaConfiguration.class, SolrConfiguration.class, ScheduleConfigure.class, RetryConfigure.class})
public class ApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationStarter.class, args);
    }
}
