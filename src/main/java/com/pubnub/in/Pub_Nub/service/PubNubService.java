package com.pubnub.in.Pub_Nub.service;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.in.Pub_Nub.entity.PublishDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PubNubService {
    @Autowired
    private PubNub pubNub;

    public CompletableFuture<PublishDto> publishMessage(PublishDto publisherDto) {
        CompletableFuture<PublishDto> future = new CompletableFuture();

        pubNub.
                publish()
                .channel(publisherDto.getChannel())
                .message(publisherDto)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            if (status.getStatusCode() == 403) {
                                System.out.println("Failed to publish message: Forbidden access to channel.");
                            } else {
                                System.out.println("Failed to publish message: " + status.getErrorData().getInformation());
                            }
                        } else {
                            System.out.println("Message published successfully! Timetoken: " + result.getTimetoken());
                            future.complete(publisherDto);
                        }
                    }
                });
        return future;
    }

}
