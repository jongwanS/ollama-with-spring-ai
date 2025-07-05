package com.ollamastudy.jongwan.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    public void loadFromPdf(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            String text = new PDFTextStripper().getText(document);
            loadFromTxt(text, "System Design Interview.pdf");
        }
    }

    public void loadFromTxt(String text, String sourceName) {
        List<Document> documents = Arrays.stream(text.split("(?<=[.!?])\\s+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(sentence -> new Document(sentence, Map.of("source", sourceName)))
                .toList();

        vectorStore.accept(documents);
    }

    public void loadFromPdfParallel(byte[] pdfBytes) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            int pageCount = document.getNumberOfPages();
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // CPU 개수 고려

            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < pageCount; i++) {
                final int pageIndex = i;
                futures.add(executor.submit(() -> {
                    try (PDDocument singlePageDoc = new PDDocument()) {
                        singlePageDoc.addPage(document.getPage(pageIndex));

                        PDFTextStripper stripper = new PDFTextStripper();
                        stripper.setStartPage(1);
                        stripper.setEndPage(1);

                        String text = stripper.getText(singlePageDoc);
                        loadFromTxt(text, "System Design Interview.pdf - Page " + (pageIndex + 1));
                    }
                    return null;
                }));
            }

            // 대기
            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            executor.shutdown();
        }
    }

}