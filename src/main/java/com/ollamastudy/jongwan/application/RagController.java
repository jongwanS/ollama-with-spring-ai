package com.ollamastudy.jongwan.application;

import com.ollamastudy.jongwan.config.DocumentLoader;
import com.ollamastudy.jongwan.service.RedisVectorStoreService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
public class RagController {

    private final DocumentLoader documentLoader;
    private final RedisVectorStoreService service;

    @PostConstruct
    public void init() throws IOException {
        String text = Files.readString(Path.of("src/main/resources/example.txt"));
        documentLoader.loadFromTxt(text);
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String q) {
        String answer = service.ask(q);
        return answer;
    }

    @GetMapping("/pdf-rag")
    public String pdf_rag(@RequestParam String q) throws IOException {

        ClassPathResource resource = new ClassPathResource("pdf/System Design Interview.pdf");
        byte[] pdfBytes = resource.getInputStream().readAllBytes();

        // 병렬 처리
        documentLoader.loadFromPdfParallel(pdfBytes);

        return "ok";
    }
}
