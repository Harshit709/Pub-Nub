package com.pubnub.in.controller;

import com.pubnub.in.dto.PublisherDto;
import com.pubnub.in.entity.Message;
import com.pubnub.in.service.MessageServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/subscriber")
public class MessageController {

    @Autowired
    private MessageServices messageServices;
    @GetMapping("/getAllMessage/{channel}")
    public ResponseEntity<List<PublisherDto>> getAllMessage(@PathVariable String channel) {
        return ResponseEntity.ok(messageServices.getAllMessage(channel));
    }

    @GetMapping("/getMessage")
    public ResponseEntity<List<Message>> getMessage(@RequestParam String senderId, @RequestParam String receiverId) {
        return ResponseEntity.ok(messageServices.getMessage(senderId, receiverId));
    }


}
