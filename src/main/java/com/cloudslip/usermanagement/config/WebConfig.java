package com.cloudslip.usermanagement.config;

import com.cloudslip.usermanagement.core.CustomMongoTemplate;
import com.cloudslip.usermanagement.core.CustomRestTemplate;
import com.cloudslip.usermanagement.core.CustomSimpleMongoRepository;
import com.google.gson.JsonParser;
import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMongoRepositories(basePackages = "com.cloudslip.usermanagement.repository", repositoryBaseClass = CustomSimpleMongoRepository.class)
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("x-auth-token", "Content-Type")
                .exposedHeaders("Content-Type")
                .allowCredentials(false).maxAge(3600);
    }

    @Bean
    CustomMongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MongoConverter converter) {
        CustomMongoTemplate mongoTemplate = new CustomMongoTemplate(mongoDbFactory, converter);
        mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        return mongoTemplate;
    }

    @Bean
    JsonParser jsonParser() {
        return new JsonParser();
    }


    @Bean
    public CustomRestTemplate restTemplate() {
        return new CustomRestTemplate();
    }
}
