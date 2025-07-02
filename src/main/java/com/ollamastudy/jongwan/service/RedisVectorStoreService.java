package com.ollamastudy.jongwan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RedisVectorStoreService {

    private final RedisVectorStore vectorStore;
    private final ChatClient chatClient;

    public RedisVectorStoreService(RedisVectorStore vectorStore, @Qualifier("englishTeacherChat") ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    public String ask(String query) {
        List<Document> relevantDocs = vectorStore.similaritySearch(query);
        String context = relevantDocs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));

        String prompt = "다음 정보를 바탕으로 질문에 답해주세요:\n" +
                context + "\n\n질문: " + query;

        return chatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse().getResult().getOutput().getText();
    }
}