package com.example.tikioauthexample.oauth;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class OAuthCredentialsCache {
    private static OAuthCredentialsCache CACHE;

    public static OAuthCredentialsCache getInstance() {
        return CACHE != null ? CACHE : (CACHE = new OAuthCredentialsCache());
    }

    private final Map<String, ClientCredentials> endpoints;
    public OAuthCredentialsCache() {
        endpoints = new ConcurrentHashMap<>();
    }

    public Optional<ClientCredentials> get(String endpoint) {
        return Optional.ofNullable(endpoints.get(endpoint));
    }

    public void put(String endpoint, ClientCredentials credentials) {
        endpoints.put(endpoint, credentials);
    }
}
