package com.ollamastudy.jongwan;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(@Qualifier("meanChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody String message) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();

        String res = chatResponse.getResult().getOutput().getText();

        return ResponseEntity.ok(res);

    }
}