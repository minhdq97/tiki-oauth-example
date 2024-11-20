package com.example.tikioauthexample.oauth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClientCredentials {
    private String accessToken;

    private final LocalDateTime creationAt = LocalDateTime.now();

    private Long expiresIn;

    private String tokenType;

    private String scope;

    public String getAuthorization() {
        if (tokenType.equalsIgnoreCase("bearer")) {
            return String.format("Bearer %s", accessToken);
        }

        return String.format("%s %s", tokenType, accessToken);
    }

    @JsonIgnore
    public boolean isValid() {
        return creationAt.plusSeconds((expiresIn - 3600L)).isAfter(LocalDateTime.now());
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }

        final ClientCredentials cred = (ClientCredentials) o;

        return accessToken.equals(cred.accessToken);
    }
}
