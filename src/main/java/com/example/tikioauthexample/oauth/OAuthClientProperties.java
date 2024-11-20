package com.example.tikioauthexample.oauth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthClientProperties {
    private String authorizationServer;
    private String clientId;
    private String clientSecret;
    private String authenticationMethod;
}
