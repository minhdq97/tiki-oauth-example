package com.example.tikioauthexample.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class OAuthClientConfig {

    @Bean
    @ConfigurationProperties(prefix = "oauth.client.provider")
    public OAuthClientProperties oAuthClientProperties() {
        return new OAuthClientProperties();
    }

    @Bean
    public OAuthCredentialsProvider oAuthCredentialsProvider() {
        OAuthClientProperties properties = oAuthClientProperties();
        return new OAuthCredentialsProvider()
            .authServer(properties.getAuthorizationServer())
            .client(properties.getClientId(), properties.getClientSecret())
            .tokenAuthMethod(properties.getAuthenticationMethod())
            .endpoint("default-client")
            .connectTimeout(Duration.ofSeconds(5));
    }
}
