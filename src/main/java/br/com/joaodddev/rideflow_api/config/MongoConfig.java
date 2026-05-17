package br.com.joaodddev.rideflow_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
// Ensure Spring Data scans the correct package for repositories in this project.
@EnableMongoRepositories(basePackages = "br.com.joaodddev.rideflow_api.repository")
public class MongoConfig {
    // Spring Boot auto-configures the MongoClient from application.yml.
    // This class enables @CreatedDate / @LastModifiedDate auditing on documents.
}