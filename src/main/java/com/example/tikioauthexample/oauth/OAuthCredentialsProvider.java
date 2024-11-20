package com.example.tikioauthexample.oauth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Accessors(chain = true)
public final class OAuthCredentialsProvider {

    public enum TokenAuthenticationMethod {
        CLIENT_SECRET_BASIC, CLIENT_SECRET_POST
    }

    private static final ObjectMapper JSON_MAPPER = (new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final ObjectReader CREDENTIALS_READER = JSON_MAPPER.readerFor(ClientCredentials.class);
    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);

    private String authorizationServerUrl;
    private String clientId;
    private String clientSecret;

    private TokenAuthenticationMethod tokenAuthMethod = TokenAuthenticationMethod.CLIENT_SECRET_BASIC;
    private String endpoint;
    private final OAuthCredentialsCache credentialsCache;
    private Duration connectionTimeout;
    private ClientCredentials credentials;

    public OAuthCredentialsProvider() {
        this.credentialsCache = OAuthCredentialsCache.getInstance();
        this.connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
    }

    public OAuthCredentialsProvider endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public OAuthCredentialsProvider connectTimeout(Duration timeout) {
        this.connectionTimeout = timeout;
        return this;
    }

    public OAuthCredentialsProvider tokenAuthMethod(String method) {
        this.tokenAuthMethod = TokenAuthenticationMethod.valueOf(method);
        return this;
    }

    public OAuthCredentialsProvider authServer(String authServerUrl) {
        this.authorizationServerUrl = authServerUrl;
        return this;
    }

    public OAuthCredentialsProvider client(String id, String secret) {
        this.clientId = id;
        this.clientSecret = secret;
        return this;
    }


    public String getAuthorization() {
        if (credentials == null) {
            loadCredentials();
        }

        return credentials.getAuthorization();
    }

    private void loadCredentials() {
        Optional<ClientCredentials> cachedCredentials;
        cachedCredentials = credentialsCache.get(endpoint);

        if (cachedCredentials.isPresent() && cachedCredentials.get().isValid()) {
            credentials = cachedCredentials.get();
        } else {
            refreshCredentials();
        }
    }

    public void refreshCredentials() {
        ClientCredentials cred = fetchCredentials();
        credentialsCache.put(endpoint, cred);

        if (credentials == null || !credentials.isValid()) {
            credentials = cred;
        }
    }

    private ClientCredentials fetchCredentials() {
        log.info("Fetch OAuth credentials at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("grant_type", "client_credentials");

        Request.Builder requestBuilder = new Request.Builder()
                .url(authorizationServerUrl)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json");

        if (tokenAuthMethod == TokenAuthenticationMethod.CLIENT_SECRET_BASIC) {
            String clientCred = String.format("%s:%s", clientId, clientSecret);
            requestBuilder.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(clientCred.getBytes()));
        } else if (tokenAuthMethod == TokenAuthenticationMethod.CLIENT_SECRET_POST) {
            bodyBuilder.add("client_id", clientId);
            bodyBuilder.add("client_secret", clientSecret);
        }

        Request request = requestBuilder.post(bodyBuilder.build()).build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(connectionTimeout)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected status code: " + response);
            }
            assert response.body() != null;
            String token = response.body().string();

            return CREDENTIALS_READER.readValue(token);
        } catch (IOException e) {
            log.error("Failed to fetch credentials ", e);
        }

        return null;
    }
}
