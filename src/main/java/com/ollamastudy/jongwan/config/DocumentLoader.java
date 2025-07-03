package com.ollamastudy.jongwan.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class DocumentLoader {

    private final RedisVectorStore vectorStore;

    public void loadFromTxt(String text) {
        List<Document> documents = Arrays.stream(text.split("(?<=[.!?])\\s+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(sentence -> new Document(sentence, Map.of("source", "example.txt")))
                .toList();
        vectorStore.accept(documents);
        //extracted(documents);
    }

    @Deprecated
    private void extracted(List<Document> documents) {
        List<Document> documentsd = Arrays.asList(
                new Document("Grammar helps you structure sentences correctly."),
                new Document("Vocabulary building is important."),
                new Document("Watching English movies helps improve listening.")
        );
        vectorStore.accept(documentsd); // 저장

        //List<Document> result = vectorStore.similaritySearch("Why is grammar important?");
        //How To improve your English
        List<Document> result = vectorStore.similaritySearch("How To improve your English");
        log.info("검색된 문서 수: {}", result.size());
        for (Document doc : result) {
            log.info("유사 문서: {}", doc.getText());
        }
    }

    public void loadFromPdf(Path pdfPath) throws IOException {
        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            String text = new PDFTextStripper().getText(document);
            loadFromTxt(text);
        }
    }
}