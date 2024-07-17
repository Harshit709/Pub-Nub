package com.pubnub.in.Pub_Nub.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PublishDto {

    private String message;
    private String senderId;
    private String receiverId;
    private String channel;
}
