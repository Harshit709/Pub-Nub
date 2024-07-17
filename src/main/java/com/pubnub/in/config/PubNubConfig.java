package com.pubnub.in.config;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.UserId;
import com.pubnub.api.enums.PNLogVerbosity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubNubConfig {
    @Value("${pubnub.publish.key}")
    private String publishKey;

    @Value("${pubnub.subscribe.key}")
    private String subscribeKey;

    @Value("${pubnub.secret.key}")
    private String secretKey;


    @Bean
    PubNub pubNub() throws PubNubException {
        PNConfiguration pnConfiguration = new PNConfiguration(new UserId("harshit@example.com"));
        pnConfiguration.setSubscribeKey(subscribeKey);
        pnConfiguration.setPublishKey(publishKey);
        pnConfiguration.setSecretKey(secretKey);
        pnConfiguration.setLogVerbosity(PNLogVerbosity.BODY);
        return new PubNub(pnConfiguration);
    }
}
