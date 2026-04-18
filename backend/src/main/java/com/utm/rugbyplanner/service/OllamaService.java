package com.utm.rugbyplanner.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * OllamaService — HTTP client for Ollama's local REST API.
 *
 * Ollama must be running locally:
 *   $ ollama serve
 *   $ ollama pull llama3.2
 *
 * Endpoint used: POST http://localhost:11434/api/generate
 *
 * Request body:
 * {
 *   "model": "llama3.2",
 *   "prompt": "...",
 *   "stream": false
 * }
 *
 * Response body:
 * {
 *   "model": "llama3.2",
 *   "response": "...",
 *   "done": true
 * }
 */
@Slf4j
@Service
public class OllamaService {

    private final RestTemplate restTemplate;

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public OllamaService(
            @Value("${ollama.timeout-seconds:120}") long timeoutSeconds,
            RestTemplateBuilder builder) {
        // Ollama can take a while to generate — use a generous timeout
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }

    /**
     * Send a prompt to Ollama and return the generated text.
     *
     * @param prompt  The full prompt to send to the model.
     * @return        The model's text response.
     * @throws RuntimeException if Ollama is not reachable or returns an error.
     */
    public String generate(String prompt) {
        String url = ollamaApiUrl + "/api/generate";

        OllamaRequest request = new OllamaRequest(ollamaModel, prompt, false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OllamaRequest> entity = new HttpEntity<>(request, headers);

        log.debug("Calling Ollama at {} with model={}", url, ollamaModel);

        try {
            ResponseEntity<OllamaResponse> response =
                    restTemplate.postForEntity(url, entity, OllamaResponse.class);

            if (response.getStatusCode() == HttpStatus.OK
                    && response.getBody() != null
                    && response.getBody().getResponse() != null) {

                String result = response.getBody().getResponse().trim();
                log.debug("Ollama response length: {} chars", result.length());
                return result;
            }

            throw new RuntimeException("Ollama returned an empty response.");

        } catch (ResourceAccessException e) {
            log.error("Cannot reach Ollama at {}: {}", url, e.getMessage());
            throw new RuntimeException(
                    "AI engine is not available. Please make sure Ollama is running " +
                    "(`ollama serve`) and the model is pulled (`ollama pull " + ollamaModel + "`).",
                    e
            );
        }
    }

    // ── Internal DTOs ─────────────────────────────────────────────────────────

    @Data
    @AllArgsConstructor
    private static class OllamaRequest {
        private String  model;
        private String  prompt;
        private boolean stream;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OllamaResponse {
        private String  model;
        private String  response;
        @JsonProperty("done")
        private boolean done;
    }
}
