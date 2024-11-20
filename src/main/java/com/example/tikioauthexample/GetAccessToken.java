package com.example.tikioauthexample;

import com.example.tikioauthexample.oauth.OAuthCredentialsProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAccessToken {
    private final OAuthCredentialsProvider oauthProvider;

    public String getToken() {
        return oauthProvider.getAuthorization();
    }
}
