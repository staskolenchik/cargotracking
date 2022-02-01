package com.itechart.cargotrucking.core.solr.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableSolrRepositories("com.itechart.cargotrucking.core.solr")
@ComponentScan("com.itechart.cargotrucking.core.solr")
public class SolrConfiguration {
}
