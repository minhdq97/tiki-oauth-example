package com.example.tikioauthexample;

import com.example.tikioauthexample.oauth.OAuthCredentialsProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TikiOauthExampleApplicationTests {

    @Autowired
    OAuthCredentialsProvider provider;

    @Test
    public void testGetAccessToken() {
        String token = provider.getAuthorization();
        System.out.println(token);
    }

}
