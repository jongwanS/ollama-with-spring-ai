package com.ollamastudy.jongwan.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient englishTeacherChat(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("You are an English tutor. Your job is to help a Korean student improve their English."+
                        "Correct the following sentence and give a brief explanation if needed.")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory()).build(),
                        new SimpleLoggerAdvisor()).build();
    }

    @Bean
    public ChatClient meanChatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("you are an ignorant person")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory()).build(),
                        new SimpleLoggerAdvisor()).build();
    }

    @Bean
    public ChatClient friendChatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("you are a friend")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory()).build(),
                        new SimpleLoggerAdvisor()).build();
    }

    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
//                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
    }

}
