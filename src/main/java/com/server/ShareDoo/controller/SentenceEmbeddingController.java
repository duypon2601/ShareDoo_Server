package com.server.ShareDoo.controller;

import com.server.ShareDoo.service.SentenceEmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/embeddings")
@RequiredArgsConstructor
@Tag(name = "Sentence Embeddings", description = "APIs for sentence embeddings and similarity calculations")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class SentenceEmbeddingController {

    private final SentenceEmbeddingService sentenceEmbeddingService;

    @PostMapping("/embedding")
    @Operation(summary = "Get embedding for text")
    public ResponseEntity<List<Float>> getEmbedding(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        double[] embedding = sentenceEmbeddingService.getEmbedding(text);
        return ResponseEntity.ok(Arrays.stream(embedding).boxed().map(Double::floatValue).collect(Collectors.toList()));
    }

    @PostMapping("/similarity")
    @Operation(summary = "Calculate similarity between two texts")
    public ResponseEntity<Map<String, Object>> calculateSimilarity(@RequestBody Map<String, String> request) {
        String text1 = request.get("text1");
        String text2 = request.get("text2");
        double similarity = sentenceEmbeddingService.calculateSimilarity(text1, text2);
        return ResponseEntity.ok(Map.of("similarity", similarity));
    }
} 