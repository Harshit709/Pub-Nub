package com.pubnub.in.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.in.dto.PublisherDto;
import com.pubnub.in.entity.Message;
import com.pubnub.in.repository.MessageRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class MessageServices {

    @Autowired
    private PubNub pubNub;

    @Autowired
    private  MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;
    public List<PublisherDto> getAllMessage(String channel) {
        CompletableFuture<List<PublisherDto>> future = new CompletableFuture<>();

        try {

            pubNub.history()
                    .channel(channel)
                    .count(100)
                    .async(new PNCallback<PNHistoryResult>() {

                        @Override
                        public void onResponse(@Nullable PNHistoryResult result, @NotNull PNStatus status) {
                            if (status.isError()) {
                                future.completeExceptionally(new RuntimeException("Failed to Fetch Message: " + status.getErrorData().toString()));
                            } else {
                                List<PublisherDto> message = result.getMessages().stream()
                                        .map(PNHistoryItemResult::getEntry)
                                        .map(Object::toString)
                                        .map(this::convertToPublisherDto)
                                        .collect(Collectors.toList());

                                future.complete(message);

                            }
                        }
                        private PublisherDto convertToPublisherDto(Object entry) {
                            try {
                                if (entry instanceof String) {
                                    return objectMapper.readValue((String) entry, PublisherDto.class);
                                }

                                // Convert object to JSON string and then to PublisherDto
                                String json = objectMapper.writeValueAsString(entry);
                                return objectMapper.readValue(json, PublisherDto.class);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to Convert Message to PublisherDto", e);
                            }
                        }
                    });
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to Connect with PubNub");
        }
    }
    public List<Message> getMessage(String senderId, String receiverId) {
        return messageRepository.findAllBySenderIdAndReceiverId(senderId, receiverId);
    }
}
