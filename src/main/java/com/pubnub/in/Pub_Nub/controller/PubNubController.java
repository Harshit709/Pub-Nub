package com.pubnub.in.Pub_Nub.controller;

import com.pubnub.in.Pub_Nub.entity.PublishDto;
import com.pubnub.in.Pub_Nub.service.PubNubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/pubnub")
public class PubNubController {

    @Autowired
    private PubNubService pubNubService;


    @PostMapping("/publish")
    public PublishDto pubNubPublish(@RequestBody PublishDto publisherDto) {
        try {
            return pubNubService.publishMessage(publisherDto).get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Failed to publish message: " + e.getMessage());
        }
        return null;
    }
}
