package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableMongoRepositories(basePackages = "com.repositories")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return "hotpizzadb";
    }

    @Bean
    public MongoClient mongoClient() {
        try {
            MongoClient client = MongoClients.create(mongoUri);
            System.out.println("Connexion MongoDB réussie avec l'URI : " + mongoUri);
            return client;
        } catch (Exception e) {
            System.err.println("Erreur de connexion MongoDB : " + e.getMessage());
            throw e;
        }
    }
}