package com.ollamastudy.jongwan.application;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/chat")
public class ChatController {

    private final VectorStore vectorStore;

    private final ChatClient chatClient;

    private final PromptTemplate template;

    public ChatController(
            VectorStore vectorStore
            , @Qualifier("meanChatClient") ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
        this.template = new PromptTemplate(new ClassPathResource("prompts/english-tutor-friend.st")); // 파일 경로
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

    @PostMapping("/eng-tutor")
    public ResponseEntity<String> askTutor(@RequestBody String message) {

        // 1. 템플릿 경로 기반 PromptTemplate 생성
        PromptTemplate template = new PromptTemplate(
                new ClassPathResource("prompts/english-tutor.st") // 파일 경로
        );

        // 2. userInput 바인딩
        String renderedPrompt = template.render(Map.of("userInput", message));

        // 3. 렌더링된 프롬프트로 메시지 생성 후 요청
        ChatResponse chatResponse = chatClient.prompt()
                .user(renderedPrompt)
                .call()
                .chatResponse();

        String res = chatResponse.getResult().getOutput().getText();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/eng-friend-tutor")
    public ResponseEntity<String> friendTutor(@RequestBody String message) {

        // 1. 템플릿 경로 기반 PromptTemplate 생성
//        PromptTemplate template = new PromptTemplate(
//                new ClassPathResource("prompts/english-tutor-friend.st") // 파일 경로
//        );

        // 2. userInput 바인딩
        String renderedPrompt = template.render(Map.of("userInput", message));

        // 3. 렌더링된 프롬프트로 메시지 생성 후 요청
        ChatResponse chatResponse = chatClient.prompt()
                .user(renderedPrompt)
                .call()
                .chatResponse();

        String res = chatResponse.getResult().getOutput().getText();
        return ResponseEntity.ok(res);
    }
}