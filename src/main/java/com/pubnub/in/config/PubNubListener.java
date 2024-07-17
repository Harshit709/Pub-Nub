package com.pubnub.in.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import com.pubnub.in.entity.Message;
import com.pubnub.in.repository.MessageRepository;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PubNubListener {

    private final PubNub pubNub;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageRepository messageRepository;

    private Set<String> onlineUsers = ConcurrentHashMap.newKeySet(); // Thread-safe set to track online users

    @Autowired
    public PubNubListener(PubNub pubNub) {
        this.pubNub = pubNub;
    }

    @PostConstruct
    public void init() {
        subscribeToChannel();
    }


    public void subscribeToChannel() {
        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(@NotNull PubNub pubnub, @NotNull PNStatus pnStatus) {
                if (pnStatus.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    System.out.println("Connected to PubNub");
                } else if (pnStatus.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    System.out.println("Disconnected from PubNub");
                }
            }


            @Override
            public void message(@NotNull PubNub pubnub, @NotNull PNMessageResult pnMessageResult) {
                System.out.println("Received message: " + pnMessageResult.getMessage());
                try {

                    JsonObject messageJsonObject = pnMessageResult.getMessage().getAsJsonObject();

                    String jsonString = messageJsonObject.toString();

                    Message message = objectMapper.readValue(jsonString, Message.class);

                    System.out.println(message);

                    messageRepository.save(message);

                } catch (Exception e) {
                    throw new RuntimeException("Failed to Convert Message to Message Entity", e);
                }
            }


            @Override
            public void presence(@NotNull PubNub pubnub, @NotNull PNPresenceEventResult pnPresenceEventResult) {
                String uuid = pnPresenceEventResult.getUuid();
                switch (pnPresenceEventResult.getEvent()) {
                    case "join":
                        onlineUsers.add(uuid);
                        System.out.println("User joined: " + uuid);
                        break;
                    case "leave":
                    case "timeout":
                        onlineUsers.remove(uuid);
                        System.out.println("User left: " + uuid);
                        break;
                }
                System.out.println("Current online users: " + onlineUsers);
            }


            // Implement other callback methods if needed
            @Override
            public void signal(@NotNull PubNub pubnub, @NotNull PNSignalResult pnSignalResult) {
                // Handle signal events
            }


            @Override
            public void uuid(@NotNull PubNub pubnub, @NotNull PNUUIDMetadataResult pnUUIDMetadataResult) {
                // Handle UUID metadata events
            }


            @Override
            public void channel(@NotNull PubNub pubnub, @NotNull PNChannelMetadataResult pnChannelMetadataResult) {
                // Handle channel metadata events
            }


            @Override
            public void membership(@NotNull PubNub pubnub, @NotNull PNMembershipResult pnMembershipResult) {
                // Handle membership events
            }


            @Override
            public void messageAction(@NotNull PubNub pubnub, @NotNull PNMessageActionResult pnMessageActionResult) {
                // Handle message action events
            }


            @Override
            public void file(@NotNull PubNub pubnub, @NotNull PNFileEventResult pnFileEventResult) {
                // Handle file events
            }
        });


        // Subscribe to a channel with presence
        pubNub.subscribe()
                .channels(Arrays.asList("Chatting")) // List of channels to subscribe to
                .withPresence() // Enable presence on the channel
                .execute();
    }
}
